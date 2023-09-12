package com.hisd3.hismk2.graphqlservices.billing

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.billing.Investor
import com.hisd3.hismk2.domain.billing.InvestorPaymentLedger
import com.hisd3.hismk2.domain.billing.Subscription
import com.hisd3.hismk2.domain.billing.enums.InvestorLedgerTransactionType
import com.hisd3.hismk2.graphqlservices.accounting.IntegrationServices
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.billing.InvestorPaymentLedgerRepository
import com.hisd3.hismk2.repository.billing.InvestorsRepository
import com.hisd3.hismk2.repository.billing.SubscriptionRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
@GraphQLApi
class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository

    @Autowired
    InvestorsRepository investorsRepository

    @Autowired
    InvestorPaymentLedgerRepository investorPaymentLedgerRepository

    @Autowired
    InvestorPaymentLedgerService investorPaymentLedgerService

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    IntegrationServices integrationServices

    @Autowired
    LedgerServices ledgerServices

    @Value('${accounting.autopostjournal}')
    Boolean auto_post_journal

    @GraphQLQuery(name = "unpaidSubscriptions")
    List<Subscription> unpaidSubscriptions(
            @GraphQLArgument(name = "id") UUID id
    ) {
        List<Subscription> subscriptions = subscriptionRepository.getSubscriptionByInvestorId(id)
        return subscriptions
    }


    @GraphQLMutation
    @Transactional
    GraphQLRetVal<Subscription> upsertSubscription(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "investor") UUID investor
    ) {
        if (!investor) return new GraphQLRetVal<Subscription>(null, false, "Failed to add subscription.")
        Investor foundInvestor = null
        investorsRepository.findById(investor).ifPresent { foundInvestor = it }

        if (id) {

            return new GraphQLRetVal<Subscription>(null, true, "Successfully updated a subscription")
        } else {
            Subscription subscription = objectMapper.convertValue(fields, Subscription.class)
            subscription.investor = foundInvestor
            subscription = subscriptionRepository.save(subscription)

            BigDecimal investorBalance = investorPaymentLedgerService.getInvestorBalance(foundInvestor)

            //create ledger
            InvestorPaymentLedger ledger = new InvestorPaymentLedger(foundInvestor, subscription, InvestorLedgerTransactionType.INVESTOR_SUBSCRIPTION, subscription.subscriptionPrice * subscription.shares as BigDecimal, 0 as BigDecimal, null, "Investor Subscription")

            ledger.subscriptionReceivable = 0
            ledger.subscribedShareCapital = 0
            ledger.additionalPaidInCapital = 0
            ledger.discountOnShareCapital = 0
            ledger.advancesFromInvestors = 0

            if (investorBalance < 0) ledger.advancesFromInvestors = investorBalance


            BigDecimal subscriptionReceivable = subscription.shares * subscription.subscriptionPrice
            BigDecimal discountOnShareCapital = subscription.shares * (subscription.parValue - subscription.subscriptionPrice)
            BigDecimal subscribedShareCapital = subscription.shares * subscription.parValue
            BigDecimal additionalPaidInCapital = subscription.shares * (subscription.subscriptionPrice - subscription.parValue)
            ledger.subscriptionReceivable = subscriptionReceivable
            ledger.subscribedShareCapital = subscribedShareCapital
            if (subscription.subscriptionPrice == subscription.parValue) {
                // Scenario 1: Subscription Price equal to Par value
                // Subscription Receivable (No. of shares subscribed x subscription price)
                //                 Subscribed Share Capital (No. of shares subscribed x par value per share)
            } else if (subscription.subscriptionPrice > subscription.parValue) {
                // Scenario 2: Subscription Price is greater than the Par value
                // Subscription Receivable (No. of shares subscribed x subscription price)
                //              Subscribed Share Capital (No. of shares subscribed x par value per share)
                //              Additional Paid-in Capital [No. of shares subscribed x (subscription price less par value per share)]
                ledger.additionalPaidInCapital = additionalPaidInCapital
            } else if (subscription.subscriptionPrice < subscription.parValue) {
                // Scenario 3: Subscription Price is less than the Par value
                // Subscription Receivable (No. of shares subscribed x subscription price)
                // Discount on Share Capital [No. of shares subscribed x (par value per share less subscription price)]
                //                 Subscribed Share Capital (No. of shares subscribed x par value per share)
                ledger.discountOnShareCapital = discountOnShareCapital
            }

            BigDecimal balance = ledger.subscriptionReceivable.abs() + ledger.advancesFromInvestors
            ledger.subscriptionReceivable = balance
            ledger.subscribedShareCapital = ledger.subscribedShareCapital.abs()
            ledger.additionalPaidInCapital = ledger.additionalPaidInCapital.abs()
            ledger.discountOnShareCapital = ledger.discountOnShareCapital.abs()

            if (balance <= 0) subscription.fullPaymentDate = Instant.now()
            subscriptionRepository.save(subscription)

            ledger = investorPaymentLedgerRepository.save(ledger)
            if (auto_post_journal) {
                createSubscriptionEntries(subscription, ledger, investorBalance)
            }

            return new GraphQLRetVal<Subscription>(subscription, true, "Successfully added a subscription")
        }

    }

    @Transactional
    void createSubscriptionEntries(Subscription subscription, InvestorPaymentLedger ledger, BigDecimal investorBalance) {
        def yearFormat = DateTimeFormatter.ofPattern("yyyy")

        def headerLedger = integrationServices.generateAutoEntries(subscription) { it, nul ->
            it.flagValue = "INVESTOR_SUBSCRIPTION"

            it.subscriptionReceivable = 0
            it.subscribedShareCapital = 0
            it.additionalPaidInCapital = 0
            it.discountOnShareCapital = 0
            it.advancesFromInvestors = 0

            it.subscriptionReceivable = ledger.subscriptionReceivable.abs()
            it.subscribedShareCapital = ledger.subscribedShareCapital.abs()
            it.additionalPaidInCapital = ledger.additionalPaidInCapital.abs()
            it.discountOnShareCapital = ledger.discountOnShareCapital.abs()
            it.advancesFromInvestors = ledger.advancesFromInvestors
        }


        Map<String, String> details = [:]
        subscription.details.each { k, v ->
            details[k] = v
        }


        details["PAYMENT_LEDGER_ID"] = ledger.id.toString()
        details["SUBSCRIPTION"] = subscription.id.toString()
        details["INVESTOR"] = ledger.investor.id.toString()
        def pHeader = ledgerServices.persistHeaderLedger(headerLedger,
                "${subscription.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${subscription.investor.investorNo}",
                "${subscription.investor.fullName}",
                "INVESTOR SUBSCRIPTION",
                LedgerDocType.JV,
                JournalType.GENERAL,
                subscription.createdDate,
                details)
        subscription.ledgerHeader = pHeader.id

        subscriptionRepository.save(subscription)

    }

}
