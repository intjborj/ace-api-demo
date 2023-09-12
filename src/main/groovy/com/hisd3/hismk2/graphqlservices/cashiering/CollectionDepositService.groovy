package com.hisd3.hismk2.graphqlservices.cashiering

import com.hisd3.hismk2.domain.IntegrationTemplate
import com.hisd3.hismk2.domain.SubAccountHolder
import com.hisd3.hismk2.domain.accounting.Bank
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.cashiering.*
import com.hisd3.hismk2.graphqlservices.accounting.IntegrationServices
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.graphqlservices.accounting.SubAccountSetupService
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.hibernate.mapping.Collection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Canonical
class CollectionDepositBreakDown {
    List<CustomCdctrReturn> hardCash = []
    List<CustomCdctrReturn> chequeEncashmentReceive = []
    List<CustomCdctrReturn> chequeEncashmentReturn = []

    BigDecimal systemTotalHardCash = 0.00
}

@Canonical
class  CustomCdctrReturn {
    UUID id
    String recNo
    BigDecimal amount
}



@Service
@GraphQLApi
@Transactional(rollbackOn = Exception.class)
class CollectionDepositService extends AbstractDaoService<CollectionDeposit> {

    @Autowired
    CdctrServices cdctrServices


    @Autowired
    GeneratorService generatorService

    @Autowired
    IntegrationServices integrationServices

    @Autowired
    SubAccountSetupService subAccountSetupService

    @Autowired
    LedgerServices ledgerServices

    @Autowired
    HospitalConfigService hospitalConfigService

    @PersistenceContext
    EntityManager entityManager

    @Autowired
    ChequeEncashmentServices chequeEncashmentServices

    CollectionDepositService( ) {
        super(CollectionDeposit.class)
    }


    @GraphQLQuery(name = "collectionDetailById")
    CollectionDetail collectionDetailById(
            @GraphQLArgument(name = "id") UUID id
    ) {
        entityManager.find(CollectionDetail.class,id)
    }


    @GraphQLMutation
    GraphQLRetVal<String> postToLedger(
            @GraphQLArgument(name = "id") UUID id
    ) {
        def allCoa= subAccountSetupService.getAllChartOfAccountGenerate("","","","","")

        def collection = findOne(id)


        if(!collection.transactionDateTime){
            return new GraphQLRetVal<String>("Transaction Date/Time is required",false,"ERROR")
        }


        if(!collection.remarks){
            return new GraphQLRetVal<String>("Remarks is required",false,"ERROR")
        }

        Map<CashierTerminal,BigDecimal> cashOnHandCredit = [:]

        List<UUID> shiftsID = new ArrayList<UUID>()

        collection.cdctrList.each {
            it.shiftings.each { shifting ->
                shiftsID << shifting.id
                if(!cashOnHandCredit.containsKey(shifting.terminal))
                    cashOnHandCredit[shifting.terminal] = 0.0


                shifting.payments.findAll { PaymentTracker a -> BooleanUtils.isNotTrue(a.voided) }.each {pt->
                    pt.paymentDetails.each { ptd ->

                        switch (ptd.type) {

                            case PaymentType.CASH:
                                cashOnHandCredit[shifting.terminal] = cashOnHandCredit[shifting.terminal] + ptd.amount
                                break

                        }
                    }
                }
            }
        }

        def chequeEncashment = chequeEncashmentServices.cheqEncashmentByShiftId(shiftsID as ArrayList<UUID>)
        if(chequeEncashment){
            chequeEncashment.each {ce->
                if(ce?.shifting){
                    if(shiftsID.contains(ce.shifting.id) && !ce?.returnedShifting){
                        cashOnHandCredit[ce.terminal] = cashOnHandCredit[ce.terminal] - ce.amount
                    }

                }
//                if(shiftsID.contains(ce.shifting.id)){
//                    if(ce?.shifting?.id != ce?.returnedShifting?.id && !shiftsID.contains(ce?.returnedShifting?.id))
//                        cashOnHandCredit[ce.terminal] = cashOnHandCredit[ce.terminal] - ce.amount
//                }

                if(ce?.returnedShifting){
                    if(ce?.returnedShifting?.id && shiftsID.contains(ce?.returnedShifting?.id)){
                        if(ce?.returnedShifting?.id != ce.shifting.id)
                            cashOnHandCredit[ce.terminal] = cashOnHandCredit[ce.terminal] + ce.amount
                    }
                }
            }
        }


        BigDecimal totalCashCredit = 0.0


        cashOnHandCredit.each { k, v ->
            totalCashCredit += v
        }


        Map<CashierTerminal,BigDecimal> checkOnHandCredit = [:]
        BigDecimal totalCheckCredit = 0.0

        // compute credit for Check
        def sample = []
        collection.collectionDetails.findAll { it.type == CollectionDetailType.CHECK}.each{
            sample << it.paymentTrackerDetails
        }

        def a = sample

        collection.collectionDetails.findAll { it.type == CollectionDetailType.CHECK}.each {cd ->

         cd.paymentTrackerDetails.each {

             if(!checkOnHandCredit.containsKey(it.paymentTracker.shift.terminal))
                checkOnHandCredit[it.paymentTracker.shift.terminal] = 0.0

             checkOnHandCredit[it.paymentTracker.shift.terminal] = checkOnHandCredit[it.paymentTracker.shift.terminal] + it.amount
         }

        }


        //compute credit for Cheque Encashment
        collection.collectionDetails.findAll { it.type == CollectionDetailType.CHECKENCASHMENT}.each {cd ->

            cd.chequeEncashmentDetails.each {

                if(!checkOnHandCredit.containsKey(it.terminal))
                    checkOnHandCredit[it.terminal] = 0.0

                checkOnHandCredit[it.terminal] = checkOnHandCredit[it.terminal] + it.amount
            }

        }

        checkOnHandCredit.each { k, v ->
            totalCheckCredit += v
        }


        // compute for Debit
        Map<Bank,BigDecimal> cashDebit = [:]
        Map<Bank,BigDecimal> checkDebit = [:]
        Map<CashierTerminal,BigDecimal> cashOverage = [:]
        Map<CashierTerminal,BigDecimal> cashShortage = [:]

        collection.collectionDetails.each {
            if(it.bank){

                if(!cashDebit.containsKey(it.bank))
                    cashDebit[it.bank] = 0.0

                if(!checkDebit.containsKey(it.bank))
                    checkDebit[it.bank] = 0.0



                if(it.type==CollectionDetailType.CASH){
                    cashDebit[it.bank] =  cashDebit[it.bank] + it.amount
                }
                else {
                    checkDebit[it.bank] =  checkDebit[it.bank] + it.amount
                }
            }

            if(it.terminal){

                //Cash overage/shortage
                if(!cashOverage.containsKey(it.terminal))
                    cashOverage[it.terminal] = 0.0

                if(!cashShortage.containsKey(it.terminal))
                    cashShortage[it.terminal] = 0.0


                if(it.amount > 0.0){
                    // overage
                    cashOverage[it.terminal] = cashOverage[it.terminal] + it.amount
                }
                else {
                    cashShortage[it.terminal] = cashShortage[it.terminal] + it.amount.abs()
                }

            }

        }

        BigDecimal totalCashDebit = 0.0
        BigDecimal totalCashDebitShortage = 0.0
        BigDecimal totalCashCreditOverage = 0.0
        BigDecimal totalCheckDebit = 0.0


        cashDebit.each { k, v ->
            totalCashDebit += v
        }

        checkDebit.each { k, v ->
            totalCheckDebit += v
        }

        cashOverage.each { k, v ->
            totalCashCreditOverage += v
        }

        cashShortage.each { k, v ->
            totalCashDebitShortage += v
        }

        // validate cash and check credits

        BigDecimal totalCredit = totalCashCredit + totalCheckCredit
        BigDecimal totalDebit = totalCashDebitShortage + totalCheckDebit + totalCashDebit

        // validate debits
        if(totalCredit != totalDebit){
            return new GraphQLRetVal<String>("Deposit Details Error. [Debit: ${totalDebit.toPlainString()} <> Credit:${totalCredit.toPlainString()}].",false,"ERROR")
        }


        // Check Deposit is Optional
       /* if(totalCheckCredit != totalCheckDebit){

            return new GraphQLRetVal<String>("Check deposit does not match source",false,"ERROR")
        }*/

        def headerLedger =	integrationServices.generateAutoEntries(new IntegrationTemplate()){it, multiple ->
            it.flagValue="COLLECTION_DEPOSIT"

            //Cash Debit
            List<IntegrationTemplate> cashD = []
            cashDebit.each { k, v->

                if(v > 0){
                    cashD << new IntegrationTemplate().tap {
                         it.sub_a = new SubAccountHolder(k)
                         it.value_a = v
                    }
                }
            }
            multiple << cashD

            //Check Debit
            List<IntegrationTemplate> checkD = []
            checkDebit.each { k, v->

                if(v > 0){
                    checkD << new IntegrationTemplate().tap {
                        it.sub_b = new SubAccountHolder(k)
                        it.value_b = v
                    }
                }
            }
            multiple << checkD

            //Cash Credit
            List<IntegrationTemplate> cashC = []
            cashOnHandCredit.each { k, v->
                if(v > 0){
                    cashC << new IntegrationTemplate().tap {
                        it.sub_c = new SubAccountHolder(k)
                        it.value_c = v * -1
                    }
                }
            }

            cashOverage.each { k, v ->
                if (v > 0) {
                    cashC << new IntegrationTemplate().tap {
                        it.sub_c = new SubAccountHolder(k)
                        it.value_c = v
                    }
                }
            }

            multiple << cashC

            //Check Credit
           List<IntegrationTemplate> checkC = []
            checkOnHandCredit.each { k, v->

                if(v > 0){
                    checkC << new IntegrationTemplate().tap {
                        it.sub_d = new SubAccountHolder(k)
                        it.value_d = v * -1
                    }
                }
            }
            multiple << checkC

            // Shortage
            List<IntegrationTemplate> shortage = []
            cashShortage.each { k, v->

                if(v > 0){
                    shortage << new IntegrationTemplate().tap {
                        it.sub_f = new SubAccountHolder(k)
                        it.value_f = v * -1
                    }
                }
            }

            // Income Credit
            cashOverage.each { k, v->

                if(v > 0){
                    shortage << new IntegrationTemplate().tap {
                        it.sub_f = new SubAccountHolder(k)
                        it.value_f = v
                    }
                }
            }

            multiple << shortage

       }


        def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
                "${collection.collectionId}",
                "${hospitalConfigService.hospitalInfo.hospitalName}",
                "${collection.remarks}",
                LedgerDocType.AJ,
                JournalType.GENERAL,
                collection.transactionDateTime,
                [:]
        )

        collection.ledgerHeader = pHeader.id

        save(collection)

        return new GraphQLRetVal<String>("OK",true,"OK")
    }

    @GraphQLMutation
    Boolean deleteCollectionDetail(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "detailId") UUID detailId
    ) {

        def collection = findOne(id)

        collection.collectionDetails.removeAll {
              it.id == detailId
        }


        save(collection)
    }

    @GraphQLMutation
    Boolean addCollectionDetail(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "checkids") List<UUID> checkids
    ) {

        def collection = findOne(id)

        def detail = new CollectionDetail()
        updateFromMap(detail,fields)

        if(checkids){
            BigDecimal total = 0.0
            // it is a check deposit
            checkids?.each {
                def ptd = entityManager.find(PaymentTrackerDetails.class,it)
                total += ptd.amount
                ptd.collectionDetail = detail
                entityManager.merge(ptd)
                detail.paymentTrackerDetails << ptd
            }
            detail.amount = total
        }

        detail.collection = collection
        collection.collectionDetails << detail

        save(collection)



        true
    }

    @GraphQLMutation
    CollectionDeposit upsertCollectionDeposit(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {

        upsertFromMap(id,fields){it, forInsert ->

        }
    }

    @GraphQLQuery(name = "collectionDepositById")
     CollectionDeposit collectionDepositById(
            @GraphQLArgument(name = "id") UUID id
    ){

        def collection = findOne(id)

        collection
    }

    @GraphQLQuery(name = "collectionDeposit")
    Page<CollectionDeposit> getCollectionDeposit(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ){
        getPageable(
                "from CollectionDeposit cd where lower(cd.collectionId) like lower(concat('%',:filter,'%'))    order by cd.createdDate",
                "Select count(cd) from CollectionDeposit cd where lower(cd.collectionId) like lower(concat('%',:filter,'%')) ",
                page,
                size,
                [filter:filter]
        )
    }


    @GraphQLMutation
    CollectionDeposit addToCollectionDeposit(
            @GraphQLArgument(name = "ids") List<UUID> ids/*,
            @GraphQLArgument(name = "transactionDateTime") Instant transactionDateTime*/
    ) {

        List<Cdctr> cdctrList = []
        ids.each {
            cdctrList << cdctrServices.findOne(it)
        }

        def collection = new CollectionDeposit()
       //collection.transactionDateTime = transactionDateTime
        collection.collectionId = generatorService.getNextValue(GeneratorType.COLLECTION_DEPOSIT){  it
           return "CD-" + StringUtils.leftPad(it + "",5,"0")
        }

        cdctrList.each {
            collection.cdctrList << it
        }


        collection = save(collection)

        cdctrList.each {
            it.collection = collection
            cdctrServices.save(it)
        }




        // compute

        collection.totalHardCash = 0.0
        collection.totalCheck = 0.0
        collection.totalCard = 0.0
        collection.totalBankdeposit = 0.0



        collection.cdctrList.each {
            it.shiftings.each {shifting->
                shifting.payments.findAll { PaymentTracker a -> BooleanUtils.isNotTrue(a.voided) }.each {   pt->
                    pt.paymentDetails.each { ptd ->
                        switch (ptd.type) {

                            case PaymentType.BANKDEPOSIT:
                                collection.totalBankdeposit += ptd.amount
                                break

                            case PaymentType.CHECK:
                                collection.totalCheck += ptd.amount
                                break

                            case PaymentType.CARD:
                                collection.totalCard += ptd.amount
                                break

                            case PaymentType.CASH:
                                collection.totalHardCash += ptd.amount
                                break

                        }
                    }

                }

            }
        }

         save(collection)

    }


    @GraphQLQuery(name = "collectionDepositRecordById")
    CollectionDepositBreakDown collectionDepositRecordById(
            @GraphQLArgument(name = "id") UUID id
    ){
        def collectionDeposit = collectionDepositById(id)
        CollectionDepositBreakDown collectionDepositBreakDown = new CollectionDepositBreakDown()

        if(collectionDeposit){
            List<UUID> shiftsID = new ArrayList<UUID>()
            collectionDeposit.cdctrList.each {
                BigDecimal totalHardCash = 0.00
                CustomCdctrReturn cdctr = new CustomCdctrReturn()
                cdctr.id = it.id
                cdctr.recNo = it.recno
                it.shiftings.each {shifting->
                    shiftsID << shifting.id
                    shifting.payments.findAll { PaymentTracker a -> BooleanUtils.isNotTrue(a.voided) }.each {   pt->
                        pt.paymentDetails.each { ptd ->
                            switch (ptd.type) {
                                case PaymentType.CASH:
                                    totalHardCash += ptd.amount
                                    break
                            }
                        }
                    }
                }
                cdctr.amount = totalHardCash
                collectionDepositBreakDown.systemTotalHardCash += totalHardCash
                if(cdctr)
                    collectionDepositBreakDown.hardCash << cdctr
            }

            def chequeEncashment = chequeEncashmentServices.cheqEncashmentByShiftId(shiftsID as ArrayList<UUID>)
            if(chequeEncashment){
                chequeEncashment.each {ce->
                    CustomCdctrReturn receive = new CustomCdctrReturn()

                        if(shiftsID.contains(ce.shifting.id)){
                            if(!ce.returnedShifting){
                                receive.id = ce.id
                                receive.recNo = ce.recordNo
                                receive.amount = ce.amount
                                collectionDepositBreakDown.systemTotalHardCash -= ce.amount
                                collectionDepositBreakDown.chequeEncashmentReceive << receive
                            }
                        }

                        if(ce.returnedShifting){
                            if(shiftsID.contains(ce?.returnedShifting?.id) && ce?.returnedShifting?.id != ce.shifting.id) {
                                CustomCdctrReturn returned = new CustomCdctrReturn()
                                returned.id = ce.id
                                returned.recNo = ce.recordNo
                                returned.amount = ce.amount
                                collectionDepositBreakDown.systemTotalHardCash += ce.amount
                                collectionDepositBreakDown.chequeEncashmentReturn << returned
                            }
                        }
                }
            }
        }

        return  collectionDepositBreakDown

    }

    // Donss cheque encashment integration
    @GraphQLMutation
    Boolean addChequeEncashmentCollectionDetail(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "checkids") List<UUID> checkids
    ) {

        def collection = findOne(id)

        def detail = new CollectionDetail()
        updateFromMap(detail,fields)

        if(checkids){
            BigDecimal total = 0.0
            // it is a check deposit
            checkids?.each {
                def ptd = entityManager.find(ChequeEncashment.class,it)
                total += ptd.amount
                ptd.collectionDetail = detail
                entityManager.merge(ptd)
                detail.chequeEncashmentDetails << ptd
            }
            detail.amount = total
        }

        detail.collection = collection
        collection.collectionDetails << detail

        save(collection)



        true
    }

}
