package com.hisd3.hismk2.graphqlservices.inventory
import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.HeaderLedger
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.accounting.LedgerHeaderDetailParam
import com.hisd3.hismk2.domain.billing.Billing
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.billing.BillingItemDetailParam
import com.hisd3.hismk2.domain.billing.BillingItemStatus
import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.domain.hrm.BiometricDevice
import com.hisd3.hismk2.domain.inventory.DepartmentStockIssue
import com.hisd3.hismk2.domain.inventory.DepartmentStockIssueItems
import com.hisd3.hismk2.domain.inventory.Inventory
import com.hisd3.hismk2.domain.inventory.InventoryLedger
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.StockRequest
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.graphqlservices.accounting.IntegrationServices
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.inventory.DepartmentStockIssueRepository
import com.hisd3.hismk2.repository.inventory.InventoryLedgerRepository
import com.hisd3.hismk2.repository.inventory.InventoryRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.rest.InventoryResource
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorType
import graphql.schema.GraphQLArgument
import groovy.transform.Canonical
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.hibernate.query.NativeQuery
import org.hibernate.transform.Transformers
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.xmlsoap.schemas.soap.encoding.Int

import javax.persistence.EntityManager
import javax.swing.text.DateFormatter
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Canonical
class StockCardTransactionsNSql {
    String key
    String referenceNo
    String documentDesc
    String ledgerDate
    Integer qtyIn
    Integer qtyOut
    Integer adjustment
    String items
    String postedLedger
    String sourceDept
    String destinationDept
}

@Canonical
class StockdCardItems {
    String descLong
    Integer qtyIn
    Integer qtyOut
    Integer adjustment
}

@Canonical
class  CaseIdDto {
    String caseId
}



enum SALES_INTEGRATION{
    IP_MEDS ,
    IP_SUPPLIES,
    IP_SERVICES,
    IP_OXYGEN,
    IP_ROOM,

    OPD_MEDS,
    OPD_SUPPLIES,
    OPD_SERVICES,
    OPD_OXYGEN,
    OPD_ROOM,

    ER_MEDS,
    ER_SUPPLIES,
    ER_SERVICES,
    ER_ROOM,
    ER_OXYGEN,

    OTC_MEDS,
    OTC_SUPPLIES,
    OTC_SERVICES,

    OTC_NONVAT_MEDS,
    OTC_NONVAT_SUPPLIES,
    OTC_NONVAT_SERVICES,

}

@Component
@TypeChecked
@GraphQLApi
class InventoryAccountingIntegrationService {

    @Value('${accounting.enable_costing}')
    Boolean enable_costing

    @Autowired
    IntegrationServices integrationServices

    @Autowired
    DepartmentStockIssueItemService departmentStockIssueItemService

    @Autowired
    LedgerServices ledgerServices

    @Autowired
    EntityManager entityManager

    @Autowired
    BillingItemServices billingItemServices

    @Autowired
    DepartmentRepository departmentRepository

    @Autowired
    ItemRepository itemRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    InventoryResource inventoryResource

    @Autowired
    InventoryLedgerService inventoryLedgerService

    @Autowired
    InventoryLedgerRepository inventoryLedgerRepository

    @Autowired
    BillingService billingService

    @Autowired
    DepartmentStockIssueRepository departmentStockIssueRepository

//    String addJournalEntryForStockIssuance(DepartmentStockIssue departmentStockIssue){
//
//        BigDecimal medicine = 0.00, medicalSupp = 0.00, supplies = 0.00,
//                   negativeMedicinesAmt = 0.00, negativeMedicalSupp = 0.00, negativeSuppliesAmt = 0.00,
//                   expenseMedicines = 0.00, expenseSupplies = 0.00, expenseMedicalSupp = 0.00
//        def yearFormat = DateTimeFormatter.ofPattern("yyyy")
//        List<DepartmentStockIssueItems> departmentStockIssueItems = departmentStockIssueItemService.getStockRequestItemsByIssue(departmentStockIssue.id)
//
//        departmentStockIssueItems.each {
//            it->
//                if(departmentStockIssue.issueType.equalsIgnoreCase('Expense')) {
//                    if (it.item.isMedicine) {
//                        expenseMedicines = expenseMedicines + (it.issueQty * it.unitCost)
//                        negativeMedicinesAmt = negativeMedicinesAmt - (it.issueQty * it.unitCost)
//                    }
//                    else {
//                        if (it.item.item_category.categoryDescription.equalsIgnoreCase('MEDICAL SUPPLY')) {
//                            expenseMedicalSupp = expenseMedicalSupp + (it.issueQty * it.unitCost)
//                            negativeMedicalSupp = negativeMedicalSupp - (it.issueQty * it.unitCost)
//                        }
//                        else {
//                            expenseSupplies = expenseSupplies + (it.issueQty * it.unitCost)
//                            negativeSuppliesAmt = negativeSuppliesAmt - (it.issueQty * it.unitCost)
//                        }
//                    }
//                }
//                else{
//                    if(it.item.isMedicine) {
//                        medicine = medicine + (it.issueQty * it.unitCost)
//                        negativeMedicinesAmt = negativeMedicinesAmt - (it.issueQty * it.unitCost)
//                    }
//                    else{
//                        if(it.item.item_category.categoryDescription.equalsIgnoreCase('MEDICAL SUPPLY')) {
//                            medicalSupp = medicalSupp + (it.issueQty * it.unitCost)
//                            negativeMedicalSupp = negativeMedicalSupp - (it.issueQty * it.unitCost)
//                        }
//                        else {
//                            supplies = supplies + (it.issueQty * it.unitCost)
//                            negativeSuppliesAmt = negativeSuppliesAmt - (it.issueQty * it.unitCost)
//                        }
//                    }
//                }
//
//        }
//
//        if(medicine != 0 || medicalSupp != 0 || supplies != 0 || expenseMedicines != 0 || expenseSupplies != 0 || expenseMedicalSupp != 0) {
//            // JOURNAL ENTRY
//            HeaderLedger headerLedger = integrationServices.generateAutoEntries(departmentStockIssue) { st, mul ->
//                //	Medicines
//                st.medicines = medicine
//                st.negativeMedicinesAmt = negativeMedicinesAmt
//                //	Medical Supplies
//                st.medicalSupp = medicalSupp
//                st.negativeMedicalSupp = negativeMedicalSupp
//                // Supplies
//                st.supplies = supplies
//                st.negativeSuppliesAmt = negativeSuppliesAmt
//                // Expenses
//                st.expenseMedicines = expenseMedicines
//                st.expenseSupplies = expenseSupplies
//                st.expenseMedicalSupp = expenseMedicalSupp
//
//                st.flagValue = INV_INTEGRATION.INV_STOCK_ISSUE.name()
//            }
//
//            Map<String, String> details = [:]
//            departmentStockIssue.details.each { k, v ->
//                details[k] = v
//            }
//
//            details["STOCK_ISSUE_ID"] = departmentStockIssue.id.toString()
//            HeaderLedger postedLedger = ledgerServices.persistHeaderLedger(headerLedger,
//                    "${departmentStockIssue.issueDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${departmentStockIssue.issueNo}",
//                    "${departmentStockIssue.issueNo} - ${departmentStockIssue.issueFrom.departmentName}",
//                    "${departmentStockIssue.issueNo} - ${departmentStockIssue.issueFrom.departmentName} TO ${departmentStockIssue.issueTo.departmentName}",
//                    LedgerDocType.JV,
//                    JournalType.GENERAL,
//                    departmentStockIssue.issueDate,
//                    details
//            )
//
//            return postedLedger.id
//        }
//
//        return null
//
//    }

//    def adjustmentJournalEntryForStockIssuance(DepartmentStockIssue departmentStockIssue, InvStockIssueJMapValues values){
//        def yearFormat = DateTimeFormatter.ofPattern("yyyy")
//
//        if(values.medicine != 0 || values.medicalSupp != 0 || values.supplies != 0 || values.expenseMedicines != 0 || values.expenseSupplies != 0 || values.expenseMedicalSupp != 0) {
//            // JOURNAL ENTRY
//            HeaderLedger headerLedger = integrationServices.generateAutoEntries(departmentStockIssue) { st, mul ->
//                //	Medicines
//                st.medicines = values.medicine
//                st.negativeMedicinesAmt = values.negativeMedicinesAmt
//                //	Medical Supplies
//                st.medicalSupp = values.medicalSupp
//                st.negativeMedicalSupp = values.negativeMedicalSupp
//                // Supplies
//                st.supplies = values.supplies
//                st.negativeSuppliesAmt = values.negativeSuppliesAmt
//                // Expenses
//                st.expenseMedicines = values.expenseMedicines
//                st.expenseSupplies = values.expenseSupplies
//                st.expenseMedicalSupp = values.expenseMedicalSupp
//
//                st.flagValue = INV_INTEGRATION.INV_STOCK_ISSUE.name()
//            }
//
//            Map<String, String> details = [:]
//            departmentStockIssue.details.each { k, v ->
//                details[k] = v
//            }
//
//            details["STOCK_ISSUE_ID"] = departmentStockIssue.id.toString()
//            HeaderLedger postedLedger = ledgerServices.persistHeaderLedger(headerLedger,
//                    "${departmentStockIssue.issueDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${departmentStockIssue.issueNo}",
//                    "${departmentStockIssue.issueNo} - ${departmentStockIssue.issueFrom.departmentName}",
//                    "UPDATE ${departmentStockIssue.issueNo} - ${departmentStockIssue.issueFrom.departmentName} TO ${departmentStockIssue.issueTo.departmentName}",
//                    LedgerDocType.JV,
//                    JournalType.GENERAL,
//                    departmentStockIssue.issueDate,
//                    details
//            )
//
//            return postedLedger
//        }
//    }

    @GraphQLQuery(name="voidStockIssueJournalEntry")
    Boolean voidStockIssueJournalEntry(@GraphQLArgument(name="id") UUID id){
        List<HeaderLedger> headerLedgerList = getStockIssueJournalEntries(id.toString())
        headerLedgerList.each {
            def header = ledgerServices.findOne(it.id)
            ledgerServices.reverseEntries(header)
        }
        return true
    }

    @GraphQLQuery(name="getStockIssueJournalEntries")
    List<HeaderLedger> getStockIssueJournalEntries(@GraphQLArgument(name="id") String id){
        ledgerServices.createQuery("""
            select h from HeaderLedger h where h.details['STOCK_ISSUE_ID'] = :stockIssueId
        """,
            [
                stockIssueId : id
            ] as Map<String, Object>).resultList
    }

    HeaderLedger stockCardJournalEntry(
            BigDecimal medicine,
            BigDecimal medicalSupply,
            BigDecimal supply
    ){
        if(medicine > 0){
            // For medicines

        }
    }

    @GraphQLMutation(name = "syncSRRUnitCost")
    GraphQLRetVal<BigDecimal> syncSRRUnitCost(
            @GraphQLArgument(name="itemId") UUID itemId
    ){
        try {
            List<InventoryLedger> inventoryLedgers = inventoryLedgerRepository.getInvLedgerItemsPerId(itemId)
            BigDecimal unitCost = getSRRLatestUnitCost(itemId.toString()) as BigDecimal
            if(unitCost > 0) {
                inventoryLedgers.each {
                    it ->
                        it.ledgerUnitCost = unitCost as BigDecimal
                }
            }
            return new GraphQLRetVal<BigDecimal>(unitCost,true,"Successfully updated.")
        }
        catch (Exception e){
            return new GraphQLRetVal<BigDecimal>(0.00,true,e.message)
        }
    }

    @GraphQLMutation(name = "updateStockCardItemCost")
    GraphQLRetVal<String> updateStockCardItemCost(
            @GraphQLArgument(name="itemId") UUID itemId,
            @GraphQLArgument(name="unitCost") String unitCost
    ){
        try {
            List<InventoryLedger> inventoryLedgers = inventoryLedgerRepository.getInvLedgerItemsPerId(itemId)
            def a = inventoryLedgers
            inventoryLedgers.each {
                it ->
                it.ledgerUnitCost = unitCost as BigDecimal
                inventoryLedgerRepository.save(it)
            }
            return new GraphQLRetVal<String>("Success",true,"Successfully saved.")
        }
        catch (Exception e){
            return new GraphQLRetVal<String>("Error",true,e.message)
        }
    }

    @GraphQLMutation(name = "processStockTransaction")
    GraphQLRetVal<String> processStockTransaction(){
        try {
            syncStockCardEntries()
            return new GraphQLRetVal<String>("Success", false, "Successfully saved!")
        }
        catch(e){
            return new GraphQLRetVal<String>("Success", false, e.message)
        }
    }

    @Async
    def syncStockCardEntries(){
        List<StockCardTransactionsNSql> stockList = getStockCardTransactionsNSql()
        stockList.each {
            InvStockIssueJMapValues invStockIssueJMapValues = new InvStockIssueJMapValues()
            BigDecimal medicine = 0.00
            BigDecimal medicalSupply =  0.00
            BigDecimal supply = 0.00
            UUID deptId = null // For type CHARGE SLIP in stock request
            UUID itemId = null // For type CHARGE SLIP in stock request
            Integer qty = 0 // For type CHARGE SLIP in stock request
            String ledgerDate = ''
            String deptStockIssueNo = ''
            List<UUID> uuidList = []

            JSONArray array = new JSONArray(it.items)
            for (int i = 0 ; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);
                String itemType = obj['itemType']
                Integer qtyIn = obj['qtyIn'] as Integer
                Integer qtyOut = obj['qtyOut'] as Integer
                BigDecimal unitCost = obj['unitCost'] as BigDecimal
                String documentDesc = obj['documentDesc']
                String referenceNo = obj['referenceNo']
                String sourceDept = obj['sourceDept']
                String itemIdStr = obj['itemId']
                ledgerDate = obj['ledgerDate']
                UUID ledgerId = UUID.fromString(obj['id'] as String)

                BigDecimal amount = 0
                List<String> outQty = Arrays.asList('CHARGESLIP','STOCKTRANSFER OUT','EXPENSE')
                List<String> inQty = Arrays.asList('REVERSE CHARGESLIP')

                if(unitCost > 0)
                    uuidList.push(ledgerId)

                if(outQty.contains(documentDesc)){
                    amount = qtyOut * unitCost
                    if(documentDesc.equalsIgnoreCase('CHARGESLIP')){
                        deptId = UUID.fromString(sourceDept)
                        itemId = UUID.fromString(itemIdStr)
                        qty = qty + qtyOut
                    }
                    else{
                        deptStockIssueNo = referenceNo
                    }
                }
                else if(inQty.contains(documentDesc)){
                    amount = qtyIn * unitCost
                    if(documentDesc.equalsIgnoreCase('REVERSE CHARGESLIP'))
                        amount = -amount
                }

                if(itemType.equalsIgnoreCase('MEDICINE')){
                    medicine = medicine + amount
                    if(documentDesc.equalsIgnoreCase('EXPENSE')){
                        invStockIssueJMapValues.expenseMedicines = invStockIssueJMapValues.expenseMedicines + amount
                    }
                    if(documentDesc.equalsIgnoreCase('STOCKTRANSFER OUT')){
                        invStockIssueJMapValues.medicine = invStockIssueJMapValues.medicine + amount
                    }
                }
                else if(itemType.equalsIgnoreCase('MEDICAL SUPPLY')){
                    medicalSupply = medicalSupply + amount
                    if(documentDesc.equalsIgnoreCase('EXPENSE')){
                        invStockIssueJMapValues.expenseMedicalSupp = invStockIssueJMapValues.expenseMedicalSupp + amount
                    }
                    if(documentDesc.equalsIgnoreCase('STOCKTRANSFER OUT')){
                        invStockIssueJMapValues.medicalSupp = invStockIssueJMapValues.medicalSupp + amount
                    }
                }
                else if(itemType.equalsIgnoreCase('SUPPLY')){
                    supply = supply + amount
                    if(documentDesc.equalsIgnoreCase('EXPENSE')){
                        invStockIssueJMapValues.expenseSupplies = invStockIssueJMapValues.expenseSupplies + amount
                    }
                    if(documentDesc.equalsIgnoreCase('STOCKTRANSFER OUT')){
                        invStockIssueJMapValues.supplies = invStockIssueJMapValues.supplies + amount
                    }
                }
            }

            //  Charge Slip
            List<String> chargeSlip = Arrays.asList('CHARGESLIP','REVERSE CHARGESLIP')  // 'REVERSE CHARGESLIP'
            List<String> deptStockIssueReq = Arrays.asList('STOCKTRANSFER OUT','EXPENSE')

            if(chargeSlip.contains(it.documentDesc))
            {
                HeaderLedger ledger
                BillingItem billingItem = billingItemServices.getBillingItemsByRecno(it.referenceNo)
                if(billingItem){
                    if(it.documentDesc.equalsIgnoreCase('CHARGESLIP')) {
                        ledger = billingItemEntry(billingItem) // Auto posted to GL
                    }
                    else if (!billingItem.canceledref && it.documentDesc.equalsIgnoreCase('REVERSE CHARGESLIP')) {
                        ledger = billingItemEntry(billingItem) // Auto posted to GL
                    }
                }
                else {
                    JSONObject obj = array.getJSONObject(0)
                    String itemIdStr =  obj['itemId'] as String
                    String caseID = getPatientCaseId(UUID.fromString(itemIdStr),obj['referenceNo'] as String)
                    if(caseID){
                        Case aCase = new Case()
                        aCase.id = UUID.fromString(caseID)
                        def billing  = billingService.findByPatientCase(aCase.id)[0]
                        billingItem = tempCreateBillingItem(billing,BillingItemType.MEDICINES,deptId,qty,itemId,ledgerDate)
                        ledger = billingItemEntry(billingItem) // Auto posted to GL
                    }
                }

                if(ledger) {
                    uuidList.each {
                        ud ->
                            InventoryLedger inventoryLedge = inventoryLedgerRepository.getById(ud)
                            inventoryLedge.postedLedger = ledger.id
                            inventoryLedgerRepository.save(inventoryLedge)
                    }

                }
            }
            else if(deptStockIssueReq.contains(it.documentDesc)){
//                DepartmentStockIssue deptIssue = departmentStockIssueRepository.findIssueByNo(deptStockIssueNo)
//                if(!deptIssue.postedLedger){
//                    String ledgerId = addJournalEntryForStockIssuance(deptIssue)
//                    if(ledgerId) {
//                        HeaderLedger headerLedger = ledgerServices.findOne(UUID.fromString(ledgerId))
//                        def login =  SecurityUtils.currentLogin()
//                        headerLedger.approvedBy = login
//                        headerLedger.approvedDatetime = Instant.now()
//                        ledgerServices.save(headerLedger) // Auto Post Entry to GL
//
//                        deptIssue.postedLedger = UUID.fromString(ledgerId)
//                        departmentStockIssueRepository.save(deptIssue)
//                        uuidList.each {
//                            ud ->
//                                InventoryLedger inventoryLedge = inventoryLedgerRepository.getById(ud)
//                                inventoryLedge.postedLedger = UUID.fromString(ledgerId)
//                                inventoryLedgerRepository.save(inventoryLedge)
//                        }
//                    }
//                }
//                else {
//                    HeaderLedger postedLedger =  adjustmentJournalEntryForStockIssuance(deptIssue,invStockIssueJMapValues) as HeaderLedger
//                    if(postedLedger.id) {
//                        def login =  SecurityUtils.currentLogin()
//                        postedLedger.approvedBy = login
//                        postedLedger.approvedDatetime = Instant.now()
//                        ledgerServices.save(postedLedger) // Auto Post Entry to GL
//
//                        uuidList.each {
//                            ud ->
//                                InventoryLedger inventoryLedge = inventoryLedgerRepository.getById(ud)
//                                if (inventoryLedge.postedLedger) {
//                                    inventoryLedge.postedLedger = postedLedger.id
//                                    inventoryLedgerRepository.save(inventoryLedge)
//                                }
//                        }
//                    }
//                }
            }

        }
    }

    BillingItem tempCreateBillingItem(
            Billing billing,
            BillingItemType billingItemType,
            UUID department,
            Integer quantity,
            UUID itemId,
            String ledgerDate
    ){
        Department tDept = [:]
        if (department) {
            tDept = departmentRepository.findById(department).get()
        }

        Item item = null

        if (itemId) {
            item = itemRepository.findById(itemId).get()
        }

        Inventory inventory = null

        def billingItemDto = new BillingItem()
        billingItemDto.id = UUID.randomUUID()
        billingItemDto.debit = 0.0
        billingItemDto.credit = 0.0
        billingItemDto.forPosting = true
        billingItemDto.department = tDept
        billingItemDto.itemType = billingItemType
        billingItemDto.status = BillingItemStatus.ACTIVE
        billingItemDto.registryTypeCharged = billing.patientCase.registryType
        billingItemDto.qty = quantity
        billingItemDto.billing = billing
        billingItemDto.recordNo = billing.billingNo
        billingItemDto.description = "[${item.itemCode}] ${item.descLong}"
        billingItemDto.details[BillingItemDetailParam.ITEMID.name()] = item.id.toString()
        Date convertDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(ledgerDate)
        Instant localConvertedDate = convertDate.toInstant()
        billingItemDto.transactionDate = localConvertedDate
        if (inventory)
            billingItemDto.details[BillingItemDetailParam.INVENTORYID.name()] = inventory.id.toString()
        return billingItemDto
    }

    HeaderLedger billingItemEntry(BillingItem billingItem){
        def yearFormat = DateTimeFormatter.ofPattern("yyyy")

        if(billingItem){
        Map<String,String> details = [:]
        // IPD ERD OPD
        details.put(LedgerHeaderDetailParam.QUANTITY.name(),billingItem.qty.toString())

        HeaderLedger headerLedger = integrationServices.generateAutoEntries(billingItem){bt , multipleData ->
            bt.inventoryDept = billingItem.department
            bt.costDept = billingItem.department
            bt.revenueDept = billingItem.department

            Department userDepartment = billingItem.department
            // ======== Special Case for Ace Bohol  WARDS REVENUE is to CSSR and PHARMACY========
            if(StringUtils.trim(userDepartment.groupCategory) =="NURSING"){
                if(bt.itemType == BillingItemType.SUPPLIES){
                    Department deptSupplies = departmentRepository.findOneByRevenueTag("SUPPLIES")
                    bt.costDept = deptSupplies
                    bt.inventoryDept = deptSupplies
                    bt.revenueDept = deptSupplies

                }

                if(bt.itemType == BillingItemType.MEDICINES){
                    Department medicinesDept = departmentRepository.findOneByRevenueTag("MEDICINES")
                    bt.costDept = medicinesDept
                    bt.inventoryDept = medicinesDept
                    bt.revenueDept = medicinesDept

                }
            }

            if(billingItem.registryTypeCharged == "IPD"){
                if(billingItem.itemType == BillingItemType.SUPPLIES)
                    bt.flagValue = SALES_INTEGRATION.IP_SUPPLIES.name()
                else
                    bt.flagValue = SALES_INTEGRATION.IP_MEDS.name()
            }


            if(billingItem.registryTypeCharged == "ERD"){
                if(billingItem.itemType == BillingItemType.SUPPLIES)
                    bt.flagValue = SALES_INTEGRATION.ER_SUPPLIES.name()
                else
                    bt.flagValue = SALES_INTEGRATION.ER_MEDS.name()
            }


            if(billingItem.registryTypeCharged == "OPD"){
                if(billingItem.itemType == BillingItemType.SUPPLIES)
                    bt.flagValue = SALES_INTEGRATION.OPD_SUPPLIES.name()
                else
                    bt.flagValue = SALES_INTEGRATION.OPD_MEDS.name()
            }

            //Check if item id exist
            String itemId = bt.details[BillingItemDetailParam.ITEMID.name()]
            if(StringUtils.isNotBlank(itemId)){

                if(billingItem.registryTypeCharged == "OTC"){

                    if(billingItem.itemType == BillingItemType.SUPPLIES)
                    {
                        bt.flagValue = SALES_INTEGRATION.OTC_SUPPLIES.name()
                    }
                    else
                    {
                        bt.flagValue = SALES_INTEGRATION.OTC_MEDS.name()
                    }
                }
            }

                def cost= inventoryResource.getLastUnitPrice(itemId).abs()
                bt.cogsPerItem = cost
                details.put(LedgerHeaderDetailParam.COGS_PER_ITEM.name(),cost.toPlainString())
                bt.costOfSale = cost * billingItem.qty
                bt.inventoryDeduct =bt.costOfSale * -1.0
                bt.income = 0
        }

        billingItem.details.each { k,v ->
            details[k] = v
        }
        details["BILLING_ID"] = billingItem.id.toString()
        def pHeader =ledgerServices.persistHeaderLedger(headerLedger,
                "${billingItem.billing.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${billingItem.billing.billingNo}",
                "${billingItem.billing.billingNo}-${billingItem.billing?.patient?.fullName?:(billingItem.billing?.otcname)}",
                "${billingItem.recordNo}-${billingItem.description}",
                LedgerDocType.DM,
                JournalType.SALES,
                billingItem.transactionDate,
                details)

        def login =  SecurityUtils.currentLogin()
        pHeader.approvedBy = login
        pHeader.approvedDatetime = Instant.now()
        ledgerServices.save(pHeader)

        return pHeader
        }

    }



    String getPatientCaseId(UUID itemId, String requestNo){
        return  entityManager.createNativeQuery("""
            SELECT cast(r.patient_case as varchar) as caseId FROM inventory.stock_request r
            left join inventory.stock_request_item ri on ri.stock_request = r.id 
            where r.stock_request_no  = :requestNo and ri.item = :itemId
        """)
                .setParameter('itemId',itemId)
                .setParameter('requestNo',requestNo)
                .unwrap(NativeQuery.class)
                .getSingleResult();
    }

    List<StockCardTransactionsNSql> getStockCardTransactionsNSql(){
            return  entityManager.createNativeQuery("""
            select 
                "referenceNo",
                "documentDesc",
                coalesce(cast(sum("qtyIn") as int),0) as "qtyIn",
                coalesce(cast(sum("qtyOut") as int),0) as "qtyOut",
                coalesce(cast(sum("adjustment") as int),0) as "adjustment",
                cast( json_agg(jsonb_build_object('id',cast("id" as varchar),'ledgerDate',"ledgerDate",'sourceDept',"sourceDept",'itemId',"item",'documentDesc',"documentDesc",'itemType',"itemType",'descLong',"descLong",'qtyIn',cast("qtyIn" as int),'qtyOut',cast("qtyOut" as int),'unitCost',cast("unitCost" as numeric),'referenceNo',"referenceNo")) as varchar) as "items"
                from
                (select
                    a.id,
                    a.source_dep as "sourceDept",
                    a.destination_dep as "destinationDept",
                    d.document_desc as "documentDesc",
                    a.item,
                    e.item_code as "itemCode",
                    e.desc_long as "descLong",
                    a.reference_no as "referenceNo",
                    a.ledger_date as "ledgerDate",
                    case 
                        when e.is_medicine is true then 'MEDICINE'
                        when ic.category_description = 'MEDICAL SUPPLY' then 'MEDICAL SUPPLY'
                        else ' SUPPLY'
                    end as "itemType",
                    case
                        when a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e'
                        or a.document_types = '37683c86-3038-4207-baf0-b51456fd7037' then 0
                        else a.ledger_qty_in end as "qtyIn",
                    a.ledger_qty_out as "qtyOut",
                    case
                        when a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e'
                        or a.document_types = '37683c86-3038-4207-baf0-b51456fd7037' then a.ledger_qty_in
                        else 0 end as "adjustment",
                    a.ledger_unit_cost as "unitCost"
                from
                    inventory.inventory_ledger a
                left join inventory.document_types d on
                    a.document_types = d.id
                left join inventory.item e on
                    a.item = e.id
                left join inventory.item_categories ic on ic.id = e.item_category 
                where
                a.ledger_unit_cost > 0 
                and
                d.id  is not null
                and
                e.id  is not null
                and
                a.posted_ledger is NULL
                and
                a.is_include = true
                and 
                d.document_desc not in ('EMERGENCY PURCHASE','STOCK RECIEVING','MATERIAL PRODUCTION','STOCKTRANSFER IN','QUANTITY ADJUSTMENT','STOCK RECIEVING (FG)','PHYSICAL COUNT')
                group by
                    a.id,
                    a.source_dep,
                    a.destination_dep,
                    a.document_types,
                    d.document_code,
                    d.document_desc,
                    a.item,
                    e.sku,
                    e.item_code,
                    e.desc_long,
                    a.reference_no,
                    a.ledger_date,
                    a.ledger_qty_in,
                    a.ledger_qty_out,
                    a.ledger_unit_cost,
                    e.is_medicine,
                    ic.category_description
                order by a.ledger_date) as stock_card
            group by 
            "referenceNo",
            "documentDesc"
        """)
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(StockCardTransactionsNSql.class))
                    .getResultList();


    }

    List<StockCardTransactionsNSql> getStockCardTransactionsNSqlByDate(
            String startDate,
            String endDate,
            String type,
            String status,
            String search
    ){
        return  entityManager.createNativeQuery("""
            select 
                "referenceNo" as "key",
                "referenceNo",
                "documentDesc",
                "sourceDept",
                "destinationDept",
                "ledgerDate",
                coalesce(cast(sum("qtyIn") as int),0) as "qtyIn",
                coalesce(cast(sum("qtyOut") as int),0) as "qtyOut",
                coalesce(cast(sum("adjustment") as int),0) as "adjustment",
                cast( json_agg(jsonb_build_object('key',cast("id" as varchar),'postedLedger',"postedLedger",'ledgerDate',"ledgerDate",'sourceDept',"sourceDept",'itemId',"item",'documentDesc',"documentDesc",'itemType',"itemType",'descLong',"descLong",'qtyIn',cast("qtyIn" as int),'qtyOut',cast("qtyOut" as int),'unitCost',cast("unitCost" as numeric),'referenceNo',"referenceNo")) as varchar) as "items"
                from
                (select
                    a.id,
                    cast(a.posted_ledger as varchar) as "postedLedger",
                    d1.department_desc as "sourceDept",
                    d2.department_desc as "destinationDept",
                    d.document_desc as "documentDesc",
                    a.item,
                    e.item_code as "itemCode",
                    e.desc_long as "descLong",
                    a.reference_no as "referenceNo",
                    to_char(date(a.ledger_date + interval '8 hour'),'YYYY-MM-DD') as "ledgerDate",
                    case 
                        when e.is_medicine is true then 'MEDICINE'
                        when ic.category_description = 'MEDICAL SUPPLY' then 'MEDICAL SUPPLY'
                        else ' SUPPLY'
                    end as "itemType",
                    case
                        when a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e'
                        or a.document_types = '37683c86-3038-4207-baf0-b51456fd7037' then 0
                        else a.ledger_qty_in end as "qtyIn",
                    a.ledger_qty_out as "qtyOut",
                    case
                        when a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e'
                        or a.document_types = '37683c86-3038-4207-baf0-b51456fd7037' then a.ledger_qty_in
                        else 0 end as "adjustment",
                        a.ledger_unit_cost as "unitCost"
                from
                    inventory.inventory_ledger a
                left join inventory.document_types d on
                    a.document_types = d.id
                left join inventory.item e on
                    a.item = e.id
                left join inventory.item_categories ic on ic.id = e.item_category 
                left join public.departments d1 on d1.id  = a.source_dep
                left join public.departments d2 on d2.id  = a.destination_dep
                where
                d.id  is not null
                and
                e.id  is not null
                and
                a.is_include = true
                and 
                case 
                    when :type = 'STOCKTRANSFER IN/OUT'
                    then document_desc in ('STOCKTRANSFER IN','STOCKTRANSFER OUT')
                    else document_desc = :type
                end
                and 
                case 
                    when :status = 'NO_UNIT_COST'
                    then a.ledger_unit_cost <= 0
                    when :status = 'POSTED'
                    then a.posted_ledger is not null
                    when :status = 'UNPOSTED'
                    then a.posted_ledger is null
                    else a.posted_ledger is null or a.posted_ledger is not null
                end  
                and 
                upper(a.reference_no) like upper(:search)
                and
                d.document_desc not in ('EMERGENCY PURCHASE','STOCK RECIEVING','MATERIAL PRODUCTION','QUANTITY ADJUSTMENT','STOCK RECIEVING (FG)','PHYSICAL COUNT')
                and
                to_char(date(a.ledger_date + interval '8 hour'),'YYYY-MM-DD') between  to_char(cast(:startDate as date),'YYYY-MM-DD')  and to_char(cast(:endDate as date),'YYYY-MM-DD')
                group by
                    a.id,
                    a.source_dep,
                    a.destination_dep,
                    a.document_types,
                    d.document_code,
                    d.document_desc,
                    a.item,
                    e.sku,
                    e.item_code,
                    e.desc_long,
                    a.reference_no,
                    a.ledger_date,
                    a.ledger_qty_in,
                    a.ledger_qty_out,
                    a.ledger_unit_cost,
                    e.is_medicine,
                    a.posted_ledger,
                    ic.category_description,
                    "sourceDept",
                    "destinationDept"
                order by a.ledger_date) as stock_card        
            group by 
            "referenceNo",
            "documentDesc",
            "sourceDept",
            "destinationDept",
            "ledgerDate"
            order by "ledgerDate"
        """)
                .setParameter('startDate',startDate)
                .setParameter('endDate',endDate)
                .setParameter('type',type)
                .setParameter('status',status)
                .setParameter('search',search)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(StockCardTransactionsNSql.class))
                .getResultList();
    }

    def getSRRLatestUnitCost(
            String itemId
    ){
        return  entityManager.createNativeQuery("""
            Select a.ledger_unit_cost from inventory.inventory_ledger a
            left join inventory.document_types d on
            a.document_types = d.id
            where d.document_desc = 'STOCK RECIEVING' and a.item = cast(itemId as uuid)
            group by a.reference_no,a.ledger_unit_cost,a.ledger_date 
            order by a.ledger_date desc
            limit 1
        """)
                .setParameter('itemId',itemId)
                .unwrap(NativeQuery.class)
                .singleResult;
    }

    @GraphQLQuery(name="getStockCardEntryList")
    List<StockCardTransactionsNSql> getStockCardEntryList(
            @GraphQLArgument(name="startDate") String startDate,
            @GraphQLArgument(name="endDate") String endDate,
            @GraphQLArgument(name="type") String type,
            @GraphQLArgument(name="status") String status,
            @GraphQLArgument(name="search") String search
    ){
        return getStockCardTransactionsNSqlByDate(startDate,endDate,type,status,search)
    }

}
