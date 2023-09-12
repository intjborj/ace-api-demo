package com.hisd3.hismk2.graphqlservices.accounting

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.accounting.AccountsPayable
import com.hisd3.hismk2.domain.accounting.ApPfCompany
import com.hisd3.hismk2.domain.accounting.ApPfNonCompany
import com.hisd3.hismk2.domain.accounting.ApReadersFee
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.billing.Subscription
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
import com.hisd3.hismk2.domain.inventory.Supplier
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.inventory.SupplierService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.inventory.ReceivingReportRepository
import com.hisd3.hismk2.repository.inventory.SupplierRepository
import com.hisd3.hismk2.rest.dto.AccountPayableDetialsDto
import com.hisd3.hismk2.rest.dto.ApAgingDetailedDto
import com.hisd3.hismk2.rest.dto.ApAgingSummaryDto
import com.hisd3.hismk2.rest.dto.ApLedgerDto
import com.hisd3.hismk2.rest.dto.DepDto
import com.hisd3.hismk2.rest.dto.JournalEntryViewDto
import com.hisd3.hismk2.rest.dto.PayablePFDto
import com.hisd3.hismk2.rest.dto.TransTypeDto
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
@GraphQLApi
class AccountsPayableServices extends AbstractDaoService<AccountsPayable> {

    @Autowired
    GeneratorService generatorService

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    AccountsPayableDetialServices accountsPayableDetialServices

    @Autowired
    ApLedgerServices apLedgerServices

    @Autowired
    JdbcTemplate jdbcTemplate

    @Autowired
    ReceivingReportRepository receivingReportRepository

    @Autowired
    IntegrationServices integrationServices

    @Autowired
    LedgerServices ledgerServices

    @Autowired
    Wtx2307Service wtx2307Service

    @Autowired
    SupplierRepository supplierRepository

    @Autowired
    SupplierService supplierService

    @Autowired
    BillingItemServices billingItemServices

    @Autowired
    AccountReceivableItemsServices accountReceivableItemsServices


	AccountsPayableServices() {
		super(AccountsPayable.class)
	}
	
	@GraphQLQuery(name = "apById")
	AccountsPayable apById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}
	
	@GraphQLQuery(name = "apListPosted", description = "Find Ap posted")
	List<AccountsPayable> apListPosted() {
		createQuery("Select ap from AccountsPayable ap where ap.posted = true").resultList
	}

	@GraphQLQuery(name = "apListBySupplierFilter", description = "List of AP Pageable By Supplier")
	Page<AccountsPayable> apListBySupplierFilter(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "supplier") UUID supplier,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

        String query = '''Select ap from AccountsPayable ap where
							ap.posted = true and
						( lower(ap.apNo) like lower(concat('%',:filter,'%')) or
						lower(ap.invoiceNo) like lower(concat('%',:filter,'%')) )'''

        String countQuery = '''Select count(ap) from AccountsPayable ap where
							ap.posted = true and
							( lower(ap.apNo) like lower(concat('%',:filter,'%')) or
						lower(ap.invoiceNo) like lower(concat('%',:filter,'%')) ) '''

        Map<String, Object> params = new HashMap<>()
        params.put('filter', filter)

        if (supplier) {
            query += ''' and (ap.supplier.id = :supplier) '''
            countQuery += ''' and (ap.supplier.id = :supplier) '''
            params.put("supplier", supplier)
        }

        query += ''' ORDER BY ap.apvDate DESC'''

        getPageable(query, countQuery, page, size, params)
    }

    @GraphQLQuery(name = "apListFilter", description = "List of AP Pageable")
    Page<AccountsPayable> apListFilter(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "supplier") UUID supplier,
            @GraphQLArgument(name = "status") Boolean status,
            @GraphQLArgument(name = "start") String start,
            @GraphQLArgument(name = "end") String end,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ) {

        String query = '''Select ap from AccountsPayable ap where
						( lower(ap.apNo) like lower(concat('%',:filter,'%')) or
						lower(ap.invoiceNo) like lower(concat('%',:filter,'%')))
						and to_date(to_char(ap.apvDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD')'''

        String countQuery = '''Select count(ap) from AccountsPayable ap where
							( lower(ap.apNo) like lower(concat('%',:filter,'%')) or
						lower(ap.invoiceNo) like lower(concat('%',:filter,'%')))
							and to_date(to_char(ap.apvDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD')'''

        Map<String, Object> params = new HashMap<>()
        params.put('filter', filter)
        params.put('start', start)
        params.put('end', end)

        if (supplier) {
            query += ''' and (ap.supplier.id = :supplier) '''
            countQuery += ''' and (ap.supplier.id = :supplier) '''
            params.put("supplier", supplier)
        }

        if (status) {
            query += ''' and (ap.posted = :status or ap.posted is null) '''
            countQuery += ''' and (ap.posted = :status or ap.posted is null) '''
            params.put("status", !status)
        }

        query += ''' ORDER BY ap.apvDate DESC'''

        getPageable(query, countQuery, page, size, params)
    }

    //mutations
    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "upsertPayables")
    AccountsPayable upsertPayables(
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "items") ArrayList<Map<String, Object>> items,
            @GraphQLArgument(name = "id") UUID id
    ) {
        def apCat = 'AP'
        def ap = upsertFromMap(id, fields, { AccountsPayable entity, boolean forInsert ->
            if (forInsert) {
                if (entity.apCategory.equalsIgnoreCase("PROFESSIONAL FEE")) {
                    apCat = 'PF'
                }
                if (entity.apCategory.equalsIgnoreCase("READER'S FEE")) {
                    apCat = 'RF'
                }
                entity.apNo = generatorService.getNextValue(GeneratorType.APNO, {
                    return "${apCat}-" + StringUtils.leftPad(it.toString(), 6, "0")
                })
                entity.appliedAmount = 0
                entity.status = "DRAFT"
                entity.posted = false

                //round numbers to 2 decimal
                entity.grossAmount = entity.grossAmount.round(2)
                entity.discountAmount = entity.discountAmount.round(2)
                entity.netOfDiscount = entity.netOfDiscount.round(2)
                entity.vatAmount = entity.vatAmount.round(2)
                entity.netOfVat = entity.netOfVat.round(2)
                entity.ewtAmount = entity.ewtAmount.round(2)
                entity.netAmount = entity.netAmount.round(2)
                //
                entity.daAmount = BigDecimal.ZERO
                entity.dmAmount = BigDecimal.ZERO
            } else {
                //round numbers to 2 decimal
                entity.grossAmount = entity.grossAmount.round(2)
                entity.discountAmount = entity.discountAmount.round(2)
                entity.netOfDiscount = entity.netOfDiscount.round(2)
                entity.vatAmount = entity.vatAmount.round(2)
                entity.netOfVat = entity.netOfVat.round(2)
                entity.ewtAmount = entity.ewtAmount.round(2)
                entity.netAmount = entity.netAmount.round(2)
            }
        })

        def apDetails = items as ArrayList<AccountPayableDetialsDto>
        apDetails.each {
            it ->
                def trans = objectMapper.convertValue(it.transType, TransTypeDto.class)
                def apDto = objectMapper.convertValue(it, AccountPayableDetialsDto.class)
                def dep = null
                if (it.department) {
                    dep = objectMapper.convertValue(it.department, DepDto.class)
                }
                accountsPayableDetialServices.upsertPayablesDetails(apDto, ap, trans?.id, dep?.id)
        }

        return ap
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "upsertPayablesByRec")
    AccountsPayable upsertPayablesByRec(
            @GraphQLArgument(name = "id") UUID id
    ) {
        def apCat = 'AP'
        def rec = receivingReportRepository.findById(id).get()

		AccountsPayable ap = new AccountsPayable()
		if(rec.refAp){
			ap = findOne(rec.refAp)
		}
		//upsert only if not posted
		if(!ap.posted){
			if(!rec.refAp) {
				ap.apNo = generatorService.getNextValue(GeneratorType.APNO, {
					return "${apCat}-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
				ap.apvDate = Instant.now().plus(Duration.ofHours(8))
				ap.dueDate = Instant.now().plus(Duration.ofHours(8))
				ap.apCategory = "ACCOUNTS PAYABLE"
				ap.ewtAmount = BigDecimal.ZERO
				ap.daAmount = BigDecimal.ZERO
				ap.dmAmount = BigDecimal.ZERO
				ap.appliedAmount = BigDecimal.ZERO
				ap.status = "DRAFT"
				ap.remarksNotes = rec.rrNo
				ap.posted = false
			}
			ap.receiving = rec
			ap.supplier = rec.supplier
			ap.paymentTerms = rec.paymentTerms
			ap.invoiceNo = rec.receivedRefNo
			ap.grossAmount = rec.grossAmount
			ap.discountAmount = rec.totalDiscount
			ap.netOfDiscount = rec.netDiscount
			ap.vatRate = rec.vatRate
			ap.vatAmount = rec.inputTax
			ap.netOfVat = rec.vatInclusive ? rec.netDiscount : rec.amount
			ap.netAmount = rec.amount

			def afterSave = save(ap)

			if(!rec.refAp){
				//ap details
				accountsPayableDetialServices.upsertPayablesDetailsByRec(rec, afterSave)

				//update receiving has ap
				rec.refAp = afterSave.id
				receivingReportRepository.save(rec)
			}
		}

		return ap
	}


    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "upsertPayablesByInvestor")
    AccountsPayable upsertPayablesByInvestor(
            @GraphQLArgument(name = "reference") Object reference,
            @GraphQLArgument(name = "type") String type,
            @GraphQLArgument(name = "amount") BigDecimal amount

    ) {
        def apCat = 'AP'

        UUID investorId = null
        UUID refId = null
        String refNo = null
        String remarksNotes = ''
        if(type.equalsIgnoreCase('overpaid')) {
            PaymentTracker paymentTracker = (reference as PaymentTracker)
            investorId = paymentTracker.investorId
            refId = paymentTracker.id
            refNo = "${paymentTracker.receiptType} # "+ paymentTracker.ornumber
            remarksNotes= 'Investor Refund'
        }
        else {
            Subscription subscription = (reference as Subscription)
            investorId = subscription.investor.id
            refId = subscription.id
            refNo = "Subscription # "+ subscription.subscriptionCode
            remarksNotes = 'Investor Rebate'
        }

        Supplier supplier = supplierRepository.findByInvestorId(investorId)
        if (!supplier) {
            supplier = supplierService.upsertSupplierByInvestor(investorId)
        }

        AccountsPayable ap = new AccountsPayable()
        ap.receiving = null
        ap.supplier = supplier
        ap.apNo = generatorService.getNextValue(GeneratorType.APNO, {
            return "${apCat}-" + StringUtils.leftPad(it.toString(), 6, "0")
        })
        ap.apCategory = "ACCOUNTS PAYABLE"
        ap.paymentTerms = null
        ap.apvDate = Instant.now().plus(Duration.ofHours(8))
        ap.dueDate = Instant.now().plus(Duration.ofHours(8))
        ap.invoiceNo = ""//ref number nis investor
        ap.grossAmount = amount //whole amount
        ap.discountAmount = BigDecimal.ZERO
        ap.netOfDiscount = BigDecimal.ZERO
        ap.vatRate = 12
        ap.vatAmount = BigDecimal.ZERO
        ap.netOfVat = BigDecimal.ZERO // same sa gross amount
        ap.ewtAmount = BigDecimal.ZERO
        ap.netAmount = amount// same sa gross amount
        ap.daAmount = BigDecimal.ZERO
        ap.dmAmount = BigDecimal.ZERO
        ap.appliedAmount = BigDecimal.ZERO
        ap.status = "DRAFT"
        ap.remarksNotes = remarksNotes
        ap.posted = false
        def afterSave = save(ap)

        accountsPayableDetialServices.upsertPayablesDetailsByInvestor(amount, refId,refNo, afterSave)
        return ap
    }

    @GraphQLQuery(name = "apAccountView")
    List<JournalEntryViewDto> apAccountView(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "status") Boolean status
    ) {
        def result = new ArrayList<JournalEntryViewDto>()
        //ewt rate
        if (id) {
            def actPay = findOne(id)
            def ewt1 = BigDecimal.ZERO; def ewt2 = BigDecimal.ZERO; def ewt3 = BigDecimal.ZERO
            def ewt4 = BigDecimal.ZERO; def ewt5 = BigDecimal.ZERO; def ewt7 = BigDecimal.ZERO
            def ewt10 = BigDecimal.ZERO; def ewt15 = BigDecimal.ZERO; def ewt18 = BigDecimal.ZERO
            def ewt30 = BigDecimal.ZERO

            def actPayDetials = accountsPayableDetialServices.detailsByAp(actPay.id)
            actPayDetials.each {
                switch (it.ewtRate) {
                    case 1:
                        ewt1 += it.ewtAmount
                        break
                    case 2:
                        ewt2 += it.ewtAmount
                        break
                    case 3:
                        ewt3 += it.ewtAmount
                        break
                    case 4:
                        ewt4 += it.ewtAmount
                        break
                    case 5:
                        ewt5 += it.ewtAmount
                        break
                    case 7:
                        ewt7 += it.ewtAmount
                        break
                    case 10:
                        ewt10 += it.ewtAmount
                        break
                    case 15:
                        ewt15 += it.ewtAmount
                        break
                    case 18:
                        ewt18 += it.ewtAmount
                        break
                    case 30:
                        ewt30 += it.ewtAmount
                        break
                }
            }
            //ewt rate
            if (actPay.transType?.flagValue) {
                def headerLedger = integrationServices.generateAutoEntries(actPay) { it, mul ->
                    it.flagValue = actPay.transType?.flagValue
                    //initialize
                    if (actPay.apCategory.equalsIgnoreCase("ACCOUNTS PAYABLE")) {
                        BigDecimal clearing = actPay.netOfVat.round(2) + actPay.discountAmount.round(2)
                        it.clearingAmount = status ? (clearing * -1) : clearing
                        // credit normal side make it negative to debit
                        it.discAmount = status ? actPay.discountAmount.round(2) : actPay.discountAmount.round(2) * -1
                        it.supplierAmount = status ? actPay.netAmount.round(2) : actPay.netAmount.round(2) * -1
                    } else {
                        it.doctorFee = status ? (actPay.netOfVat.round(2) * -1) : actPay.netOfVat.round(2)
                        //debit (normal side credit)
                        it.readersFee = status ? actPay.netOfVat.round(2) : (actPay.netOfVat.round(2) * -1)
                        //debit normal side
                        it.dueToDoctors = status ? actPay.netAmount.round(2) : (actPay.netAmount.round(2) * -1)//credit
                    }

                    //ewt
                    it.ewt1Percent = status ? ewt1.round(2) : ewt1.round(2) * -1
                    it.ewt2Percent = status ? ewt2.round(2) : ewt2.round(2) * -1
                    it.ewt3Percent = status ? ewt3.round(2) : ewt3.round(2) * -1
                    it.ewt4Percent = status ? ewt4.round(2) : ewt4.round(2) * -1
                    it.ewt5Percent = status ? ewt5.round(2) : ewt5.round(2) * -1
                    it.ewt7Percent = status ? ewt7.round(2) : ewt7.round(2) * -1
                    it.ewt10Percent = status ? ewt10.round(2) : ewt10.round(2) * -1
                    it.ewt15Percent = status ? ewt15.round(2) : ewt15.round(2) * -1
                    it.ewt18Percent = status ? ewt18.round(2) : ewt18.round(2) * -1
                    it.ewt30Percent = status ? ewt30.round(2) : ewt30.round(2) * -1
                }

                Set<Ledger> ledger = new HashSet<Ledger>(headerLedger.ledger);
                ledger.each {
                    def list = new JournalEntryViewDto(
                            code: it.journalAccount.code,
                            desc: it.journalAccount.description,
                            debit: it.debit,
                            credit: it.credit
                    )
                    result.add(list)
                }
            } else {
                if (actPay.postedLedger) {
                    def header = ledgerServices.findOne(actPay.postedLedger)
                    Set<Ledger> ledger = new HashSet<Ledger>(header.ledger);
                    ledger.each {
                        def list = new JournalEntryViewDto(
                                code: it.journalAccount.code,
                                desc: it.journalAccount.description,
                                debit: it.credit,
                                credit: it.debit
                        )
                        result.add(list)
                    }
                }
            }
        }
        return result.sort { it.credit }
    }


    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "postAp")
    AccountsPayable postAp(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "status") Boolean status
    ) {
        def apCat = 'AP'
        def ap = findOne(id)
        if (ap.apCategory.equalsIgnoreCase("PROFESSIONAL FEE")) {
            apCat = 'PF'
        }
        if (ap.apCategory.equalsIgnoreCase("READER'S FEE")) {
            apCat = 'RF'
        }
        if (status) {
            def header = ledgerServices.findOne(ap.postedLedger)
            ledgerServices.reverseEntriesCustom(header, ap.apvDate)
            //update AP
            ap.postedLedger = null
            ap.status = "DRAFT"
            ap.posted = false
            ap.postedBy = null
            save(ap)
            //remove ap ledger
            apLedgerServices.removeApLedger(ap.apNo)
            if (ap.ewtAmount > BigDecimal.ZERO) {
                wtx2307Service.remove2307(ap.id)
            }
            //end remover ap ledger
        } else {
            postToLedgerAccounting(ap)

            //add to ap ledger
            Map<String, Object> ledger = new HashMap<>()
            ledger.put('ledgerType', apCat)
            ledger.put('refNo', ap?.apNo)
            ledger.put('refId', ap?.id)
            ledger.put('debit', 0.00)
            ledger.put('credit', ap?.netAmount)
            apLedgerServices.upsertApLedger(ledger, ap?.supplier?.id, null)
            //end to ap ledger

            //insert if ewt is not zero
            if (ap.ewtAmount > BigDecimal.ZERO) {
                Map<String, Object> ewt = new HashMap<>()
                ewt.put('refId', ap.id)
                ewt.put('refNo', ap.apNo)
                ewt.put('wtxDate', ap.apvDate)
                ewt.put('type', 'AP') //AP, DIS, RP, AROTHERS
                ewt.put('gross', ap.grossAmount) //net of discount
                ewt.put('vatAmount', ap.vatAmount) // 0
                ewt.put('netVat', ap.netOfVat) // same by gross
                ewt.put('ewtAmount', ap.ewtAmount) //ewt amounnt
                wtx2307Service.upsert2307(ewt, null, ap.supplier.id)
            }
        }
        return ap
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "postApManual")
    GraphQLRetVal<Boolean> postApManual(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "header") Map<String, Object> header,
            @GraphQLArgument(name = "entries") List<Map<String, Object>> entries
    ) {
        def ap = findOne(id)
        def apCat = 'AP'
        if (ap.apCategory.equalsIgnoreCase("PROFESSIONAL FEE")) {
            apCat = 'PF'
        }
        if (ap.apCategory.equalsIgnoreCase("READER'S FEE")) {
            apCat = 'RF'
        }

        Map<String, String> details = [:]

        ap.details.each { k, v ->
            details[k] = v
        }

        details["ACC_PAYABLE_ID"] = ap.id.toString()
        details["SUPPLIER_ID"] = ap.supplier.id.toString()

        def result = ledgerServices.addManualJVDynamic(header, entries, LedgerDocType.AP,
                JournalType.PURCHASES_PAYABLES, ap.apvDate, details)

        //update parent
        ap.postedLedger = result.returnId
        ap.status = "POSTED"
        ap.posted = true
        ap.postedBy = SecurityUtils.currentLogin()
        save(ap)

        //add to ap ledger
        Map<String, Object> ledger = new HashMap<>()
        ledger.put('ledgerType', apCat)
        ledger.put('refNo', ap?.apNo)
        ledger.put('refId', ap?.id)
        ledger.put('debit', 0.00)
        ledger.put('credit', ap?.netAmount)
        apLedgerServices.upsertApLedger(ledger, ap?.supplier?.id, null)
        //end to ap ledger

        //insert if ewt is not zero
        if (ap.ewtAmount > BigDecimal.ZERO) {
            Map<String, Object> ewt = new HashMap<>()
            ewt.put('refId', ap.id)
            ewt.put('refNo', ap.apNo)
            ewt.put('wtxDate', ap.apvDate)
            ewt.put('type', 'AP') //AP, DIS, RP, AROTHERS
            ewt.put('gross', ap.grossAmount) //net of discount
            ewt.put('vatAmount', ap.vatAmount) // 0
            ewt.put('netVat', ap.netOfVat) // same by gross
            ewt.put('ewtAmount', ap.ewtAmount) //ewt amounnt
            wtx2307Service.upsert2307(ewt, null, ap.supplier.id)
        }

        return result
    }


    //save to accounting in post
    @Transactional(rollbackFor = Exception.class)
    AccountsPayable postToLedgerAccounting(AccountsPayable accountsPayable) {
        def yearFormat = DateTimeFormatter.ofPattern("yyyy")
        def actPay = super.save(accountsPayable) as AccountsPayable
        //ewt rate
        def ewt1 = BigDecimal.ZERO; def ewt2 = BigDecimal.ZERO; def ewt3 = BigDecimal.ZERO
        def ewt4 = BigDecimal.ZERO; def ewt5 = BigDecimal.ZERO; def ewt7 = BigDecimal.ZERO
        def ewt10 = BigDecimal.ZERO; def ewt15 = BigDecimal.ZERO; def ewt18 = BigDecimal.ZERO
        def ewt30 = BigDecimal.ZERO
        def actPayDetials = accountsPayableDetialServices.detailsByAp(actPay.id)
        actPayDetials.each {
            switch (it.ewtRate) {
                case 1:
                    ewt1 += it.ewtAmount
                    break
                case 2:
                    ewt2 += it.ewtAmount
                    break
                case 3:
                    ewt3 += it.ewtAmount
                    break
                case 4:
                    ewt4 += it.ewtAmount
                    break
                case 5:
                    ewt5 += it.ewtAmount
                    break
                case 7:
                    ewt7 += it.ewtAmount
                    break
                case 10:
                    ewt10 += it.ewtAmount
                    break
                case 15:
                    ewt15 += it.ewtAmount
                    break
                case 18:
                    ewt18 += it.ewtAmount
                    break
                case 30:
                    ewt30 += it.ewtAmount
                    break
            }
        }
        //ewt rate

        def headerLedger = integrationServices.generateAutoEntries(accountsPayable) { it, mul ->
            it.flagValue = actPay.transType?.flagValue
            //initialize

            if (actPay.apCategory.equalsIgnoreCase("ACCOUNTS PAYABLE")) {
                BigDecimal clearing = actPay.netOfVat.round(2) + actPay.discountAmount.round(2)
                it.clearingAmount = clearing * -1 // credit normal side make it negative to debit
                it.discAmount = actPay.discountAmount.round(2)
                it.supplierAmount = actPay.netAmount.round(2)
            } else {
                it.doctorFee = actPay.netOfVat.round(2) * -1 //debit (credit normal side)
                it.readersFee = actPay.netOfVat.round(2) //debit normal side
                it.dueToDoctors = actPay.netAmount.round(2) //credit
            }

            //ewt
            it.ewt1Percent = ewt1.round(2)
            it.ewt2Percent = ewt2.round(2)
            it.ewt3Percent = ewt3.round(2)
            it.ewt4Percent = ewt4.round(2)
            it.ewt5Percent = ewt5.round(2)
            it.ewt7Percent = ewt7.round(2)
            it.ewt10Percent = ewt10.round(2)
            it.ewt15Percent = ewt15.round(2)
            it.ewt18Percent = ewt18.round(2)
            it.ewt30Percent = ewt30.round(2)
        }
        Map<String, String> details = [:]

        actPay.details.each { k, v ->
            details[k] = v
        }

        details["ACC_PAYABLE_ID"] = actPay.id.toString()
        details["SUPPLIER_ID"] = actPay.supplier.id.toString()

        def pHeader = ledgerServices.persistHeaderLedger(headerLedger,
                "${actPay.apvDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${actPay.apNo}",
                "${actPay.apNo}-${actPay.supplier.supplierFullname}",
                "${actPay.apNo}-${actPay.remarksNotes}",
                LedgerDocType.AP,
                JournalType.PURCHASES_PAYABLES,
                actPay.apvDate,
                details)

        actPay.postedLedger = pHeader.id
        actPay.status = "POSTED"
        actPay.posted = true
        actPay.postedBy = SecurityUtils.currentLogin()

//		if(actPay.supplierAmount < 0.0)
//		{
//			pHeader.reversal = true
//			ledgerServices.save(pHeader)
//		}
        save(actPay)

    }

    //update Ap
    @Transactional(rollbackFor = Exception.class)
    AccountsPayable updateAp(UUID id, String disNo, BigDecimal applied) {
        def ap = findOne(id)
        ap.disbursement = ap.disbursement ? ap.disbursement + ',' + disNo : disNo
        ap.appliedAmount = ap.appliedAmount + applied
        save(ap)
    }


    @Transactional(rollbackFor = Exception.class)
    AccountsPayable updateApForRemove(UUID id, String disNo, BigDecimal applied, Boolean posted) {
        def ap = findOne(id)
        //remove
        def str = ap.disbursement ? ap.disbursement.split(",") : null
        def dis = null
        if (str) {
            List<String> al = Arrays.asList(str) //initialize list
            al.indexOf(disNo)
            def count = al.size()
            if (count > 1) {
                al.each {
                    ap.disbursement = dis ? dis + ',' + it : it
                }
            } else {
                ap.disbursement = null
            }
        }
        //end remove
        if (posted) {
            ap.appliedAmount = ap.appliedAmount - applied
        }
        save(ap)
        return ap
    }

    @Transactional(rollbackFor = Exception.class)
    AccountsPayable updateApFromDM(UUID id, String dmNo, BigDecimal applied, String type) {
        def ap = findOne(id)
        ap.dmRefNo = ap.dmRefNo ? ap.dmRefNo + ',' + dmNo : dmNo
        if (type.equalsIgnoreCase("DA")) {
            ap.daAmount = ap.daAmount + applied
        } else {
            ap.dmAmount = ap.dmAmount + applied
        }
        save(ap)
    }

    @Transactional(rollbackFor = Exception.class)
    AccountsPayable updateApForRemoveDM(UUID id, String dmNo, BigDecimal applied, Boolean posted, String type) {
        def ap = findOne(id)
        //remove
        def str = ap.dmRefNo ? ap.dmRefNo.split(",") : null
        def dm = null
        if (str) {
            List<String> al = Arrays.asList(str) //initialize list
            al.indexOf(dmNo)
            def count = al.size()
            if (count > 1) {
                al.each {
                    ap.dmRefNo = dm ? dm + ',' + it : it
                }
            } else {
                ap.dmRefNo = null
            }
        }
        //end remove
        if (posted) {
            if (type.equalsIgnoreCase("DA")) {
                ap.daAmount = ap.daAmount - applied
            } else {
                ap.dmAmount = ap.dmAmount - applied
            }
        }
        save(ap)
        return ap
    }


    //upsert from PF COMPANY
    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "upsertFromPf")
    AccountsPayable upsertFromPf(
            @GraphQLArgument(name = "parent") Map<String, Object> parent,
            @GraphQLArgument(name = "pf") ArrayList<Map<String, Object>> pf
    ) {
        def ap = new AccountsPayable()
        def data = objectMapper.convertValue(parent, PayablePFDto.class)

        def sup = supplierRepository.findById(data.supplier).get()

        def vatRate = sup.isVatable ? 0.12 : 0.00
        def vatAmount = calculateVat(sup.isVatInclusive, data.totalPf, vatRate)
        def ewtAmount = calculateEwt(sup.isVatInclusive, data.totalPf, vatRate, sup.ewtRate)

        ap.supplier = sup
        ap.apNo = generatorService.getNextValue(GeneratorType.APNO, {
            return "PF-" + StringUtils.leftPad(it.toString(), 6, "0")
        })
        ap.apCategory = "PROFESSIONAL FEE"
        ap.paymentTerms = null
        ap.apvDate = Instant.now().plus(Duration.ofHours(8))
        ap.dueDate = Instant.now().plus(Duration.ofHours(8))
        ap.invoiceNo = data.refNo
        ap.grossAmount = data.totalPf.round(2)
        ap.discountAmount = BigDecimal.ZERO
        ap.netOfDiscount = data.totalPf.round(2)
        ap.vatRate = sup.isVatable ? 12.00 : 0.00
        ap.vatAmount = vatAmount
        ap.netOfVat = data.totalPf.round(2)
        ap.ewtAmount = ewtAmount
        ap.netAmount = (data.totalPf - ewtAmount).round(2)
        ap.appliedAmount = BigDecimal.ZERO
        ap.daAmount = BigDecimal.ZERO
        ap.dmAmount = BigDecimal.ZERO
        ap.status = "DRAFT"
        ap.remarksNotes = data.refNo
        ap.posted = false
        def afterSave = save(ap)

        def apDetails = pf as ArrayList<ApPfCompany>
        apDetails.each {
            it ->
                def detials = objectMapper.convertValue(it, ApPfCompany.class)
                accountsPayableDetialServices.upsertPfDetials(afterSave, detials)
                //update ar items
                accountReceivableItemsServices.updateApProcess(detials.id, true)
        }


        return ap
    }

    //upsert from PF non Company
    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "upsertFromPfNonCompany")
    AccountsPayable upsertFromPfNonCompany(
            @GraphQLArgument(name = "parent") Map<String, Object> parent,
            @GraphQLArgument(name = "pf") ArrayList<Map<String, Object>> pf
    ) {
        def ap = new AccountsPayable()
        def data = objectMapper.convertValue(parent, PayablePFDto.class)
        def sup = supplierRepository.findById(data.supplier).get()

        def vatRate = sup.isVatable ? 0.12 : 0.00
        def vatAmount = calculateVat(sup.isVatInclusive, data.totalPf, vatRate)
        def ewtAmount = calculateEwt(sup.isVatInclusive, data.totalPf, vatRate, sup.ewtRate)

        ap.supplier = sup
        ap.apNo = generatorService.getNextValue(GeneratorType.APNO, {
            return "PF-" + StringUtils.leftPad(it.toString(), 6, "0")
        })
        ap.apCategory = "PROFESSIONAL FEE"
        ap.paymentTerms = null
        ap.apvDate = Instant.now().plus(Duration.ofHours(8))
        ap.dueDate = Instant.now().plus(Duration.ofHours(8))
        ap.invoiceNo = data.reference
        ap.grossAmount = data.totalPf.round(2)
        ap.discountAmount = BigDecimal.ZERO
        ap.netOfDiscount = data.totalPf.round(2)
        ap.vatRate = sup.isVatable ? 12.00 : 0.00
        ap.vatAmount = vatAmount
        ap.netOfVat = data.totalPf.round(2)
        ap.ewtAmount = ewtAmount
        ap.netAmount = (data.totalPf - ewtAmount).round(2)
        ap.appliedAmount = BigDecimal.ZERO
        ap.daAmount = BigDecimal.ZERO
        ap.dmAmount = BigDecimal.ZERO
        ap.status = "DRAFT"
        ap.remarksNotes = data.refNo
        ap.posted = false
        def afterSave = save(ap)

        def apDetails = pf as ArrayList<ApPfCompany>
        apDetails.each {
            it ->
                def detials = objectMapper.convertValue(it, ApPfNonCompany.class)
                accountsPayableDetialServices.upsertPfDetialsNonCompany(afterSave, detials)
                //update billing items
                billingItemServices.updatePfProcess(detials.id, true)
        }

        return ap
    }

    //upsert from RF
    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "upsertFromRf")
    AccountsPayable upsertFromRf(
            @GraphQLArgument(name = "parent") Map<String, Object> parent,
            @GraphQLArgument(name = "pf") ArrayList<Map<String, Object>> pf
    ) {
        def ap = new AccountsPayable()
        def data = objectMapper.convertValue(parent, PayablePFDto.class)

        def sup = supplierRepository.findById(data.supplier).get()

        def vatRate = sup.isVatable ? 0.12 : 0.00
        def vatAmount = calculateVat(sup.isVatInclusive, data.totalPf, vatRate)
        def ewtAmount = calculateEwt(sup.isVatInclusive, data.totalPf, vatRate, sup.ewtRate)

        ap.supplier = sup
        ap.apNo = generatorService.getNextValue(GeneratorType.APNO, {
            return "RF-" + StringUtils.leftPad(it.toString(), 6, "0")
        })
        ap.apCategory = "READER'S FEE"
        ap.paymentTerms = null
        ap.apvDate = Instant.now().plus(Duration.ofHours(8))
        ap.dueDate = Instant.now().plus(Duration.ofHours(8))
        ap.invoiceNo = data.reference
        ap.grossAmount = data.totalPf.round(2)
        ap.discountAmount = BigDecimal.ZERO
        ap.netOfDiscount = data.totalPf.round(2)
        ap.vatRate = sup.isVatable ? 12.00 : 0.00
        ap.vatAmount = vatAmount
        ap.netOfVat = data.totalPf.round(2)
        ap.ewtAmount = ewtAmount
        ap.netAmount = (data.totalPf - ewtAmount).round(2)
        ap.appliedAmount = BigDecimal.ZERO
        ap.daAmount = BigDecimal.ZERO
        ap.dmAmount = BigDecimal.ZERO
        ap.status = "DRAFT"
        ap.remarksNotes = data.refNo
        ap.posted = false
        def afterSave = save(ap)

        def apDetails = pf as ArrayList<ApReadersFee>
        apDetails.each {
            it ->
                def detials = objectMapper.convertValue(it, ApReadersFee.class)
                accountsPayableDetialServices.upsertPfDetialsRf(afterSave, detials)
                //update billing items
                billingItemServices.updatePfProcess(detials.id, true)
        }

        return ap
    }


    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "updatePFNonCompany")
    GraphQLRetVal<Boolean> updatePFNonCompany(
            @GraphQLArgument(name = "pf") ArrayList<Map<String, Object>> pf,
            @GraphQLArgument(name = "status") Boolean status
    ) {
        def apDetails = pf as ArrayList<ApPfCompany>
        apDetails.each {
            it ->
                //update billing items
                def detials = objectMapper.convertValue(it, ApPfNonCompany.class)
                billingItemServices.updatePfProcess(detials.id, status)
        }

        new GraphQLRetVal<Boolean>(true, true, "OK")
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "updateReaders")
    GraphQLRetVal<Boolean> updateReaders(
            @GraphQLArgument(name = "pf") ArrayList<Map<String, Object>> pf,
            @GraphQLArgument(name = "status") Boolean status
    ) {
        def apDetails = pf as ArrayList<ApReadersFee>
        apDetails.each {
            it ->
                def detials = objectMapper.convertValue(it, ApReadersFee.class)
                //update billing items
                billingItemServices.updatePfProcess(detials.id, status)
        }

        new GraphQLRetVal<Boolean>(true, true, "OK")
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "updatePfCompany")
    GraphQLRetVal<Boolean> updatePfCompany(
            @GraphQLArgument(name = "pf") ArrayList<Map<String, Object>> pf,
            @GraphQLArgument(name = "status") Boolean status
    ) {

        def apDetails = pf as ArrayList<ApPfCompany>
        apDetails.each {
            it ->
                def detials = objectMapper.convertValue(it, ApPfCompany.class)
                //update ar items
                accountReceivableItemsServices.updateApProcess(detials.id, status)
        }

        new GraphQLRetVal<Boolean>(true, true, "OK")
    }


    //calculate
    static BigDecimal calculateVat(Boolean vatInclusive,
                                   BigDecimal amount,
                                   BigDecimal vatRate) {

        def vat = (amount) / (vatRate + 1)
        def vatAmount = vatInclusive ?
                vat.round(2) * vatRate :
                (amount) * vatRate

        return vatAmount.round(2)
    }

    static calculateEwt(Boolean vatInclusive, BigDecimal amount, BigDecimal vatRate, BigDecimal ewtRate) {
        def netOfdiscount = amount;
        def vat = netOfdiscount / (vatRate + 1)
        def ewt = 0;
        if (vatRate <= 0) {
            ewt = netOfdiscount * ewtRate;
        } else {
            ewt = vatInclusive ?
                    vat.round(2) * ewtRate :
                    netOfdiscount * ewtRate;
        }

        return ewt.round(2);
    }

    //ledger view general
    @GraphQLQuery(name = "ledgerView")
    List<JournalEntryViewDto> ledgerView(
            @GraphQLArgument(name = "id") UUID id
    ) {
        def result = new ArrayList<JournalEntryViewDto>()
        def header = ledgerServices.findOne(id)
        if (header) {
            Set<Ledger> ledger = new HashSet<Ledger>(header.ledger);
            ledger.each {
                def list = new JournalEntryViewDto(
                        code: it.journalAccount.code,
                        desc: it.journalAccount.description,
                        debit: it.debit,
                        credit: it.credit
                )
                result.add(list)
            }
        }
        return result.sort { it.credit }
    }

    //==== reports query ====//
    @GraphQLQuery(name = "apLedger")
    List<ApLedgerDto> apLedger(
            @GraphQLArgument(name = "supplier") UUID supplier,
            @GraphQLArgument(name = "start") String start,
            @GraphQLArgument(name = "end") String end,
            @GraphQLArgument(name = "filter") String filter
    ) {

        String sql = """select * from accounting.ap_ledger(?) 
where date(ledger_date) between ?::date and ?::date and lower(ref_no) like lower(concat('%',?,'%'))"""
        List<ApLedgerDto> items = jdbcTemplate.query(sql,
                new BeanPropertyRowMapper(ApLedgerDto.class),
                supplier,
                start,
                end,
                filter
        )
        return items
    }

    @GraphQLQuery(name = "apAgingDetailed")
    List<ApAgingDetailedDto> apAgingDetailed(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "supplier") UUID supplier,
            @GraphQLArgument(name = "supplierTypes") UUID supplierTypes,
            @GraphQLArgument(name = "posted") Boolean posted

    ) {

        String sql = """select * from accounting.aging_report(?::date) where supplier like '%%' """

        if (posted != null) {
            sql += """ and (posted = ${posted} or posted is null) """
        }

        if (supplierTypes) {
            sql += """ and supplier_type_id = '${supplierTypes}' """
        }

        if (supplier) {
            sql += """ and supplier_id = '${supplier}' """
        }

        sql += """ order by supplier;"""

        List<ApAgingDetailedDto> items = jdbcTemplate.query(sql,
                new BeanPropertyRowMapper(ApAgingDetailedDto.class),
                filter
        )
        return items
    }

    @GraphQLQuery(name = "apAgingSummary")
    List<ApAgingSummaryDto> apAgingSummary(
            @GraphQLArgument(name = "filter") String filter,
            @GraphQLArgument(name = "supplierTypes") UUID supplierTypes,
            @GraphQLArgument(name = "posted") Boolean posted
    ) {

        String sql = """select supplier_id as id,supplier,supplier_type_id,supplier_type,sum(current_amount) as current_amount,
sum(day_1_to_31) as day_1_to_31,sum(day_31_to_60) as day_31_to_60,sum(day_61_to_90) as day_61_to_90,sum(day_91_to_120) as day_91_to_120,
sum(older) as older,sum(total) as total from accounting.aging_report(?::date) where supplier like '%%' """

        if (posted != null) {
            sql += """ and (posted = ${posted} or posted is null) """
        }

        if (supplierTypes) {
            sql += """ and supplier_type_id = '${supplierTypes}' """
        }

        sql += """ group by supplier_id,supplier,supplier_type_id,supplier_type order by supplier;"""

        List<ApAgingSummaryDto> items = jdbcTemplate.query(sql,
                new BeanPropertyRowMapper(ApAgingSummaryDto.class),
                filter
        )
        return items
    }

}
