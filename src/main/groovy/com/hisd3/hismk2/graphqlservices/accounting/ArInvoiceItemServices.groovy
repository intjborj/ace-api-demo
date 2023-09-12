package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.ArCreditNoteItems
import com.hisd3.hismk2.domain.accounting.ArCustomers
import com.hisd3.hismk2.domain.accounting.ArInvoice
import com.hisd3.hismk2.domain.accounting.ArInvoiceItems
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.billing.Investor
import com.hisd3.hismk2.domain.fixed_assets.FixedAssetItem
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.hrm.EmployeeService
import com.hisd3.hismk2.graphqlservices.inventory.SupplierService
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.rest.dto.ARInvoiceDto
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.query.NativeQuery
import org.hibernate.transform.Transformers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

import javax.persistence.Column
import javax.persistence.EntityManager
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.transaction.Transactional
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
@GraphQLApi
@Transactional(rollbackOn = Exception.class)
class ArInvoiceItemServices extends  ArAbstractFormulaHelper<ArInvoiceItems> {

    ArInvoiceItemServices(){
        super(ArInvoiceItems.class)
    }


    @Autowired
    GeneratorService generatorService

    @Autowired
    ArCustomerServices arCustomerServices

    @Autowired
    BillingItemServices billingItemServices

    @Autowired
    ArInvoiceServices invoiceServices

    @Autowired
    EmployeeService employeeService

    @Autowired
    EntityManager entityManager

    @Autowired
    ArCreditNoteItemServices arCreditNoteItemServices

    @Autowired
    EntityObjectMapperService entityObjectMapperService

    @GraphQLQuery(name="findOneInvoiceItems")
    ArInvoiceItems findOneInvoiceItems(
            @GraphQLArgument(name = "id") UUID id
    ){
        try{
            if(id) findOne(id)
            else return null
        }
        catch (ignored) {
            return null
        }
    }

    @GraphQLMutation(name="addInvoiceItem")
    GraphQLResVal<ArInvoiceItems> addInvoiceItem(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String,Object> fields
    ){
        try{
            def invoiceItems = upsertFromMap(id, fields, { ArInvoiceItems entity, boolean forInsert ->
                if (forInsert) {
                    if(!entity.recordNo)
                        entity.recordNo = generatorService.getNextValue(GeneratorType.AR_INVOICE_ITEMS, {
                            return StringUtils.leftPad(it.toString(), 6, "0")
                        })
                    return entity
                }
                else {
                    return entity
                }
            })

            ArInvoice arInvoice = invoiceItems.arInvoice
            if((invoiceItems.vatAmount <= 0 && arInvoice.isVatable) || (invoiceItems.cwtAmount <= 0 && arInvoice.isCWT) )
                applyVatCWT(invoiceItems.id,arInvoice.isVatable,arInvoice.isCWT,null,null)
            invoiceServices.updateInvoiceTotals(invoiceItems.arInvoice.id)

            return new GraphQLResVal<ArInvoiceItems>(invoiceItems, true, 'Invoice item has been successfully saved. ')
        }
        catch (ignore){
            return new GraphQLResVal<ArInvoiceItems>(null, false, 'Unable to save invoice item. Please contact support for assistance.')
        }
    }

    @GraphQLMutation(name="addInvoiceClaimsItem")
    GraphQLResVal<ArInvoiceItems> addInvoiceClaimsItem(
            @GraphQLArgument(name = "billingItemId") UUID billingItemId,
            @GraphQLArgument(name = "invoiceId") UUID invoiceId
    ){
        try{
            if(billingItemId) {
                def billingItem = billingItemServices.findOne(billingItemId)
                def invoice = invoiceServices.findOne(invoiceId)
                if(billingItem && invoice) {
                    ArInvoiceItems invoiceItems = new ArInvoiceItems()
                    invoiceItems.arInvoice = invoice
                    invoiceItems.invoiceNo = invoice.invoiceNo
                    invoiceItems.arCustomer = invoice.arCustomer
                    invoiceItems.recordNo = generatorService.getNextValue(GeneratorType.AR_INVOICE_ITEMS, {
                        return StringUtils.leftPad(it.toString(), 6, "0")
                    })
                    invoiceItems.itemName = billingItem.billing.patient.fullName
                    invoiceItems.description = billingItem.description
                    invoiceItems.approval_code = billingItem?.approvalCode ?: ''
                    invoiceItems.itemType = billingItem.itemType.name() == 'DEDUCTIONS'  ? 'HCI' : 'PF'
                    invoiceItems.unitPrice = billingItem.credit
                    invoiceItems.quantity = 1
                    invoiceItems.discount = 0
                    invoiceItems.discountAmount = 0.00
                    invoiceItems.isCWT = invoice.isCWT
                    invoiceItems.isVatable = invoice.isVatable
                    if(invoiceItems.itemType.equalsIgnoreCase('HCI')){
                        invoiceItems.totalHCIAmount =  billingItem.credit
                    }
                    if(invoiceItems.itemType.equalsIgnoreCase('PF')) {
                        invoiceItems.totalPFAmount = billingItem.credit
                    }

                    invoiceItems.totalAmountDue = billingItem.credit
                    invoiceItems.claimsItem = true
                    invoiceItems.patient_name = invoiceItems.itemName
                    invoiceItems.billing_no = billingItem.billing.billingNo
                    invoiceItems.soa_no =  "${DateTimeFormatter.ofPattern(" yyyy ").withZone(ZoneId.systemDefault()).format(billingItem.billing.createdDate)}-${billingItem.billing.billingNo}"
                    invoiceItems.admission_date = billingItem?.billing?.patientCase?.admissionDatetime ? Date.from(billingItem.billing.patientCase.admissionDatetime) : Date.from(billingItem.transactionDate)
                    invoiceItems.discharge_date = billingItem?.billing?.patientCase?.dischargedDatetime ?  Date.from(billingItem.billing.patientCase.dischargedDatetime) : Date.from(billingItem.transactionDate)
                    invoiceItems.registry_type = billingItem.billing.patientCase.registryType
                    invoiceItems.billing_item_id = billingItem.id
                    invoiceItems.billing_id = billingItem.billing.id
                    invoiceItems.patient_id = billingItem.billing.patient.id
                    invoiceItems.case_id = billingItem.billing.patientCase.id
                    invoiceItems.status = 'active'

                    if(invoiceItems.isVatable)
                        invoiceItems = applyVatLocal(invoiceItems,null)

                    if(invoiceItems.isCWT)
                        invoiceItems = applyCWTLocal(invoiceItems,null)

                    if(billingItem.itemType.name().equalsIgnoreCase('DEDUCTIONSPF') && billingItem.details['PF_EMPLOYEEID']){
                        def pf = employeeService.findById(UUID.fromString(billingItem.details['PF_EMPLOYEEID']))
                        if(pf) {
                            invoiceItems.pf_name = pf.fullName
                            invoiceItems.pf_id = pf.id
                        }
                    }

                    ArInvoiceItems created = save(invoiceItems)
                    invoice.invoiceType = 'claims'
                    invoiceServices.save(invoice)
                    invoiceServices.updateInvoiceTotals(created.arInvoice.id)
                    if(created){
                        billingItem.arBilled = true
                        billingItemServices.save(billingItem)
                    }

                    return new GraphQLResVal<ArInvoiceItems>(created, true, 'Invoice item has been successfully saved. ')
                }
            }
            return new GraphQLResVal<ArInvoiceItems>(null, false, 'Unable to save invoice item. Please contact support for assistance.')
        }
        catch (ignore){
            return new GraphQLResVal<ArInvoiceItems>(null, false, 'Unable to save invoice item. Please contact support for assistance.')
        }
    }

    @GraphQLMutation(name="addTransferItem")
    GraphQLResVal<ArInvoiceItems> addTransferItem(
            @GraphQLArgument(name = "creditNoteItemId") UUID creditNoteItemId,
            @GraphQLArgument(name = "invoiceId") UUID invoiceId
    ){
        try{
            if(creditNoteItemId) {
                ArCreditNoteItems creditNoteItems = arCreditNoteItemServices.findOne(creditNoteItemId)
                def invoice = invoiceServices.findOne(invoiceId)
                if(creditNoteItems && invoice) {
                    ArInvoiceItems invoiceItems =  objectMapper.convertValue(creditNoteItems.arInvoiceItem, ArInvoiceItems)
                    invoiceItems.id = null
                    invoiceItems.arInvoice = invoice
                    invoiceItems.invoiceNo = invoice.invoiceNo
                    invoiceItems.arCustomer = invoice.arCustomer
                    invoiceItems.recordNo = generatorService.getNextValue(GeneratorType.AR_INVOICE_ITEMS, {
                        return StringUtils.leftPad(it.toString(), 6, "0")
                    })
                    invoiceItems.unitPrice = creditNoteItems.totalAmountDue
                    invoiceItems.quantity = 1
                    invoiceItems.discount = 0
                    invoiceItems.discountAmount = 0.00
                    invoiceItems.isCWT = invoice.isCWT
                    invoiceItems.isVatable = invoice.isVatable
                    invoiceItems.totalAmountDue = creditNoteItems.totalAmountDue

                    if(invoiceItems.itemType.equalsIgnoreCase('HCI')){
                        invoiceItems.totalHCIAmount =  invoiceItems.totalAmountDue
                    }
                    if(invoiceItems.itemType.equalsIgnoreCase('PF')) {
                        invoiceItems.totalPFAmount = invoiceItems.totalAmountDue
                    }
                    invoiceItems.claimsItem = true
                    invoiceItems.status = 'active'
                    invoiceItems.reference_transfer_id = creditNoteItems.id

                    if(invoiceItems.isVatable)
                        invoiceItems = applyVatLocal(invoiceItems,null)

                    if(invoiceItems.isCWT)
                        invoiceItems = applyCWTLocal(invoiceItems,null)

                    ArInvoiceItems created = save(invoiceItems)
                    if(created) {
                        creditNoteItems.recipientInvoice = created.id
                        arCreditNoteItemServices.save(creditNoteItems)
                    }
                    invoice.invoiceType = 'claims'
                    invoiceServices.save(invoice)
                    invoiceServices.updateInvoiceTotals(created.arInvoice.id)

                    return new GraphQLResVal<ArInvoiceItems>(created, true, 'Invoice item has been successfully saved. ')
                }
            }
            return new GraphQLResVal<ArInvoiceItems>(null, false, 'Unable to save invoice item. Please contact support for assistance.')
        }
        catch (ignore){
            return new GraphQLResVal<ArInvoiceItems>(null, false, 'Unable to save invoice item. Please contact support for assistance.')
        }
    }


    @GraphQLMutation(name="removeInvoiceItem")
    GraphQLResVal<ArInvoiceItems> removeInvoiceItem(
            @GraphQLArgument(name = "id") UUID id
    ) {
        try{
            def invoiceItem = findOne(id)
            def invoiceId = invoiceItem.arInvoice.id
            if(invoiceItem){
                if(invoiceItem.claimsItem){
                    def billingItem = billingItemServices.findOne(invoiceItem.billing_item_id)
                    billingItem.arBilled = false
                    billingItemServices.save(billingItem)
                }
                if(invoiceItem.reference_transfer_id){
                    def creditNoteItems = arCreditNoteItemServices.findOne(invoiceItem.reference_transfer_id)
                    if(creditNoteItems) {
                        creditNoteItems.recipientInvoice = null
                        arCreditNoteItemServices.save(creditNoteItems)
                    }
                }
                deleteById(id)
                invoiceServices.updateInvoiceTotals(invoiceId)
                return new GraphQLResVal<ArInvoiceItems>(invoiceItem, true, 'Invoice item has been successfully removed. ')
            }
            return new GraphQLResVal<ArInvoiceItems>(null, false, 'Unable to removed invoice item. Please contact support for assistance.')
        }catch (ignore){
            return new GraphQLResVal<ArInvoiceItems>(null, false, 'Unable to removed invoice item. Please contact support for assistance.')
        }
    }

    @GraphQLQuery(name="findInvoiceItemsByInvoice")
    Page<ArInvoiceItems> findInvoiceItemsByInvoice(
            @GraphQLArgument(name = "invoiceId") UUID invoiceId,
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ){
        if(invoiceId) {
            String queryStr = """   from ArInvoiceItems c where c.arInvoice.id = :invoiceId 
                                and ( 
                                        lower(c.itemName) like lower(concat('%',:search,'%')) or 
                                        lower(c.description) like lower(concat('%',:search,'%'))
                                ) """
            Map<String, Object> params = [:]
            params['invoiceId'] = invoiceId
            params['search'] = search

            getPageable(
                    """ Select c ${queryStr} order by c.recordNo desc""",
                    """ Select count(c) ${queryStr} """,
                    page,
                    size,
                    params
            )
        }
        else Page.empty()
    }

    @GraphQLQuery(name="findInvoiceItemsByCustomer")
    Page<ArInvoiceItems> findInvoiceItemsByCustomer(
            @GraphQLArgument(name = "customerId") UUID customerId,
            @GraphQLArgument(name = "invoiceId") UUID invoiceId,
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "status") String status,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ){
        try {
            String queryStr = """   from ArInvoiceItems c where c.arCustomer.id = :customerId 
                                and ( 
                                        lower(c.itemName) like lower(concat('%',:search,'%')) or 
                                        lower(c.description) like lower(concat('%',:search,'%'))
                                ) """
            Map<String, Object> params = [:]
            params['customerId'] = customerId
            params['search'] = search

            if (invoiceId) {
                params['invoiceId'] = invoiceId
                queryStr += """ and c.arInvoice.id = :invoiceId  """
            }

            if (status) {
                queryStr += "and c.arInvoice.status = :status "
                params['status'] = status
            } else queryStr += "and c.arInvoice.status != 'New' "

            getPageable(
                    """ Select c ${queryStr} order by c.recordNo""",
                    """ Select count(c) ${queryStr} """,
                    page,
                    size,
                    params
            )
        }
        catch (ignore){
            return  Page.empty()
        }
    }

    @GraphQLQuery(name="arInvoiceItemUUIDList")
    List<UUID> arInvoiceItemUUIDList(
            @GraphQLArgument(name = "id") UUID id
    ){
        try{
            return entityManager.createQuery(""" Select c.id from ArInvoiceItems c where c.arInvoice.id = :id """)
                    .setParameter('id',id)
                    .resultList
        }
        catch (ignored){
            return []
        }
    }

    @GraphQLQuery(name="hciInvoiceItemTotalVat")
    BigDecimal hciInvoiceItemTotalVat(
            @GraphQLArgument(name = "id") UUID id
    ){
        try{
            return entityManager.createQuery(""" 
                        Select 
                            coalesce(sum(i.vatAmount),0)
                            from ArInvoiceItems i where i.arInvoice.id = :id
                            and i.totalHCIAmount > 0
            """,BigDecimal.class)
                    .setParameter('id',id)
                    .getSingleResult()
        }
        catch (ignored){
            return 0
        }
    }

    @GraphQLQuery(name="hciInvoiceItemTotalCWT")
    BigDecimal hciInvoiceItemTotalCWT(
            @GraphQLArgument(name = "id") UUID id
    ){
        try{
            return entityManager.createQuery(""" 
                        Select 
                            coalesce(sum(i.cwtAmount),0)
                            from ArInvoiceItems i where i.arInvoice.id = :id
                            and i.totalHCIAmount > 0
            """,BigDecimal.class)
                    .setParameter('id',id)
                    .getSingleResult()
        }
        catch (ignored){
            return 0
        }
    }



    @Transactional
    @GraphQLMutation(name = "generateInvoiceTax")
    GraphQLResVal<Boolean> generateInvoiceTax(
            @GraphQLArgument(name = "invoiceId") UUID invoiceId,
            @GraphQLArgument(name = "taxType") String taxType,
            @GraphQLArgument(name = "isApply") Boolean isApply = true
    ) {
        try{
            switch (taxType.toUpperCase()){
                case 'CWT':
                    ArInvoice invoice = invoiceServices.findOne(invoiceId)
                    List<UUID> items = arInvoiceItemUUIDList(invoiceId)
                    items.each {it->
                        if(invoice.isVatable)
                            applyVatCWT(it,invoice.isVatable,isApply,null,null)
                        else
                            applyCWT(it,isApply,null)
                    }
                    invoice.isCWT = isApply
                    invoiceServices.save(invoice)
                    invoiceServices.updateInvoiceTotals(invoiceId)
                    return new GraphQLResVal<Boolean>(true, true, 'Tax calculation was successful. Your invoice now reflects the updated total amount due.')
                default:
                    return  new GraphQLResVal<Boolean>(false, false, 'Transaction failed: Calculation error. Please check your input and try again.')
            }
        }catch(ignored){
            return  new GraphQLResVal<Boolean>(false, false, 'Transaction failed: Calculation error. Please check your input and try again.')
        }
    }

    @Transactional
    @GraphQLMutation(name = "generateInvoiceVat")
    GraphQLResVal<Boolean> generateInvoiceVat(
            @GraphQLArgument(name = "invoiceId") UUID invoiceId,
            @GraphQLArgument(name = "isVatable") Boolean isVatable
    ) {
        try{
            ArInvoice invoice = invoiceServices.findOne(invoiceId)
            List<UUID> items = arInvoiceItemUUIDList(invoiceId)
            items.each {it->
                // Vat Items
                if(invoice.isCWT)
                    applyVatCWT(it,isVatable,true,null,null)
                else
                    applyVat(it,isVatable,null)
            }
            // Vat Invoice
            invoice.isVatable = isVatable
            invoiceServices.save(invoice)
            invoiceServices.updateInvoiceTotals(invoiceId)
            return  new GraphQLResVal<Boolean>(true,true ,'Tax calculation was successful. Your invoice now reflects the updated total amount due.')
        }catch(ignored){
            return  new GraphQLResVal<Boolean>(false, false, 'Transaction failed: Calculation error. Please check your input and try again.')
        }
    }

    @GraphQLQuery(name="getARInvoiceItemPerPatientGroupById")
    List<ARInvoiceDto> getARInvoiceItemPerPatientGroupById(
            @GraphQLArgument(name="id") UUID id
    ){
        def invoiceItems = entityManager.createNativeQuery("""
                select 
				cast(case 
					when aii.admission_date is not null
					then aii.admission_date 
					when aii.discharge_date is not null 
					then aii.discharge_date
				end AS varchar) as "invoiceDate",
				aii.soa_no, 
				aii.patient_name as "itemName", 
				aii.approval_code as "approvalCode", 
				coalesce(sum(aii.total_hci_amount),0) as "totalHCIAmount",
				coalesce(sum(aii.total_pf_amount),0) as "totalPFAmount",
				coalesce(sum(aii.total_amount_due)) as "totalAmountDue" 
				from accounting.ar_invoice_items aii 
				where 
				aii.ar_invoice_id  = CAST(:id AS uuid)
				group by aii.admission_date , aii.discharge_date ,aii.patient_name,  aii.approval_code ,aii.soa_no
                """).setParameter("id", id)
        return invoiceItems.unwrap(NativeQuery.class).setResultTransformer(Transformers.aliasToBean(ARInvoiceDto.class)).getResultList()
    }

}
