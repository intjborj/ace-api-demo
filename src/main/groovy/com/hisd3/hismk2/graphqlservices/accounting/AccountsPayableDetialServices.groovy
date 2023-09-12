package com.hisd3.hismk2.graphqlservices.accounting

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.AccountsPayable
import com.hisd3.hismk2.domain.accounting.AccountsPayableDetails
import com.hisd3.hismk2.domain.accounting.ApPfCompany
import com.hisd3.hismk2.domain.accounting.ApPfNonCompany
import com.hisd3.hismk2.domain.accounting.ApReadersFee
import com.hisd3.hismk2.domain.accounting.ApTransaction
import com.hisd3.hismk2.domain.billing.Subscription
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
import com.hisd3.hismk2.domain.inventory.ReceivingReport
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.rest.dto.AccountPayableDetialsDto
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
import org.springframework.transaction.annotation.Transactional

@Service
@GraphQLApi
class AccountsPayableDetialServices extends AbstractDaoService<AccountsPayableDetails> {

    @Autowired
    GeneratorService generatorService

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    ApTransactionServices apTransactionServices

    @Autowired
    DepartmentRepository departmentRepository

    @Autowired
    BillingItemServices billingItemServices

    @Autowired
    AccountReceivableItemsServices accountReceivableItemsServices


    AccountsPayableDetialServices() {
        super(AccountsPayableDetails.class)
    }

    @GraphQLQuery(name = "apDetailsById")
    AccountsPayableDetails apDetailsById(
            @GraphQLArgument(name = "id") UUID id
    ) {
        findOne(id)
    }

    @GraphQLQuery(name = "detailsByAp", description = "Find Ap posted")
    List<AccountsPayableDetails> detailsByAp(@GraphQLArgument(name = "id") UUID id) {
        createQuery("Select ap from AccountsPayableDetails ap where ap.accountsPayable.id = :id", ["id": id]).resultList
    }

    //mutations
    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "upsertPayablesDetails")
    AccountsPayableDetails upsertPayablesDetails(
            @GraphQLArgument(name = "it") AccountPayableDetialsDto it,
            @GraphQLArgument(name = "ap") AccountsPayable ap,
            @GraphQLArgument(name = "trans") UUID trans,
            @GraphQLArgument(name = "dep") UUID dep
    ) {
        AccountsPayableDetails upsert = new AccountsPayableDetails()
        if (!it.isNew) {
            upsert = findOne(UUID.fromString(it.id))
        }
        upsert.accountsPayable = ap
        if (trans) {
            upsert.transType = apTransactionServices.apTransactionById(trans)
        }
        if (dep) {
            upsert.department = departmentRepository.findById(dep).get()
        }
        upsert.amount = it.amount
        upsert.discRate = it.discRate
        upsert.discAmount = it.discAmount
        upsert.vatInclusive = it.vatInclusive
        upsert.vatAmount = it.vatAmount
        upsert.taxDesc = it.taxDesc
        upsert.ewtRate = it.ewtRate
        upsert.ewtAmount = it.ewtAmount
        upsert.netAmount = it.netAmount
        upsert.remarksNotes = it.remarksNotes
        upsert.refNo = it.refNo
        //upsert.source = "ap"
        save(upsert)
    }

    //mutations
    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "upsertPayablesDetailsByRec")
    AccountsPayableDetails upsertPayablesDetailsByRec(
            @GraphQLArgument(name = "it") ReceivingReport it,
            @GraphQLArgument(name = "ap") AccountsPayable ap
    ) {
        AccountsPayableDetails upsert = new AccountsPayableDetails()
        //disc rate
        def s_price = it.amount - it.totalDiscount;
        def discountRate = ((it.amount - s_price) / it.amount) * 100;
        upsert.accountsPayable = ap
        upsert.department = it.receiveDepartment
        upsert.amount = it.grossAmount
        upsert.discRate = discountRate
        upsert.discAmount = it.totalDiscount
        upsert.vatInclusive = it.vatInclusive
        upsert.vatAmount = it.inputTax
        upsert.ewtRate = 0
        upsert.ewtAmount = 0
        upsert.netAmount = it.vatInclusive ? it.amount : it.netDiscount
        upsert.refId = it.id
        upsert.refNo = it.receivedRefNo
        upsert.source = "rec"
        save(upsert)
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "upsertPayablesDetailsByInvestor")
    AccountsPayableDetails upsertPayablesDetailsByInvestor(
            @GraphQLArgument(name = "unallocated") BigDecimal unallocated,
            @GraphQLArgument(name = "refId") UUID refId,
            @GraphQLArgument(name = "refNo") String refNo,
            @GraphQLArgument(name = "ap") AccountsPayable ap
    ) {
        AccountsPayableDetails upsert = new AccountsPayableDetails()

        upsert.accountsPayable = ap
        upsert.department = null
        upsert.amount = unallocated
        upsert.discRate = BigDecimal.ZERO
        upsert.discAmount = BigDecimal.ZERO
        upsert.vatInclusive = true
        upsert.vatAmount = BigDecimal.ZERO
        upsert.ewtRate = 0
        upsert.ewtAmount = 0
        upsert.netAmount = unallocated
        upsert.refId = refId
        upsert.refNo = refNo
//        upsert.refId = payment.id
//        upsert.refNo = "${payment.receiptType} # "+ payment.ornumber
        upsert.source = "investor"
        save(upsert)
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "removeApDetails")
    AccountsPayableDetails removeApDetails(
            @GraphQLArgument(name = "id") UUID id
    ) {
        def details = findOne(id)
        //update billing
        if (details.refId) {
            if (details.source.equalsIgnoreCase("pfNonCompany") || details.source.equalsIgnoreCase("rf")) {
                billingItemServices.updatePfProcess(details.refId, false)
            } else if (details.source.equalsIgnoreCase("pfCompany")) {
                accountReceivableItemsServices.updateApProcess(details.refId, false)
            }

        }
        delete(details)

        return details
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "upsertPfDetials")
    AccountsPayableDetails upsertPfDetials(
            @GraphQLArgument(name = "ap") AccountsPayable ap,
            @GraphQLArgument(name = "pf") ApPfCompany pf
    ) {
        AccountsPayableDetails upsert = new AccountsPayableDetails()

        def sup = ap.supplier
        def vatRate = sup.isVatable ? 0.12 : 0.00

        def pfFee = pf.pfPayable.round(4)

        def vatAmount = calculateVat(sup.isVatInclusive, pfFee, vatRate)
        def ewtAmount = calculateEwt(sup.isVatInclusive, pfFee, vatRate, sup.ewtRate)

        upsert.accountsPayable = ap
        upsert.department = null
        upsert.amount = pfFee
        upsert.discRate = 0
        upsert.discAmount = 0
        upsert.vatInclusive = sup.isVatInclusive
        upsert.vatAmount = vatAmount
        upsert.taxDesc = "${sup.ewtRate * 100 as Integer}%"
        upsert.ewtRate = sup.ewtRate * 100
        upsert.ewtAmount = ewtAmount
        upsert.netAmount = pfFee - ewtAmount
        upsert.remarksNotes = "${pf.patient}"
        upsert.refId = pf.id
        upsert.refNo = pf.billingItem.billing?.finalizedSoa ? pf.billingItem.billing?.finalSoa : pf.billingItem.billing?.billingNo
        upsert.source = "pfCompany"
        save(upsert)
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "upsertPfDetialsNonCompany")
    AccountsPayableDetails upsertPfDetialsNonCompany(
            @GraphQLArgument(name = "ap") AccountsPayable ap,
            @GraphQLArgument(name = "pf") ApPfNonCompany pf
    ) {
        AccountsPayableDetails upsert = new AccountsPayableDetails()

        def sup = ap.supplier
        def vatRate = sup.isVatable ? 0.12 : 0.00

        def pfFee = pf.pfFee.round(4)

        def vatAmount = calculateVat(sup.isVatInclusive, pfFee, vatRate)
        def ewtAmount = calculateEwt(sup.isVatInclusive, pfFee, vatRate, sup.ewtRate)

        upsert.accountsPayable = ap
        upsert.department = pf.department
        upsert.amount = pfFee
        upsert.discRate = 0
        upsert.discAmount = 0
        upsert.vatInclusive = sup.isVatInclusive
        upsert.vatAmount = vatAmount
        upsert.taxDesc = "${sup.ewtRate * 100 as Integer}%"
        upsert.ewtRate = sup.ewtRate * 100
        upsert.ewtAmount = ewtAmount
        upsert.netAmount = pfFee - ewtAmount
        upsert.remarksNotes = "[${pf?.billing?.billingNo}] ${pf?.billing?.patient?.fullName} (PF)"
        upsert.refId = pf.id
        upsert.refNo = pf.billing.finalizedSoa ? pf.billing?.finalSoa : pf.billing?.billingNo
        upsert.source = "pfNonCompany"
        save(upsert)
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "upsertPfDetialsRf")
    AccountsPayableDetails upsertPfDetialsRf(
            @GraphQLArgument(name = "ap") AccountsPayable ap,
            @GraphQLArgument(name = "pf") ApReadersFee pf
    ) {
        AccountsPayableDetails upsert = new AccountsPayableDetails()

        def sup = ap.supplier
        def vatRate = sup.isVatable ? 0.12 : 0.00
        def rfFee = pf.rfFee.round(4)

        def vatAmount = calculateVat(sup.isVatInclusive, rfFee, vatRate)
        def ewtAmount = calculateEwt(sup.isVatInclusive, rfFee, vatRate, sup.ewtRate)

        upsert.accountsPayable = ap
        upsert.department = pf.department
        upsert.amount = rfFee
        upsert.discRate = 0
        upsert.discAmount = 0
        upsert.vatInclusive = sup.isVatInclusive
        upsert.vatAmount = vatAmount
        upsert.taxDesc = "${sup.ewtRate * 100 as Integer}%"
        upsert.ewtRate = sup.ewtRate * 100
        upsert.ewtAmount = ewtAmount
        upsert.netAmount = rfFee - ewtAmount
        upsert.remarksNotes = "[${pf?.billing?.billingNo}] ${pf?.billing?.patient?.fullName} (RF)"
        upsert.refId = pf.id
        upsert.refNo = pf.billing.finalizedSoa ? pf.billing?.finalSoa : pf.billing?.billingNo
        upsert.source = "rf"
        save(upsert)
    }

    //calculate
    static BigDecimal calculateVat(Boolean vatInclusive,
                                   BigDecimal amount,
                                   BigDecimal vatRate) {

        def vat = (amount) / (vatRate + 1)

        def vatAmount = vatInclusive ?
                vat * vatRate :
                (amount) * vatRate

        return vatAmount.round(2)
    }

    static calculateEwt(Boolean vatInclusive, BigDecimal amount, BigDecimal vatRate, BigDecimal ewtRate) {
        def netOfdiscount = amount;
        def ewt = 0;
        def vat = netOfdiscount / (vatRate + 1)
        if (vatRate <= 0) {
            ewt = netOfdiscount * ewtRate;
        } else {
            ewt = vatInclusive ?
                    vat * ewtRate :
                    netOfdiscount * ewtRate;
        }

        return ewt.round(2)
    }
}
