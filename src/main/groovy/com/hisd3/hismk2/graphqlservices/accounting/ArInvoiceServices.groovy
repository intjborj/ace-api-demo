package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.AR_INVOICE_FLAG
import com.hisd3.hismk2.domain.accounting.ArInvoice
import com.hisd3.hismk2.domain.accounting.ArTransactionLedger
import com.hisd3.hismk2.domain.accounting.CustomerType
import com.hisd3.hismk2.domain.accounting.HeaderLedger
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.CompanyAccountServices
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

import javax.persistence.EntityManager
import javax.transaction.Transactional
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Service
@GraphQLApi
@Transactional(rollbackOn = Exception.class)
class ArInvoiceServices extends ArAbstractFormulaHelper<ArInvoice> {

    ArInvoiceServices(){
        super(ArInvoice.class)
    }

    @Autowired
    GeneratorService generatorService

    @Autowired
    ArCustomerServices arCustomerServices

    @Autowired
    EntityManager entityManager

    @Autowired
    EntityObjectMapperService entityObjectMapperService

    @Autowired
    IntegrationServices integrationServices

    @Autowired
    LedgerServices ledgerServices

    @Autowired
    CompanyAccountServices companyAccountServices

    @Autowired
    ArTransactionLedgerServices arTransactionLedgerServices

    @Autowired
    ArInvoiceItemServices arInvoiceItemServices

    @GraphQLMutation(name="createInvoice")
    GraphQLResVal<ArInvoice> createInvoice(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String,Object> fields
    ){
            def invoice = upsertFromMap(id, fields)
            if(!invoice.invoiceNo) {
                def formatter = DateTimeFormatter.ofPattern("yyyy")
                String year = invoice.createdDate.atZone(ZoneId.systemDefault()).format(formatter)
                invoice.invoiceNo = generatorService.getNextGeneratorFeatPrefix("ar_invoice_${year}") {
                    it -> return "INV${year}-${StringUtils.leftPad(it.toString(), 6, "0")}"
                }
                save(invoice)
            }

            return new GraphQLResVal<ArInvoice>(invoice, true, "Invoice transaction completed successfully")
    }

    @GraphQLQuery(name="findAllInvoice")
    Page<ArInvoice> findAllInvoice(
            @GraphQLArgument(name = "customerId") UUID customerId,
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size,
            @GraphQLArgument(name = "status") List<String> status
    ){
        String queryStr = """ from ArInvoice c where ( lower(c.invoiceNo) like lower(concat('%',:search,'%')) )
                            """
        Map<String,Object> params = [:]
        params['search'] = search

        if(customerId){
            queryStr += "and c.arCustomer.id = :customerId "
            params['customerId'] = customerId
        }


        if(status) {
            queryStr += "and c.status in :status "
            params['status'] = status
        }

        getPageable(
                """ Select c ${queryStr} order by c.createdDate desc""",
                """ Select count(c) ${queryStr}""",
                page,
                size,
                params
        )
    }

    @GraphQLQuery(name="findOneInvoice")
    ArInvoice findOneInvoice(
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


    @Transactional
    @GraphQLMutation(name="updateInvoiceTotals")
    ArInvoice updateInvoiceTotals(
            @GraphQLArgument(name = "id") UUID id
    ){
            def result = entityManager.createQuery(""" 
                        Select 
                                sum(i.totalAmountDue), 
                                sum(i.creditNote), 
                                sum(i.totalHCIAmount),
                                sum(i.totalPFAmount),
                                sum(i.cwtAmount), 
                                sum(i.vatAmount)
                                 from ArInvoiceItems i where i.arInvoice.id = :id  
            """)
                    .setParameter('id',id)
                    .getSingleResult()
            ArInvoice invoice = findOne(id)
            invoice.totalAmountDue = result[0] as BigDecimal ?: 0.00
            invoice.totalCreditNote = result[1] as BigDecimal ?: 0.00
            invoice.totalHCIAmount = result[2] as BigDecimal ?: 0.00
            invoice.totalPFAmount = result[3] as BigDecimal ?: 0.00
            invoice.cwtAmount = result[4] as BigDecimal ?: 0.00
            invoice.vatAmount = result[5] as BigDecimal ?: 0.00
            save(invoice)

            return invoice

    }

    @GraphQLQuery(name="invoiceItemAmountSum")
    BigDecimal invoiceItemAmountSum(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "itemType") List<String> itemType
    ){
        try{
            return entityManager.createQuery(""" 
                        Select 
                            sum(i.totalAmountDue)
                            from ArInvoiceItems i where i.arInvoice.id = :id
                            and i.itemType in :itemType
            """,BigDecimal.class)
                    .setParameter('id',id)
                    .setParameter('itemType',itemType)
                            .getSingleResult()
        }
        catch (ignored) {
            return 0
        }
    }

    @GraphQLQuery(name="customerBalance")
    BigDecimal customerBalance(
            @GraphQLArgument(name = "customerId") UUID customerId
    ){
        try{
            return entityManager.createQuery(""" 
                        Select 
                                coalesce(sum(i.totalAmountDue),0)
                                 from ArInvoiceItems i where i.arCustomer.customerId = :customerId 
            """, BigDecimal.class)
                    .setParameter('customerId',customerId)
                    .getSingleResult()
        }
        catch (ignored) {
            return null
        }
    }


    @GraphQLMutation(name="createEmptyInvoice")
    GraphQLResVal<ArInvoice> createEmptyInvoice(
            @GraphQLArgument(name = "customerId") UUID customerId=null
    ){
        try {
                ArInvoice arInvoice = new ArInvoice()
                arInvoice.status = 'Draft'
                if(customerId)
                    arInvoice.arCustomer = arCustomerServices.findOne(customerId)
                def created = save(arInvoice)
                return new GraphQLResVal<ArInvoice>(created, true, 'Successfully saved.')

        }catch (ignored) {
            return new GraphQLResVal<ArInvoice>(null, false, 'Unable to save invoice data. Please contact support for assistance.')
        }
    }



    HeaderLedger claimsInvoicePosting(ArInvoice invoice){
            def dateFormat = DateTimeFormatter.ofPattern("yyyy-MM")

            def headerLedger =	integrationServices.generateAutoEntries(invoice){it, nul ->
                def companyAccount = companyAccountServices.findOne(it.arCustomer.referenceId)
                it.totalHCITax = arInvoiceItemServices.hciInvoiceItemTotalCWT(it.id)?: 0.00
                it.totalHCIVat = arInvoiceItemServices.hciInvoiceItemTotalVat(it.id)?: 0.00
                it.totalAmount = (it.totalHCIAmount?:0.00) + (it.totalHCITax?:0.00) + (it.totalHCIVat?:0.00)
                it.negativeTotalAmount = -it.totalAmount
                it.flagValue = AR_INVOICE_FLAG.AR_CLAIMS_INVOICE.name()
                it.companyAccount = companyAccount
            }

            Map<String,String> details = [:]
            invoice.details.each { k,v ->
                details[k] = v
            }

            details["INVOICE_ID"] = invoice.id.toString()


            Date dateTime = invoice.invoiceDate

            def transactionDate
            transactionDate = dateToInstantConverter(dateTime)

            def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
                    "${invoice.arCustomer.customerName}-${transactionDate.atZone(ZoneId.systemDefault()).format(dateFormat)}",
                    "RECEIVABLES - ${invoice.arCustomer.customerName} ${invoice.arCustomer.customerType == CustomerType.PROMISSORY_NOTE ? '(PN)' : '' }",
                    "INV-${invoice.invoiceNo} - CLAIMS INVOICE FOR ${invoice.totalHCIAmount}",
                    LedgerDocType.INV,
                    JournalType.GENERAL,
                    transactionDate,
                    details)
            invoice.ledgerId = pHeader.id
            save(invoice)
            return  pHeader

    }

    HeaderLedger personalInvoicePosting(ArInvoice invoice){
        def yearFormat = DateTimeFormatter.ofPattern("yyyy")

        def headerLedger =	integrationServices.generateAutoEntries(invoice){it, nul ->
            it.flagValue = AR_INVOICE_FLAG.AR_PERSONAL_INVOICE.name()
            it.electricity = invoiceItemAmountSum(it.id,['electricity'])
            it.rental = invoiceItemAmountSum(it.id,['rental'])
            it.others = invoiceItemAmountSum(it.id,['custom','affiliation'])
            it.totalAmount = (it.totalAmountDue?:0.00) + (it.vatAmount?:0.00) + (it.cwtAmount?:0.00)
        }

        Map<String,String> details = [:]
        invoice.details.each { k,v ->
            details[k] = v
        }

        details["INVOICE_ID"] = invoice.id.toString()


        Date dateTime = invoice.invoiceDate

        def transactionDate
        transactionDate = dateToInstantConverter(dateTime)

        def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
                "${transactionDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${invoice.invoiceNo}",
                "RECEIVABLES - ${invoice.arCustomer.customerName}",
                "INV-${invoice.invoiceNo} - REGULAR INVOICE FOR ${invoice.totalAmountDue}",
                LedgerDocType.INV,
                JournalType.GENERAL,
                transactionDate,
                details)
        invoice.ledgerId = pHeader.id
        save(invoice)
        return  pHeader
    }


    @Transactional
    @GraphQLMutation(name = "invoicePosting")
    GraphQLResVal<ArInvoice> invoicePosting(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String,Object> fields,
            @GraphQLArgument(name = "entryPosting") Boolean entryPosting
    ) {
        def invoice  =  createInvoice(id,fields).response
        if(!invoice)
            return  new GraphQLResVal<ArInvoice>(null, false, 'Transaction failed: Calculation error. Please check your input and try again.')

        if(invoice.status.equalsIgnoreCase('pending') && entryPosting) {
            if (invoice.invoiceType.equalsIgnoreCase('claims') && invoice.totalHCIAmount > 0)
                claimsInvoicePosting(invoice)
            if (invoice.invoiceType.equalsIgnoreCase('regular'))
                personalInvoicePosting(invoice)

            arTransactionLedgerServices.insertArInvoiceTransactionLedger(invoice)
        }
        return  new GraphQLResVal<ArInvoice>(invoice, true, 'Invoice transaction completed successfully.')

    }

}
