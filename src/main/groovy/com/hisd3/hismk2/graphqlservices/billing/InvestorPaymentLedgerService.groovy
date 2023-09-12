package com.hisd3.hismk2.graphqlservices.billing


import com.hisd3.hismk2.domain.PersistentToken
import com.hisd3.hismk2.domain.billing.Investor
import com.hisd3.hismk2.domain.billing.InvestorPaymentLedger
import com.hisd3.hismk2.domain.billing.Subscription
import com.hisd3.hismk2.domain.billing.enums.InvestorLedgerTransactionType
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.dto.InvestorPaymentLedgerDto
import com.hisd3.hismk2.graphqlservices.billing.dto.InvestorPaymentLedgerRunningBalanceDto
import com.hisd3.hismk2.graphqlservices.cashiering.PaymentTrackerServices
import com.hisd3.hismk2.repository.billing.InvestorPaymentLedgerRepository
import com.hisd3.hismk2.graphqlservices.billing.dto.InvestorPaymentLedgerDto
import com.hisd3.hismk2.repository.billing.SubscriptionRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.hibernate.query.NativeQuery
import org.hibernate.transform.Transformers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional


@Component
@GraphQLApi
class InvestorPaymentLedgerService extends AbstractDaoService<InvestorPaymentLedger> {

    InvestorPaymentLedgerService(){
        super(InvestorPaymentLedger.class)
    }

    @Autowired
    InvestorPaymentLedgerRepository investorPaymentLedgerRepository

    @Autowired
    SubscriptionRepository subscriptionRepository

    @Autowired
    EntityManager entityManager

    @Autowired
    PaymentTrackerServices paymentTrackerServices

    // NOTE: only add this to your graphql query when getting only one investor, 
    // I don't know if this is advisable to include when the return is array of investors
    // it may have slow performance.
    @GraphQLQuery(name = "paymentLedgers", description = "Get all Investor Payment Ledgers")
    List<InvestorPaymentLedgerDto> getInvestorPaymentLedgers(@GraphQLContext Investor investor) {
        def ledgers = entityManager.createNativeQuery("""
                SELECT
                    cast(ipl.id AS varchar) AS "id",
                    cast(i.id AS varchar) AS "investorId",
                    ipl. "type" AS "type",
                    cast(ptd.created_date AS varchar) AS "ptdCreatedDate",
                    ptd. "type" AS "modeOfPayment",
                    ptd.check_date AS "checkDate",
                    ptd.bank AS "acquiringBank", 
                    ipl.particular AS "particular",
                    ipl.debit AS "debit",
                    ipl.credit AS "credit",
                    s.shiftno AS "shiftNumber",
                    cast(s.id AS varchar) AS "shiftId",
                    i.arno AS "subscriptionNumber",
                    ptd.reference AS "referenceNumber",
                    pt.ornumber AS "receiptNumber",
                    pt.receipt_type AS "receiptType",
                    sub.subscription_code AS "subscriptionCode",
                    cast(sub.created_date AS varchar) AS "subscriptionCreatedDate",
                    cast(sub.id AS varchar) AS "subscriptionId",
                    coalesce(ipl.is_voided,false) as "isVoided",
                    cast(ipl.created_date AS varchar) as "createdDate"
                FROM
                    billing.investor_payment_ledger ipl
                    LEFT JOIN billing.investor_subscriptions sub ON sub.id = ipl.subscription  
                    LEFT JOIN cashiering.payment_tracker pt ON ipl.payment_tracker = pt.id
                    LEFT JOIN cashiering.payment_tracker_details ptd ON pt.id = ptd.payment_tracker
                    LEFT JOIN cashiering.shifting s ON pt.shiftid = s.id
                    LEFT JOIN billing.investors i ON i.id = ipl.investor
                WHERE 
                    cast(i.id AS uuid) = CAST(:investor as uuid)
                    and (ipl.deleted != true or ipl.deleted  is null)
                ORDER BY
                    ipl.created_date, ptd.created_date
                """).setParameter("investor", investor.id)


        return ledgers.unwrap(NativeQuery.class).setResultTransformer(Transformers.aliasToBean(InvestorPaymentLedgerDto.class)).getResultList()
    }

    @GraphQLQuery(name = "paymentLedgersRunningBalance", description = "Get all Investor Payment Ledgers with Running Balance")
    List<InvestorPaymentLedgerRunningBalanceDto> getInvestorPaymentLedgersRunningBalance(@GraphQLContext Investor investor) {
        def ledgers = entityManager.createNativeQuery("""
                SELECT
                    CAST(ipl.created_date AS varchar) AS "createdDate",
                    ipl. "type" AS "type",
                    ipl.particular AS "particular",
                    ipl.debit AS "debit",
                    ipl.credit AS "credit",
                    sum(ipl.debit - ipl.credit) OVER (ORDER BY ipl.created_date,ipl.id) AS "balance",
                    ipl.remarks AS "remarks"
                FROM
                    billing.investor_payment_ledger ipl
                WHERE
                    CAST(ipl.investor AS uuid) = CAST(:investor AS uuid)
                    and (ipl.deleted != true or ipl.deleted  is null)
                """).setParameter("investor", investor.id)

        return ledgers.unwrap(NativeQuery.class).setResultTransformer(Transformers.aliasToBean(InvestorPaymentLedgerRunningBalanceDto.class)).getResultList()
    }


    @GraphQLQuery(name = "totalSubscription")
    BigDecimal totalDebitSubscriptions(
            @GraphQLContext Investor investor
    ) {
        BigDecimal totalSubscription = entityManager.createQuery("""
            Select sum(s.total) from Subscription s
            left join s.investor i
            where i.id = :investor
        """, BigDecimal.class).setParameter("investor", investor.id).getSingleResult()

        return totalSubscription ?: 0
    }

    @GraphQLQuery(name = "totalPaidSubscriptions")
    BigDecimal totalPaidSubscription(
            @GraphQLContext Investor investor
    ) {
        BigDecimal totalSubscription = entityManager.createQuery("""
            Select sum(s.total) from Subscription s
            left join s.investor i
            where i.id = :investor
            and s.fullPaymentDate is not null
        """, BigDecimal.class).setParameter("investor", investor.id).getSingleResult()

        return totalSubscription ?: 0
    }


    @GraphQLQuery(name = "totalUnpaidSubscriptions")
    BigDecimal totalUnpaidSubscriptions(
            @GraphQLContext Investor investor
    ) {
        BigDecimal totalSubscription = entityManager.createQuery("""
            Select sum(s.total) from Subscription s
            left join s.investor i
            where s.fullPaymentDate is null AND i.id = :investor
        """, BigDecimal.class).setParameter("investor", investor.id).getSingleResult()

        return totalSubscription ?: 0
    }

    @GraphQLQuery(name = "unpaidSubscriptions")
    List<Subscription> getUnpaidSubscriptions(
            @GraphQLContext Investor investor
    ) {
        List<Subscription> totalSubscription = entityManager.createQuery("""
            Select s from Subscription s
            left join s.investor i
            where s.fullPaymentDate is null AND i.id = :investor
        """, Subscription.class).setParameter("investor", investor.id).resultList

        return totalSubscription
    }

    @GraphQLQuery(name = "totalPaidInSubscription")
    BigDecimal totalPaidInSubscription(
            @GraphQLContext Investor investor
    ) {
        BigDecimal totalSubscription = entityManager.createQuery("""
                SELECT
                    sum(ipl.credit) 
                FROM
                    InvestorPaymentLedger ipl
                LEFT JOIN ipl.investor i
                WHERE 
                    i.id = :investor
        """, BigDecimal.class).setParameter("investor", investor.id).getSingleResult()

        return totalSubscription ?: 0
    }


    @GraphQLQuery(name = "balance", description = "Get all Investor Payment Ledgers")
    BigDecimal getInvestorBalance(@GraphQLContext Investor investor) {
        def balance = entityManager.createQuery("""
                SELECT
                    sum(ipl.total) 
                FROM
                    InvestorPaymentLedger ipl
                LEFT JOIN ipl.investor i
                WHERE 
                    i.id = :investor
                """, BigDecimal.class).setParameter("investor", investor.id).getSingleResult()

        return balance ?: 0
    }

    BigDecimal getSubscriptionBalance(UUID subscriptionId) {
        def balance = entityManager.createQuery("""
                SELECT
                    sum(ipl.total) 
                FROM
                    InvestorPaymentLedger ipl
                LEFT JOIN ipl.subscription s
                WHERE 
                    s.id = :subscription
                """, BigDecimal.class).setParameter("subscription", subscriptionId).getSingleResult()

        return balance ?: 0
    }


    @GraphQLMutation(name="voidInvestorPayment")
//    @Transactional
    Boolean voidInvestorPayment(
            @GraphQLArgument(name="id") UUID id,
            @GraphQLArgument(name="subscriptionId") UUID subscriptionId
    ){
        try{
            if(id) {
                def ledger = findOne(id)
                if (ledger) {
                    paymentTrackerServices.voidPayment(ledger.paymentTracker.id, "Void Investor payment")

                    if(ledger.subscription.fullPaymentDate) {
                        Subscription subscription = ledger.subscription
                        subscription.fullPaymentDate = null
                        subscriptionRepository.save(subscription)
                    }

                    InvestorPaymentLedger voidedEntry = new InvestorPaymentLedger(ledger.investor, ledger.paymentTracker, ledger.subscription, InvestorLedgerTransactionType.INVESTOR_PAYMENT_VOID, ledger.credit, 0 as BigDecimal, "${ledger.remarks}", "Investor Void Payment")
                    voidedEntry.subscriptionReceivable = voidedEntry.credit
                    voidedEntry.subscribedShareCapital = 0
                    voidedEntry.shareCapital = 0

                    voidedEntry.paymentLedgerRefId = ledger
                    def newSave = save(voidedEntry)

                    ledger.isVoided = true
                    ledger.paymentLedgerRefId = newSave
                    save(ledger)
                    return true
                }
            }
            return false
        }catch(e){
            return false
        }

    }

    @GraphQLQuery(name = "findByPaymentTrackerId")
    List<InvestorPaymentLedger> findByPaymentTrackerId(
            @GraphQLArgument(name = 'id') UUID id
    ) {
        createQuery(""" SELECT i FROM InvestorPaymentLedger i where i.paymentTracker.id = :id """,
        [
                id:id
        ]).resultList
    }

}
