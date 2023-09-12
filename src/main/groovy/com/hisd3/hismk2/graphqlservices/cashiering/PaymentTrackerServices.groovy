package com.hisd3.hismk2.graphqlservices.cashiering

import com.hisd3.hismk2.domain.IntegrationTemplate
import com.hisd3.hismk2.domain.SubAccountHolder
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.billing.*
import com.hisd3.hismk2.domain.billing.enums.InvestorLedgerTransactionType
import com.hisd3.hismk2.domain.cashiering.*
import com.hisd3.hismk2.domain.hospital_config.Constant
import com.hisd3.hismk2.graphqlservices.InvestorConf
import com.hisd3.hismk2.graphqlservices.accounting.*
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.graphqlservices.billing.InvestorPaymentLedgerService
import com.hisd3.hismk2.graphqlservices.billing.SubscriptionService
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.billing.InvestorPaymentLedgerRepository
import com.hisd3.hismk2.repository.billing.InvestorsRepository
import com.hisd3.hismk2.repository.billing.SubscriptionRepository
import com.hisd3.hismk2.repository.hospital_config.ConstantRepository
import com.hisd3.hismk2.security.SecurityUtils
import groovy.json.JsonSlurper
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

// MISC is manual
// Trade Only
enum PAYMENTS_RECOGNITION_PATIENT {
    HOSPITAL_PAYMENTS,
    MISC_PAYMENTS,
    REAPPLY_OR
}


@Service
@GraphQLApi
class PaymentTrackerServices extends AbstractDaoService<PaymentTracker> {
    PaymentTrackerServices() {
        super(PaymentTracker.class)
    }

    @PersistenceContext
    EntityManager entityManager

    @Autowired
    BillingService billingService

    @Autowired
    InvestorPaymentLedgerService investorPaymentLedgerService

    @Autowired
    BillingItemServices billingItemServices

    @Autowired
    ReceiptIssuanceService receiptIssuanceService

    @Autowired
    ShiftingServices shiftingServices

    @Autowired
    ChartOfAccountServices chartOfAccountServices

    @Autowired
    IntegrationServices integrationServices

    @Autowired
    LedgerServices ledgerServices

    @Autowired
    SubAccountSetupService subAccountSetupService

    @Autowired
    HospitalConfigService hospitalConfigService

    @Autowired
    BankServices bankServices

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate

    @Autowired
    DepartmentRepository departmentRepository

    @Autowired
    InvestorsRepository investorsRepository

    @Autowired
    SubscriptionService subscriptionService

    @Autowired
    SubscriptionRepository subscriptionRepository

    @Autowired
    InvestorPaymentLedgerRepository investorPaymentLedgerRepository

    @Autowired
    AccountsPayableServices accountsPayableServices

    @Autowired
    ConstantRepository constantRepository

    @Autowired
    PaymentTransactionTypeServices paymentTransactionTypeServices

    @Value('${accounting.autopostjournal}')
    Boolean auto_post_journal

    @GraphQLQuery(name = "getChecksForDeposit")
    Page<PaymentTrackerDetails> getChecksForDeposit(
            @GraphQLArgument(name = "filter") String filter, // probably Check No.
            @GraphQLArgument(name = "page") Integer page, // 0 based
            @GraphQLArgument(name = "size") Integer size
    ) {


        def records = entityManager.createQuery("""
         from PaymentTrackerDetails pt 
       where  pt.type=:type and  coalesce(pt.ignored,false) = false and (pt.reconcileId is null and  pt.collectionDetail is null)
       and lower(pt.reference) like concat('%',:filter,'%')
  order by pt.paymentTracker.createdDate
""", PaymentTrackerDetails.class)
                .setParameter("type", PaymentType.CHECK)
                .setParameter("filter", filter)
                .setMaxResults(size)
                .setFirstResult(page * size)
                .resultList


        def count = entityManager.createQuery("""
      Select count(pt)  from PaymentTrackerDetails pt 
       where  pt.type=:type and  coalesce(pt.ignored,false) = false and pt.reconcileId is null
       and lower(pt.reference) like concat('%',:filter,'%')
""", Long.class)
                .setParameter("type", PaymentType.CHECK)
                .setParameter("filter", filter)
                .singleResult


        new PageImpl<PaymentTrackerDetails>(records, PageRequest.of(page, size),
                count)

    }


    @GraphQLMutation
    @Transactional
    Boolean clearChecks(
            @GraphQLArgument(name = "bankId") UUID bankId,
            @GraphQLArgument(name = "ids") List<UUID> ids
    ) {


        def bank = bankServices.bankById(bankId)
        List<PaymentTrackerDetails> pds = []

        ids.each {
            pds << entityManager.find(PaymentTrackerDetails.class, it)
        }

        def headerLedger = integrationServices.generateAutoEntries(new IntegrationTemplate()) { it, multiple ->
            it.flagValue = "CHECK_CLEARING"


            List<IntegrationTemplate> cib = []
            pds.each { pdt ->
                cib << new IntegrationTemplate().tap {

                    it.sub_a = new SubAccountHolder(bank)
                    it.value_a = pdt.amount
                }
            }


            multiple << cib
            List<IntegrationTemplate> cibClearing = []
            pds.each { pdt ->
                cibClearing << new IntegrationTemplate().tap {

                    it.sub_b = new SubAccountHolder(bank)
                    it.value_b = pdt.amount * -1
                }
            }

            multiple << cibClearing


        }


        def pHeader = ledgerServices.persistHeaderLedger(headerLedger,
                "CKC-${LocalDate.now().format(DateTimeFormatter.ofPattern("MMddyyyy"))}",
                hospitalConfigService.hospitalInfo.hospitalName,
                "CHECK CLEARING",
                LedgerDocType.JV,
                JournalType.GENERAL,
                Instant.now(),
                [:]
        )

        pds.each {
            it.reconcileId = pHeader.id
            it.reconcileDate = Instant.now()
            entityManager.merge(it)
        }

        true

        true
    }

    @GraphQLMutation
    @Transactional
    Boolean clearCards(
            @GraphQLArgument(name = "ids") List<UUID> ids
    ) {

        List<PaymentTrackerDetails> pds = []

        ids.each {
            pds << entityManager.find(PaymentTrackerDetails.class, it)
        }

        def headerLedger = integrationServices.generateAutoEntries(new IntegrationTemplate()) { it, multiple ->
            it.flagValue = "CARD_CLEARING"

            List<IntegrationTemplate> cib = []
            pds.each { pdt ->
                cib << new IntegrationTemplate().tap {

                    it.sub_a = new SubAccountHolder(pdt.bankEntity)
                    it.value_a = pdt.amount
                }
            }

            multiple << cib
            List<IntegrationTemplate> arCheckClearing = []
            pds.each { pdt ->
                arCheckClearing << new IntegrationTemplate().tap {

                    it.sub_b = new SubAccountHolder(pdt.bankEntity)
                    it.value_b = pdt.amount * -1
                }
            }

            multiple << arCheckClearing


        }

        def pHeader = ledgerServices.persistHeaderLedger(headerLedger,
                "CC-${LocalDate.now().format(DateTimeFormatter.ofPattern("MMddyyyy"))}",
                hospitalConfigService.hospitalInfo.hospitalName,
                "CARD CLEARING",
                LedgerDocType.JV,
                JournalType.GENERAL,
                Instant.now(),
                [:]
        )

        pds.each {
            it.reconcileId = pHeader.id
            it.reconcileDate = Instant.now()
            entityManager.merge(it)
        }

        true

    }


    @GraphQLMutation
    @Transactional
    PaymentTrackerDetails deniedItem(
            @GraphQLArgument(name = "paymentTrackerDetailId") UUID paymentTrackerDetailId
    ) {

        def ptd = entityManager.find(PaymentTrackerDetails.class, paymentTrackerDetailId)
        ptd.denied = true
        ptd = entityManager.merge(ptd)
        ptd
    }

    @GraphQLQuery(name = "getCreditCardsAndCheck")
    Page<PaymentTrackerDetails> getCreditCardsAndCheck(
            @GraphQLArgument(name = "type") String type,
            @GraphQLArgument(name = "startDateTime") Instant startDateTime,
            @GraphQLArgument(name = "endDateTime") Instant endDateTime,
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size,
            @GraphQLArgument(name = "showAll") Boolean showAll
    ) {


        String showAllStr = " and  :showAll = true "

        if (!showAll) {

            showAllStr = """
   and ptd.reconcileId is  null and  :showAll = false and (ptd.denied = false or ptd.denied is null)
"""
        }

        def records = entityManager.createQuery("""
        Select ptd  from PaymentTrackerDetails ptd where ptd.type=:type   and
        ptd.paymentTracker.createdDate >= :startDateTime and ptd.paymentTracker.createdDate <= :endDateTime 
        and
       (
       lower(ptd.reference) like lower(concat('%',:filter,'%'))
       )
        ${showAllStr}   and (ptd.paymentTracker.voided is null or ptd.paymentTracker.voided=false)
       order by ptd.paymentTracker.createdDate
       
""", PaymentTrackerDetails.class)
                .setParameter("showAll", showAll)
                .setParameter("filter", filter)
                .setParameter("type", PaymentType.valueOf(type))
                .setParameter("startDateTime", startDateTime)
                .setParameter("endDateTime", endDateTime)
                .setMaxResults(size)
                .setFirstResult(page * size)
                .resultList


        Long count = entityManager.createQuery("""
  select count(*) from PaymentTrackerDetails ptd where ptd.type=:type and
		   ptd.paymentTracker.createdDate >=:startDateTime and ptd.paymentTracker.createdDate <= :endDateTime and 
		   ( 
			 lower(ptd.reference) like lower(concat('%',:filter,'%')) 
		   )
		     ${showAllStr} and (ptd.paymentTracker.voided is null or ptd.paymentTracker.voided=false)
		   
""", Long.class).setParameter("showAll", showAll)
                .setParameter("showAll", showAll)
                .setParameter("filter", filter)
                .setParameter("type", PaymentType.valueOf(type))
                .setParameter("startDateTime", startDateTime)
                .setParameter("endDateTime", endDateTime)
                .resultList.find()


        new PageImpl<PaymentTrackerDetails>(records, PageRequest.of(page, size),
                count ?: 0)

    }

    PaymentTracker findPTByHeader(UUID ledgerHeader) {

        createQuery("from PaymentTracker p where p.ledgerHeader =:ledgerHeader",
                [ledgerHeader: ledgerHeader]).resultList.find()

    }


    List<PaymentTrackerDetails> getCreditCardsAndCheckDownload(
            @GraphQLArgument(name = "type") String type,
            @GraphQLArgument(name = "startDateTime") Instant startDateTime,
            @GraphQLArgument(name = "endDateTime") Instant endDateTime,
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "showAll") Boolean showAll
    ) {


        String showAllStr = " and  :showAll = true "

        if (!showAll) {

            showAllStr = """
   and ptd.reconcileId is  null and  :showAll = false and (ptd.denied = false or ptd.denied is null)
"""
        }

        def records = entityManager.createQuery("""
        Select ptd  from PaymentTrackerDetails ptd where ptd.type=:type   and
        ptd.paymentTracker.createdDate >= :startDateTime and ptd.paymentTracker.createdDate <= :endDateTime 
        and
       (
       lower(ptd.reference) like lower(concat('%',:filter,'%'))
       )
        ${showAllStr}   and (ptd.paymentTracker.voided is null or ptd.paymentTracker.voided=false)
       order by ptd.paymentTracker.createdDate
       
""", PaymentTrackerDetails.class)
                .setParameter("showAll", showAll)
                .setParameter("filter", filter)
                .setParameter("type", PaymentType.valueOf(type))
                .setParameter("startDateTime", startDateTime)
                .setParameter("endDateTime", endDateTime)
                .resultList


        return records


    }


    @GraphQLMutation
    @Transactional
    PaymentTracker applyOr(
            @GraphQLArgument(name = "headerId") UUID headerId,
            @GraphQLArgument(name = "ledgerId") UUID ledgerId,
            @GraphQLArgument(name = "billingId") UUID billingId,
            @GraphQLArgument(name = "amountCut") BigDecimal amountCut
    ) {

        ///	def allCoa= subAccountSetupService.getAllChartOfAccountGenerate("","","","","")
        def header = ledgerServices.findOne(headerId)
        PaymentTracker paymentTracker = findPTByHeader(headerId)
        Billing billing = billingService.findOne(billingId)


        def ledger = header.ledger.find { it.id == ledgerId }
        def forHospitalAmount = amountCut
        def forAllPFBalance = 0.0

        billingService.balances(billingId).findAll { it.employeeid }.each {
            forAllPFBalance += it.balance
        }


        List<BillingItem> pfPayments = []
        List<BillingItem> hciPayments = []

        // Nov. 5, 2020
        // will now prioritized PF Fees
        def pfBalances = billingService.balances(billingId).findAll { it.employeeid }

        for (charges in pfBalances.findAll { it.balance > 0 }) {

            if (forHospitalAmount > 0) {
                if (forHospitalAmount >= charges.balance) {
                    forHospitalAmount -= charges.balance
                    pfPayments << billingService.addPayment(
                            billingId,
                            charges.balance,
                            charges.employeeid,
                            paymentTracker,
                            "OR Application: ${paymentTracker.ornumber}",
                            true
                    )
                } else {
                    pfPayments << billingService.addPayment(
                            billingId,
                            forHospitalAmount,
                            charges.employeeid,
                            paymentTracker,
                            "OR Application: ${paymentTracker.ornumber}",
                            true
                    )
                    forHospitalAmount = 0.0
                }
            } else {
                break
            }
        }

        if (forHospitalAmount > 0.0) {
            hciPayments << billingService.addPayment(
                    billingId,
                    forHospitalAmount,
                    null,
                    paymentTracker,
                    "OR Application: ${paymentTracker.ornumber}",
                    true
            )
        }


        // post to accounting
        def headerLedger = integrationServices.generateAutoEntries(new IntegrationTemplate()) { IntegrationTemplate template, multipleData ->

            template.flagValue = "REAPPLY_OR"

            //IPD ERD OPD OTC
            // get all ER Payments

            template.value_a = 0.0 // in-patient
            template.value_b = 0.0 // outpatient
            template.value_c = 0.0 // otc
            template.value_d = 0.0 // emergency

            hciPayments.each {
                it.amountdetails.each { k, v ->
                    BillingItem bItem = billing.billingItemList.find { it.id == UUID.fromString(k) }
                    if (bItem) {
                        if (bItem.registryTypeCharged == "ERD")
                            template.value_d += v

                        if (bItem.registryTypeCharged == "OPD")
                            template.value_b += v

                        if (bItem.registryTypeCharged == "IPD")
                            template.value_a += v

                        if (bItem.registryTypeCharged == "OTC")
                            template.value_c += v
                    }
                }
            }

            template.value_a *= -1
            template.value_b *= -1
            template.value_c *= -1
            template.value_d *= -1

            template.value_e = 0.0
            pfPayments.each {
                template.value_e += it.subTotal.abs()
            }

        }


        List<Entry> entries = []
        headerLedger.ledger.each {      // neutralized

            // if normal side is Debit
            if (it.journalAccount.motherAccount.normalSide == "DEBIT") {
                entries << new Entry(it.journalAccount, it.credit * -1).tap {
                    it.journal.fromGenerator = true
                }
            }

            if (it.journalAccount.motherAccount.normalSide == "CREDIT") {
                entries << new Entry(it.journalAccount, it.credit).tap {
                    it.journal.fromGenerator = true
                }
            }

        }
        entries << new Entry(ledger.journalAccount.tap {
            it.fromGenerator = true
        }, amountCut * -1)

        headerLedger = ledgerServices.createDraftHeaderLedger(entries)

        def yearFormat = DateTimeFormatter.ofPattern("yyyy")

        def pHeader = ledgerServices.persistHeaderLedger(headerLedger,
                "${billing.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${billing.billingNo}",
                "${billing.billingNo}-${billing?.patientCase?.patient?.fullName ?: billing.otcname}",
                "Folio Application-${paymentTracker.description}",
                LedgerDocType.AJ,
                JournalType.GENERAL,
                Instant.now(),
                [:]
        )

        pHeader.reapplyPaymentTracker = paymentTracker.id

        ledgerServices.save(pHeader)
        pfPayments.each { bItem ->
            paymentTracker.appliedOrs << new AppliedOr().tap {
                it.billingItem = bItem
                it.amount = bItem.subTotal.abs()
                it.journalCode = ledger.journalAccount.code
                it.paymentTracker = paymentTracker
            }
        }


        hciPayments.each { bItem ->
            paymentTracker.appliedOrs << new AppliedOr().tap {
                it.billingItem = bItem
                it.amount = bItem.subTotal.abs()
                it.journalCode = ledger.journalAccount.code
                it.paymentTracker = paymentTracker
            }
        }

        save(paymentTracker)

    }


    @GraphQLQuery(name = "searchORForApplication")
    PaymentTracker searchORForApplication(
            @GraphQLArgument(name = "orNumber") String orNumber
    ) {

        def match = createQuery("from PaymentTracker p where p.ornumber=:ornumber  and p.receiptType = :receiptType and (p.voided is null or p.voided = false ) order by p.ornumber",
                [
                        ornumber   : orNumber,
                        receiptType: ReceiptType.OR

                ])
                .resultList.find()


        if (match?.ledgerHeader) {
            def headerLeadger = ledgerServices.findOne(match.ledgerHeader)
            def allCredits = headerLeadger.ledger.findAll { it.credit > 0 }
            allCredits.each { ledger ->

                ledger.totalAppliedOr = 0.0
                match.appliedOrs.findAll { aor -> aor.journalCode == ledger.journalAccount.code }
                        .each {
                            ledger.totalAppliedOr += it.amount
                        }

                match.allCredits << ledger
            }
        }


        match
    }


    @GraphQLQuery(name = "getPaymentsByBillingId")
    List<PaymentTracker> getPaymentsByBillingId(
            @GraphQLArgument(name = "billingId") UUID billingId
    ) {

        createQuery("from PaymentTracker p where p.billingid=:billingId order by p.ornumber",
                [billingId: billingId])
                .resultList
    }

    // should not void is totalApplied > 0
    @GraphQLMutation
    @Transactional(rollbackOn = Exception.class)
    GraphQLRetVal<PaymentTracker> voidPayment(@GraphQLArgument(name = "paymentTrackerId") UUID paymentTrackerId,
                                              @GraphQLArgument(name = "remarks") String remarks) {

        remarks = StringUtils.upperCase(remarks)
        def paymentTracker = findOne(paymentTrackerId)

        // checked if is already deposited
        if (paymentTracker?.shift?.cdctr?.collection?.ledgerHeader) {
            return new GraphQLRetVal<PaymentTracker>(paymentTracker, false, "Cannot Void Deposited OR at [${paymentTracker?.shift?.cdctr?.collection?.collectionId}]")
        }


        if (paymentTracker.billingid) {

            billingService.processVoidORPayment(paymentTracker.billingid, paymentTracker, remarks)

        } else {
            // Misc Payments

            // Todo: Accounting Entries
            if (paymentTracker.ledgerHeader) {
                def hl = ledgerServices.findOne(paymentTracker.ledgerHeader)
                if (hl)
                    ledgerServices.reverseEntries(hl)

                UUID billingId = null
                paymentTracker.appliedOrs.each {
                    billingId = it?.billingItem?.billing?.id

                    if (it.billingItem) {
                        billingItemServices.cancelBillingItem(
                                it.billingItem.id,
                                ["VOIDTYPE": "Voided OR: " + paymentTracker.ornumber]
                        )
                    }

                }


                def effect = ledgerServices.getByPaymentTrackerParent(paymentTracker)
                effect.each {
                    ledgerServices.reverseEntries(it)
                }


                if (billingId) {
                    def billing = billingService.findOne(billingId)
                    billing.status = "ACTIVE"
                    billingService.save(billing)
                }

            }


        }

        paymentTracker.voided = true
        paymentTracker.voidDate = Instant.now()
        paymentTracker.voidType = remarks


        return new GraphQLRetVal<PaymentTracker>(paymentTracker, true, "")
    }

    @GraphQLMutation
    @Transactional
    PaymentTracker addPaymentMisc(
            @GraphQLArgument(name = "batchReceiptId") UUID batchReceiptId,
            @GraphQLArgument(name = "shiftId") UUID shiftId,
            @GraphQLArgument(name = "type") String type,
            @GraphQLArgument(name = "tendered") List<Map<String, Object>> tendered,
            @GraphQLArgument(name = "miscTarget") List<Map<String, Object>> miscTarget,
            @GraphQLArgument(name = "arRemarks") String arRemarks,
            @GraphQLArgument(name = "payorName") String payorName,
            @GraphQLArgument(name = "payorId") UUID payorId,
            @GraphQLArgument(name = "payorCategory") String payorCategory,
            @GraphQLArgument(name = "transactionCategoryId") UUID transactionCategoryId
    ) {

        def paymentTracker = new PaymentTracker()

        def totalpayments = 0.0,
            totalCash = 0.0,
            totalCheck = 0.0,
            totalCard = 0.0,
            totalDeposit = 0.0,
            totalEWallet = 0.0,
            pf = 0.0

        tendered.each {
            map ->
                def payTrackerDetail = new PaymentTrackerDetails()
                payTrackerDetail.paymentTracker = paymentTracker
                updateFromMap(payTrackerDetail, map)

                if (payTrackerDetail.type == PaymentType.CASH)
                    totalCash += payTrackerDetail.amount

                if (payTrackerDetail.type == PaymentType.CHECK)
                    totalCheck += payTrackerDetail.amount

                if (payTrackerDetail.type == PaymentType.CARD)
                    totalCard += payTrackerDetail.amount

                if (payTrackerDetail.type == PaymentType.BANKDEPOSIT)
                    totalDeposit += payTrackerDetail.amount

                if (payTrackerDetail.type == PaymentType.EWALLET)
                    totalEWallet += payTrackerDetail.amount

                paymentTracker.paymentDetails.add(payTrackerDetail)
        }

        paymentTracker.payorName = payorName?:''
        switch (payorCategory){
            case PayorType.PATIENT.name():
                paymentTracker.patientId = payorId
                break
            case PayorType.CORPORATE.name():
                paymentTracker.companyId = payorId
                break
            case PayorType.HMO.name():
                paymentTracker.companyId = payorId
                break
            case PayorType.DOCTORS.name():
                paymentTracker.supplierId = payorId
                break
            case PayorType.EMPLOYEE.name():
                paymentTracker.employeeId = payorId
                break
            default:
                break
        }

        String transactionCategory = ''
        if(transactionCategoryId) {
            def transCategory = paymentTransactionTypeServices.findOne(transactionCategoryId)
            paymentTracker.transactionCategory = transCategory.typeName
            paymentTracker.transactionCategoryId = transCategory.id
            transactionCategory = transCategory.typeName
        }


        paymentTracker.totalCash = totalCash
        paymentTracker.totalCheck = totalCheck
        paymentTracker.totalCard = totalCard
        paymentTracker.totalDeposit = totalDeposit
        paymentTracker.totalEWallet = totalEWallet

        totalpayments = (totalCash + totalCheck + totalCard + totalDeposit + totalEWallet)

        paymentTracker.totalpayments = totalpayments
        paymentTracker.hosp = totalpayments
        paymentTracker.pf = pf

        paymentTracker.change = totalpayments - (totalCash + totalCheck + totalCard + totalDeposit + totalEWallet)

        def receiptIssuance = receiptIssuanceService.findOne(batchReceiptId)
        def shift = shiftingServices.findOne(shiftId)

        if (type == "OR") {
            paymentTracker.ornumber = receiptIssuance.receiptCurrent
            paymentTracker.receiptType = ReceiptType.OR
            paymentTracker.shift = shift
            paymentTracker.description = "OR [${paymentTracker.ornumber}] - ${transactionCategory ?: ''} ${arRemarks? "[${arRemarks}]":''}"

            receiptIssuance.receiptCurrent++

            if (receiptIssuance.receiptCurrent > receiptIssuance.receiptTo) {
                receiptIssuance.receiptCurrent = null
                receiptIssuance.activebatch = false
            }
            receiptIssuanceService.save(receiptIssuance)
        } else {
            paymentTracker.ornumber = receiptIssuance.arCurrent
            paymentTracker.receiptType = ReceiptType.AR
            paymentTracker.shift = shift
            paymentTracker.description = "AR [${paymentTracker.ornumber}] - ${transactionCategory ?: ''} ${arRemarks ? "[${arRemarks}]":''}"

            receiptIssuance.arCurrent++

            if (receiptIssuance.arCurrent > receiptIssuance.arTo) {
                receiptIssuance.arCurrent = null
                receiptIssuance.aractive = false
            }
            receiptIssuanceService.save(receiptIssuance)

        }

        paymentTracker = save(paymentTracker)

        // changed to new Coa
        miscTarget.each {
            map ->
                def code = map.get("code") as String
                def accountCode = (map.get("account") ? map.get("account")['code'] : '') as String

                def amount = new BigDecimal(map.get("amount") as BigDecimal)

                def pt = new PaymentTarget()
                pt.amount = amount
                //pt.chartOfAccount = chartOfAccountServices.findOne(UUID.fromString(id))
                if(code)
                    pt.journalCode = code
                else
                    pt.journalCode = accountCode

                paymentTracker.paymentTargets << pt
        }

        if (auto_post_journal) {
            postToAccountingMisc(paymentTracker)
        }
        paymentTracker

    }


    @GraphQLMutation
    @Transactional
    PaymentTracker addInvestorPayment(
            @GraphQLArgument(name = "batchReceiptId") UUID batchReceiptId,
            @GraphQLArgument(name = "shiftId") UUID shiftId,
            @GraphQLArgument(name = "type") String type,
            @GraphQLArgument(name = "tendered") List<Map<String, Object>> tendered,
            @GraphQLArgument(name = "particular") String particular,
            @GraphQLArgument(name = "investorId") UUID investorId,
            @GraphQLArgument(name = "subscriptions") List<Map<String, Object>> subscriptions,
            @GraphQLArgument(name = "unallocated") BigDecimal unallocated

    ) {
        Investor investor = null
        investorsRepository.findById(investorId).ifPresent { investor = it }

        PaymentTracker paymentTracker = new PaymentTracker()

        def totalpayments = 0.0,
            totalCash = 0.0,
            totalCheck = 0.0,
            totalCard = 0.0,
            totalDeposit = 0.0,
            totalEWallet = 0.0,
            pf = 0.0

        tendered.each {
            map ->
                def payTrackerDetail = new PaymentTrackerDetails()
                payTrackerDetail.paymentTracker = paymentTracker
                updateFromMap(payTrackerDetail, map)

                if (payTrackerDetail.type == PaymentType.CASH)
                    totalCash += payTrackerDetail.amount

                if (payTrackerDetail.type == PaymentType.CHECK)
                    totalCheck += payTrackerDetail.amount

                if (payTrackerDetail.type == PaymentType.CARD)
                    totalCard += payTrackerDetail.amount

                if (payTrackerDetail.type == PaymentType.BANKDEPOSIT)
                    totalDeposit += payTrackerDetail.amount

                if (payTrackerDetail.type == PaymentType.EWALLET)
                    totalEWallet += payTrackerDetail.amount

                paymentTracker.paymentDetails.add(payTrackerDetail)
        }

        paymentTracker.totalCash = totalCash
        paymentTracker.totalCheck = totalCheck
        paymentTracker.totalCard = totalCard
        paymentTracker.totalDeposit = totalDeposit
        paymentTracker.totalEWallet = totalEWallet

        totalpayments = (totalCash + totalCheck + totalCard + totalDeposit + totalEWallet)

        paymentTracker.totalpayments = totalpayments
        paymentTracker.hosp = totalpayments
        paymentTracker.pf = pf

        paymentTracker.change = totalpayments

        def receiptIssuance = receiptIssuanceService.findOne(batchReceiptId)
        def shift = shiftingServices.findOne(shiftId)

        String subscriptionNo = ''
        subscriptions.each {
            UUID id = UUID.fromString(it.get("id") as String)
            subscriptionRepository
                    .findById(id)
                    .ifPresent({
                        if(subscriptionNo)
                            subscriptionNo += "| ${it.subscriptionCode }"
                        else
                            subscriptionNo = it.subscriptionCode
                    })
        }

        if (type == "OR") {
            paymentTracker.ornumber = receiptIssuance.receiptCurrent
            paymentTracker.receiptType = ReceiptType.OR
            paymentTracker.shift = shift
            paymentTracker.description = "OR [${paymentTracker.ornumber}] - SUBSCRIPTION PAYMENT - ${investor.fullName} - SUBSCRIPTION NO: ${subscriptionNo}"

            receiptIssuance.receiptCurrent++

            if (receiptIssuance.receiptCurrent > receiptIssuance.receiptTo) {
                receiptIssuance.receiptCurrent = null
                receiptIssuance.activebatch = false
            }
            receiptIssuanceService.save(receiptIssuance)
        } else {
            paymentTracker.ornumber = receiptIssuance.arCurrent
            paymentTracker.receiptType = ReceiptType.AR
            paymentTracker.shift = shift
            paymentTracker.description = "AR [${paymentTracker.ornumber}] - SUBSCRIPTION PAYMENT - ${investor.fullName} - SUBSCRIPTION NO: ${subscriptionNo}"

            receiptIssuance.arCurrent++

            if (receiptIssuance.arCurrent > receiptIssuance.arTo) {
                receiptIssuance.arCurrent = null
                receiptIssuance.aractive = false
            }
            receiptIssuanceService.save(receiptIssuance)

        }

        paymentTracker.investorId = investorId
        paymentTracker.payorName = investor?.fullName ?: ''
        paymentTracker.transactionCategory = 'INVESTOR'
        paymentTracker = save(paymentTracker)

        List<InvestorPaymentLedger> ledgers = []
        List<InvestorPaymentLedger> fullyPaidLedger = []
        List<Subscription> fullyPaidSubscriptions = []
        List<Subscription> toBePaidSubscriptions = []
        List<Map<String,Object>> forRefundSubscriptions = []

        subscriptions.each {
            Subscription subscription = null
            BigDecimal payment = it.get('payment') as BigDecimal
            UUID id = UUID.fromString(it.get("id") as String)

            subscriptionRepository
                    .findById(id)
                    .ifPresent({ subscription = it })

            if (null) throw new Exception("One of the subscription is not found.")
            BigDecimal subscriptionBalance = investorPaymentLedgerService.getSubscriptionBalance(subscription.id)

            BigDecimal subscribedShareCapital = 0
            BigDecimal subscriptionReceivable = 0

            BigDecimal balance = 0
            balance = payment - subscriptionBalance

            if (payment > balance && balance > 0) subscriptionReceivable = subscriptionBalance
            else subscriptionReceivable = payment

            //  Subscription paid in regular basis
            if (subscriptionBalance <= payment) {
                subscribedShareCapital += subscription.shares * subscription.parValue
                subscription.fullPaymentDate = Instant.now()
                fullyPaidSubscriptions.add(subscription)
            } else {
                toBePaidSubscriptions.add(subscription)
            }

            BigDecimal shareCapitalPremium = subscription.shares * (subscription.subscriptionPrice - subscription.parValue)
            BigDecimal shareCapitalCommonShare = subscription.shares * subscription.parValue

            InvestorPaymentLedger ledger = new InvestorPaymentLedger(investor, paymentTracker, subscription, InvestorLedgerTransactionType.INVESTOR_PAYMENT, 0 as BigDecimal, payment, null, "Investor Payment")
            ledger.subscriptionReceivable = subscriptionReceivable.negate()
            paymentTracker.change -= subscriptionReceivable

            ledger.subscribedShareCapital = shareCapitalCommonShare.negate()
            ledger.subscribedShareCapitalPremium = shareCapitalPremium.negate()
            ledger.shareCapitalCommonShare = shareCapitalCommonShare.abs()
            ledger.shareCapitalPremium =  shareCapitalPremium.abs()
            ledger.discountOnShareCapital = 0.00
            if (balance > 0)
                ledger.advancesFromInvestors = balance.abs()

            //  Subscription with discounted amount
            if(it.get("singlePayment")) {

                BigDecimal discount = 0.00
                Constant discountValueType = constantRepository.findByConstantName(InvestorConf.INVESTOR_REBATE_DISCOUNT_VALUE_TYPE.name())
                Constant discountValue = constantRepository.findByConstantName(InvestorConf.INVESTOR_REBATE_DISCOUNT.name())

                String discType = (discountValueType?.value?:'').toUpperCase()
                BigDecimal discValue = (discountValue?.value?:0.00) as BigDecimal
                switch (discType){
                    case 'AMOUNT':
                        discount = discValue
                        break;
                    case 'PERCENTAGE':
                        discount = subscription.total * (discountValue.value.toBigDecimal() / 100).setScale(2, RoundingMode.HALF_EVEN)
                        break;
                    default:
                        break;
                }

                if(balance.abs() == discount.abs()) {
                    subscription.fullPaymentDate = Instant.now()
                    fullyPaidSubscriptions.add(subscription)

                    subscriptionReceivable += discount
                    InvestorPaymentLedger ledgerDiscount = new InvestorPaymentLedger(investor, subscription, InvestorLedgerTransactionType.INVESTOR_DISCOUNT, 0.00, discount, null, "Subscription Discount")
                    ledgerDiscount.subscriptionReceivable = discount.negate()
                    paymentTracker.change -= 0.00
                    ledgerDiscount.subscribedShareCapital = 0.00
                    ledgerDiscount.subscribedShareCapitalPremium =  0.00
                    ledgerDiscount.shareCapitalCommonShare = 0.00
                    ledgerDiscount.discountOnShareCapital = 0.00
                    ledgerDiscount.shareCapitalPremium =  0.00
                    ledgers.add(ledgerDiscount)

                    subscribedShareCapital += subscription.shares * subscription.parValue
                    ledger.subscribedShareCapital = subscribedShareCapital.negate()
                    ledger.subscribedShareCapitalPremium = shareCapitalPremium.negate()
                    ledger.discountOnShareCapital = discount.abs()
                    ledger.shareCapitalCommonShare = subscribedShareCapital.abs()
                    ledger.shareCapitalPremium =  shareCapitalPremium.abs() - discount.abs()
                    fullyPaidLedger.add(ledger)
                }
            }
            else{
                if (subscription.fullPaymentDate)
                    fullyPaidLedger.add(ledger)
            }

            ledgers.add(ledger)

        }

        ledgers = investorPaymentLedgerRepository.saveAll(ledgers)
        subscriptionRepository.saveAll(fullyPaidSubscriptions)

        if (auto_post_journal) {
            postToAccountingInvestorPayment(ledgers, paymentTracker)
            fullyPaidLedger.each {
                fullyPaid ->
                            postToAccountingInvestorIssuanceOfShares(fullyPaid)
            }
        }

//        if(forRefundSubscriptions){
//            forRefundSubscriptions.each {
//                def reference = it['subscription']
//                accountsPayableServices.upsertPayablesByInvestor(reference,'rebate', it['amount'] as BigDecimal)
//            }
//        }

        if(unallocated){
            accountsPayableServices.upsertPayablesByInvestor(paymentTracker,'overpaid', unallocated)
        }

        paymentTracker

    }


    @GraphQLMutation
    @Transactional
    PaymentTracker addPayment(
            @GraphQLArgument(name = "batchReceiptId") UUID batchReceiptId,
            @GraphQLArgument(name = "shiftId") UUID shiftId,
            @GraphQLArgument(name = "type") String type,
            @GraphQLArgument(name = "partitions") Map<String, BigDecimal> partitions,
            @GraphQLArgument(name = "tendered") List<Map<String, Object>> tendered,
            @GraphQLArgument(name = "billingId") UUID billingId,
            @GraphQLArgument(name = "arRemarks") String arRemarks,
            @GraphQLArgument(name = "taggedIds") List<UUID> taggedIds,
            @GraphQLArgument(name = "taggedIdsMeds") List<UUID> taggedIdsMeds
    ) {

        def paymentTracker = new PaymentTracker()

        def totalpayments = 0.0,
            totalCash = 0.0,
            totalCheck = 0.0,
            totalCard = 0.0,
            totalDeposit = 0.0,
            totalEWallet = 0.00,
            hosp = 0.0,
            pf = 0.0

        tendered.each {
            map ->
                def payTrackerDetail = new PaymentTrackerDetails()
                payTrackerDetail.paymentTracker = paymentTracker
                updateFromMap(payTrackerDetail, map)

                if (payTrackerDetail.type == PaymentType.CASH)
                    totalCash += payTrackerDetail.amount

                if (payTrackerDetail.type == PaymentType.CHECK)
                    totalCheck += payTrackerDetail.amount

                if (payTrackerDetail.type == PaymentType.CARD)
                    totalCard += payTrackerDetail.amount

                if (payTrackerDetail.type == PaymentType.BANKDEPOSIT)
                    totalDeposit += payTrackerDetail.amount

                if (payTrackerDetail.type == PaymentType.EWALLET)
                    totalEWallet += payTrackerDetail.amount

                paymentTracker.paymentDetails.add(payTrackerDetail)
        }

        paymentTracker.totalCash = totalCash
        paymentTracker.totalCheck = totalCheck
        paymentTracker.totalCard = totalCard
        paymentTracker.totalDeposit = totalDeposit
        paymentTracker.totalEWallet = totalEWallet

        partitions.each {
            key, value ->
                totalpayments += value
                if (key == "HOSPITAL") {
                    hosp += value
                } else {
                    hosp += value // all hospital for progress payments
                    //pf += value
                }
        }

        paymentTracker.hosp = hosp
        paymentTracker.pf = pf // always zero... Ace removed the functionality to auto select doctor at frontend


        paymentTracker.totalpayments = totalpayments
        paymentTracker.change = totalpayments - (totalCash + totalCheck + totalCard + totalDeposit + totalEWallet)

        paymentTracker.billingid = billingId

        def receiptIssuance = receiptIssuanceService.findOne(batchReceiptId)
        def billing = billingService.findOne(billingId)
        def shift = shiftingServices.findOne(shiftId)

        if(billing){
            paymentTracker.payorName = billing.patient?.fullName ?: billing.otcname
            paymentTracker.patientId = billing.patient?.id ?: null
            paymentTracker.transactionCategory = billing?.patientCase?.registryType ? billing.overrideProgressPayment ? "${billing.patientCase.registryType}-PP" : billing.patientCase.registryType : 'OTC'
        }

        if (type == "OR") {
            paymentTracker.ornumber = receiptIssuance.receiptCurrent
            paymentTracker.receiptType = ReceiptType.OR
            paymentTracker.shift = shift
            paymentTracker.description = "OR [${paymentTracker.ornumber}] - ${billing.patient?.fullName ?: billing.otcname}"

            receiptIssuance.receiptCurrent++

            if (receiptIssuance.receiptCurrent > receiptIssuance.receiptTo) {
                receiptIssuance.receiptCurrent = null
                receiptIssuance.activebatch = false
            }
            receiptIssuanceService.save(receiptIssuance)
        } else {
            paymentTracker.ornumber = receiptIssuance.arCurrent
            paymentTracker.receiptType = ReceiptType.AR
            paymentTracker.shift = shift
            paymentTracker.description = "AR [${paymentTracker.ornumber}] - ${arRemarks}"

            receiptIssuance.arCurrent++

            if (receiptIssuance.arCurrent > receiptIssuance.arTo) {
                receiptIssuance.arCurrent = null
                receiptIssuance.aractive = false
            }
            receiptIssuanceService.save(receiptIssuance)

        }

        paymentTracker = save(paymentTracker)

        def hospitalItem = partitions.find {
            it.key == "HOSPITAL"
        }

        def forHospitalAmount = hospitalItem.value
        def forAllPFBalance = 0.0

        // PF Not Applied here... will be applied during reapplication
        /*billingService.balances(billingId).findAll { it.employeeid }.each {
            forAllPFBalance += it.balance
        }*/


        List<BillingItem> pfPayments = []
        List<BillingItem> hciPayments = []

        // Jan. 17, 2020
        // PF not prioritized

        /*def pfBalances = billingService.balances(billingId).findAll { it.employeeid }

        for(charges in pfBalances.findAll { it.balance > 0}){

            if(forHospitalAmount > 0){
                if(forHospitalAmount >= charges.balance){
                    forHospitalAmount -= charges.balance
                    pfPayments << billingService.addPayment(
                            billingId,
                            charges.balance,
                            charges.employeeid,
                            paymentTracker
                    )
                } else {
                    pfPayments << billingService.addPayment(
                            billingId,
                            forHospitalAmount,
                            charges.employeeid,
                            paymentTracker
                    )
                    forHospitalAmount = 0.0
                }
            } else {
                break
            }
        }*/


        if (billing?.patientCase?.registryType == "IPD" || billing.overrideProgressPayment) {
            if (forHospitalAmount > 0.0) {
                hciPayments << billingService.addPayment(
                        billingId,
                        forHospitalAmount,
                        null,
                        paymentTracker,
                        null,
                        false,
                        true
                )
            }
        } else {
            billingService.balances(billingId).findAll { it.employeeid }.each {
                forAllPFBalance += it.balance
            }

            def pfBalances = billingService.balances(billingId).findAll { it.employeeid }

            for (charges in pfBalances.findAll { it.balance > 0 }) {

                if (forHospitalAmount > 0) {
                    if (forHospitalAmount >= charges.balance) {
                        forHospitalAmount -= charges.balance
                        pfPayments << billingService.addPayment(
                                billingId,
                                charges.balance,
                                charges.employeeid,
                                paymentTracker
                        )
                    } else {
                        pfPayments << billingService.addPayment(
                                billingId,
                                forHospitalAmount,
                                charges.employeeid,
                                paymentTracker
                        )
                        forHospitalAmount = 0.0
                    }
                } else {
                    break
                }


            }


            if (forHospitalAmount > 0.0) {
                hciPayments << billingService.addPayment(
                        billingId,
                        forHospitalAmount,
                        null,
                        paymentTracker
                )
            }


        }


        // marked tagged Items for CASH BASIS approval

        taggedIds?.each {

            def billingItem = billingItemServices.findOne(it)
            billingItem.taggedOrNumber = paymentTracker.ornumber
            billingItem.taggedCashAllowedBy = SecurityUtils.currentLogin()
            billingItemServices.save(billingItem)

        }

        taggedIdsMeds.each {
            def cashBasisItem = entityManager.find(CashBasisItem.class, it)
            cashBasisItem.processed = true
            def jsonSlurper = new JsonSlurper()
            def data = (List<Map<String, Object>>) jsonSlurper.parseText(cashBasisItem.data)

            billingItemServices.addBillingItem(cashBasisItem.billing.id,
                    BillingItemType.valueOf(cashBasisItem.type),
                    data,
                    null,
                    departmentRepository.findById(cashBasisItem.departmentId).get(),
                    cashBasisItem.createdBy
            )

            entityManager.merge(cashBasisItem)
        }

        if (auto_post_journal) {
            postToAccounting(paymentTracker, pfPayments, hciPayments)
        }

    }

    // Save Override

    PaymentTracker postToAccountingMisc(PaymentTracker paymentTracker) {


        def allCoa = subAccountSetupService.getAllChartOfAccountGenerate("", "", "", "", "")

        def headerLedger = integrationServices.generateAutoEntries(paymentTracker) { PaymentTracker ptracker, multipleData ->

            ptracker.flagValue = "MISC_PAYMENTS"

            //Check for Credit Card

            List<PaymentTracker> forCC = []
            ptracker.paymentDetails.findAll { it.type == PaymentType.CARD }.each { pdt ->
                forCC << new PaymentTracker().tap {
                    it.amountForCreditCard = pdt.amount
                    it.bankForCreditCard = pdt.bankEntity
                }
            }

            ptracker.paymentDetails.findAll { it.type == PaymentType.EWALLET }.each { pdt ->
                forCC << new PaymentTracker().tap {
                    it.amountForCreditCard = pdt.amount
                    it.bankForCreditCard = pdt.bankEntity
                }
            }

            multipleData << forCC


            List<PaymentTracker> forBank = []
            ptracker.paymentDetails.findAll { it.type == PaymentType.BANKDEPOSIT }.each { pdt ->
                forBank << new PaymentTracker().tap {
                    it.amountForCashDeposit = pdt.amount
                    it.bankForCashDeposit = pdt.bankEntity
                }
            }


            multipleData << forBank
        }


        List<Entry> entries = []


        headerLedger.ledger.each {
            it.journalAccount.fromGenerator = true
            entries << new Entry(it.journalAccount, it.debit)
        }

        paymentTracker.paymentTargets.each { target ->

            def match = allCoa.find { it.code == target.journalCode }
            if (match) {
                if(match.motherAccount.normalSide.equalsIgnoreCase('debit'))
                    entries << new Entry(match, target.amount.negate())
                else
                    entries << new Entry(match, target.amount)
            }
        }


        headerLedger = ledgerServices.createDraftHeaderLedger(entries)

        def pHeader = ledgerServices.persistHeaderLedger(headerLedger,
                "${paymentTracker.receiptType.name()}-${paymentTracker.ornumber}",
                "${paymentTracker.description}",
                "${paymentTracker.ornumber}-${paymentTracker.description}",
                paymentTracker.receiptType == ReceiptType.AR ? LedgerDocType.AR : LedgerDocType.OR,
                JournalType.RECEIPTS,
                paymentTracker.createdDate,
                [:]
        )
        paymentTracker.ledgerHeader = pHeader.id
        save(paymentTracker)
    }

    PaymentTracker postToAccounting(PaymentTracker paymentTracker, List<BillingItem> pfPayments, List<BillingItem> hciPayments) {

        // only patient /otc hospital payments
        if (paymentTracker.billingid) {
            def billing = billingService.findOne(paymentTracker.billingid)
            def yearFormat = DateTimeFormatter.ofPattern("yyyy")
            Map<String, String> details = [:]

            details[BillingItemDetailParam.PAYTRACKER_ID.name()] = paymentTracker.id.toString()
            details[BillingItemDetailParam.ORNUMBER.name()] = paymentTracker.ornumber
            details[BillingItemDetailParam.BILLING_ID.name()] = paymentTracker.billingid.toString()


            if (billingService.isAllowedProgressPayment(billing) || billing.finalizedSoa) {

                def headerLedger = integrationServices.generateAutoEntries(paymentTracker) { PaymentTracker ptracker, multipleData ->

                    ptracker.flagValue = "HOSPITAL_PAYMENTS_MOD_1"

                    // Check on Hand and Cash on Hand Totals are enough


                    //Check for Credit Card

                    List<PaymentTracker> forCC = []
                    ptracker.paymentDetails.findAll { it.type == PaymentType.CARD }.each { pdt ->
                        forCC << new PaymentTracker().tap {
                            it.amountForCreditCard = pdt.amount
                            it.bankForCreditCard = pdt.bankEntity
                        }
                    }

                    ptracker.paymentDetails.findAll { it.type == PaymentType.EWALLET }.each { pdt ->
                        forCC << new PaymentTracker().tap {
                            it.amountForCreditCard = pdt.amount
                            it.bankForCreditCard = pdt.bankEntity
                        }
                    }

                    multipleData << forCC


                    List<PaymentTracker> forBank = []
                    ptracker.paymentDetails.findAll { it.type == PaymentType.BANKDEPOSIT }.each { pdt ->
                        forBank << new PaymentTracker().tap {
                            it.amountForCashDeposit = pdt.amount
                            it.bankForCashDeposit = pdt.bankEntity
                        }
                    }

                    multipleData << forBank

                    ptracker.advancesFromPatients = ptracker.totalpayments
                }


                def pHeader = ledgerServices.persistHeaderLedger(headerLedger,
                        "${billing.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${billing.billingNo}",
                        "${paymentTracker.description}",
                        "${paymentTracker.ornumber}-${paymentTracker.description}",
                        LedgerDocType.OR,
                        JournalType.RECEIPTS,
                        paymentTracker.createdDate,
                        details
                )


                paymentTracker.ledgerHeader = pHeader.id

            } else {

                def headerLedger = integrationServices.generateAutoEntries(paymentTracker) { PaymentTracker ptracker, multipleData ->
                    ptracker.flagValue = "HOSPITAL_PAYMENTS"
                    // Check on Hand and Cash on Hand Totals are enough

                    //Check for Credit Card

                    List<PaymentTracker> forCC = []
                    ptracker.paymentDetails.findAll { it.type == PaymentType.CARD }.each { pdt ->
                        forCC << new PaymentTracker().tap {
                            it.amountForCreditCard = pdt.amount
                            it.bankForCreditCard = pdt.bankEntity
                        }
                    }

                    ptracker.paymentDetails.findAll { it.type == PaymentType.EWALLET }.each { pdt ->
                        forCC << new PaymentTracker().tap {
                            it.amountForCreditCard = pdt.amount
                            it.bankForCreditCard = pdt.bankEntity
                        }
                    }

                    multipleData << forCC


                    List<PaymentTracker> forBank = []
                    ptracker.paymentDetails.findAll { it.type == PaymentType.BANKDEPOSIT }.each { pdt ->
                        forBank << new PaymentTracker().tap {
                            it.amountForCashDeposit = pdt.amount
                            it.bankForCashDeposit = pdt.bankEntity
                        }
                    }


                    multipleData << forBank

                    //IPD ERD OPD OTC
                    // get all ER Payments

                    ptracker.erPayments = 0.0
                    ptracker.opdPayments = 0.0
                    ptracker.ipdPayments = 0.0
                    ptracker.otcPayments = 0.0

                    hciPayments.each {
                        it.amountdetails.each { k, v ->
                            BillingItem bItem = billing.billingItemList.find { it.id == UUID.fromString(k) }
                            if (bItem) {
                                if (bItem.registryTypeCharged == "ERD")
                                    ptracker.erPayments += v

                                if (bItem.registryTypeCharged == "OPD")
                                    ptracker.opdPayments += v

                                if (bItem.registryTypeCharged == "IPD")
                                    ptracker.ipdPayments += v

                                if (bItem.registryTypeCharged == "OTC")
                                    ptracker.otcPayments += v
                            }
                        }
                    }

                    ptracker.erPayments *= -1
                    ptracker.opdPayments *= -1
                    ptracker.ipdPayments *= -1
                    ptracker.otcPayments *= -1

                    ptracker.pfPaymentsAll = 0.0
                    pfPayments.each {
                        ptracker.pfPaymentsAll += it.subTotal.abs()
                    }


                }


                def pHeader = ledgerServices.persistHeaderLedger(headerLedger,
                        "${billing.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${billing.billingNo}",
                        "${paymentTracker.description}",
                        "${paymentTracker.ornumber}-${paymentTracker.description}",
                        LedgerDocType.OR,
                        JournalType.RECEIPTS,
                        paymentTracker.createdDate,
                        details
                )


                paymentTracker.ledgerHeader = pHeader.id

            }


        }

        paymentTracker
    }


    @Transactional
    void postToAccountingInvestorPayment(List<InvestorPaymentLedger> ledgers, PaymentTracker paymentTracker) {

        def yearFormat = DateTimeFormatter.ofPattern("yyyy")

        BigDecimal subscriptionReceivable = 0
        BigDecimal subscribedShareCapital = 0
        BigDecimal shareCapital = 0
        BigDecimal advancesFromInvestors = 0
        BigDecimal discountOnShareCapital = 0

        ledgers.each {
            subscriptionReceivable += it.subscriptionReceivable ?: 0
            subscribedShareCapital += it.subscribedShareCapital ?: 0
            shareCapital += it.shareCapital ?: 0
            advancesFromInvestors += it.advancesFromInvestors ?: 0
            discountOnShareCapital += it.discountOnShareCapital ?: 0
        }

        def headerLedger = integrationServices.generateAutoEntries(paymentTracker) { it, multipleData ->
            it.flagValue = "INVESTOR_PAYMENT"

            // Check on Hand and Cash on Hand Totals are enough

            //Check for Credit Card
            List<PaymentTracker> forCC = []
            it.paymentDetails.findAll { it.type == PaymentType.CARD }.each { pdt ->
                forCC << new PaymentTracker().tap {
                    it.amountForCreditCard = pdt.amount
                    it.bankForCreditCard = pdt.bankEntity
                }
            }

            it.paymentDetails.findAll { it.type == PaymentType.EWALLET }.each { pdt ->
                forCC << new PaymentTracker().tap {
                    it.amountForCreditCard = pdt.amount
                    it.bankForCreditCard = pdt.bankEntity
                }
            }

            multipleData << forCC

            List<PaymentTracker> forBank = []
            it.paymentDetails.findAll { it.type == PaymentType.BANKDEPOSIT }.each { pdt ->
                forBank << new PaymentTracker().tap {
                    it.amountForCashDeposit = pdt.amount
                    it.bankForCashDeposit = pdt.bankEntity
                }
            }

            multipleData << forBank

            it.subscriptionReceivable = subscriptionReceivable
            it.subscribedShareCapital = subscribedShareCapital
            it.shareCapital = shareCapital
            it.advancesFromInvestors = advancesFromInvestors
            it.discountOnShareCapital = discountOnShareCapital

        }

        Map<String, String> details = [:]
        paymentTracker.details.each { k, v ->
            details[k] = v
        }
        Investor investor = null
        String ledgersIds = ""
        investorsRepository.findById(paymentTracker.investorId).ifPresent { investor = it }

        ledgersIds = ledgers.stream().map { it.id.toString() }.collect(Collectors.joining(","))

        details["PAYMENT_LEDGER_IDS"] = ledgersIds
        details["INVESTOR"] = investor.id.toString()
        def pHeader = ledgerServices.persistHeaderLedger(headerLedger,
                "${paymentTracker.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${investor.investorNo}",
                "${investor.fullName}",
                "SUBSCRIPTION PAYMENT",
                LedgerDocType.JV,
                JournalType.GENERAL,
                paymentTracker.createdDate,
                details)
        paymentTracker.ledgerHeader = pHeader.id

        save(paymentTracker)


    }

    @Transactional
    void postToAccountingInvestorIssuanceOfShares(InvestorPaymentLedger paymentLedger) {
        def yearFormat = DateTimeFormatter.ofPattern("yyyy")
        def headerLedger = integrationServices.generateAutoEntries(paymentLedger) { it, multipleData ->
            it.flagValue = "INVESTOR_ISSUANCE_OF_SHARES"
            it.shareCapitalCommonShare = it.shareCapitalCommonShare.abs()
            it.shareCapitalPremium = it.shareCapitalPremium.abs()
            it.discountOnShareCapital = it.discountOnShareCapital.negate()
        }

        Map<String, String> details = [:]
        paymentLedger.details.each { k, v ->
            details[k] = v
        }

        details["PAYMENT_LEDGER_ID"] = paymentLedger.id.toString()

        def pHeader = ledgerServices.persistHeaderLedger(headerLedger,
                "${paymentLedger.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${paymentLedger.investor.investorNo}",
                "${paymentLedger.investor.fullName}",
                "SUBSCRIPTION ISSUANCE OF SHARES",
                LedgerDocType.JV,
                JournalType.GENERAL,
                paymentLedger.createdDate,
                details)
        paymentLedger.ledgerHeader = pHeader.id

        investorPaymentLedgerRepository.save(paymentLedger)


    }




}
