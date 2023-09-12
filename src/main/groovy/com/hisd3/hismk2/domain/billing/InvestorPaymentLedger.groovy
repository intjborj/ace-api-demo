package com.hisd3.hismk2.domain.billing

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.accounting.Bank
import com.hisd3.hismk2.domain.billing.enums.InvestorLedgerTransactionType
import com.hisd3.hismk2.domain.cashiering.CashierTerminal
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.Transient

@Entity
@Table(name = "investor_payment_ledger", schema = "billing")
@SQLDelete(sql = "UPDATE billing.investor_payment_ledger SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class InvestorPaymentLedger extends AbstractAuditingEntity implements Serializable, AutoIntegrateable {

    InvestorPaymentLedger(){
    }

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor", referencedColumnName = "id")
    Investor investor

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_tracker", referencedColumnName = "id")
    PaymentTracker paymentTracker

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription", referencedColumnName = "id")
    Subscription subscription

    @GraphQLQuery
    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "varchar")
    InvestorLedgerTransactionType type

    @GraphQLQuery
    @Column(name = "debit", columnDefinition = "numeric(15,2)")
    BigDecimal debit

    @GraphQLQuery
    @Column(name = "credit", columnDefinition = "numeric(15,2)")
    BigDecimal credit

    @GraphQLQuery
    @Formula(value = "debit - credit")
    BigDecimal total

    @GraphQLQuery
    @Column(name = "remarks", columnDefinition = "varchar")
    String remarks

    @GraphQLQuery
    @Column(name = "particular", columnDefinition = "varchar")
    String particular

    @GraphQLQuery
    @Column(name = "ledger_header", columnDefinition = "varchar")
    UUID ledgerHeader

    @GraphQLQuery
    @Column(name = "subscribed_share_capital", columnDefinition = "numeric(15,2)")
    BigDecimal subscribedShareCapital

    @GraphQLQuery
    @Column(name = "subscription_receivable", columnDefinition = "numeric(15,2)")
    BigDecimal subscriptionReceivable

    @GraphQLQuery
    @Column(name = "additional_paid_in_capital", columnDefinition = "numeric(15,2)")
    BigDecimal additionalPaidInCapital

    @GraphQLQuery
    @Column(name = "discount_on_share_capital", columnDefinition = "numeric(15,2)")
    BigDecimal discountOnShareCapital

    @GraphQLQuery
    @Column(name = "advances_from_investors", columnDefinition = "numeric(15,2)")
    BigDecimal advancesFromInvestors

    @GraphQLQuery
    @Column(name = "share_capital", columnDefinition = "numeric(15,2)")
    BigDecimal shareCapital

    @GraphQLQuery
    @Column(name = "is_voided", columnDefinition = "bool")
    Boolean isVoided

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_ledger_ref_id", referencedColumnName = "id")
    InvestorPaymentLedger paymentLedgerRefId

    InvestorPaymentLedger(Investor investor, PaymentTracker paymentTracker, InvestorLedgerTransactionType type, BigDecimal debit, BigDecimal credit, String remarks, String particular) {
        this.investor = investor
        this.paymentTracker = paymentTracker
        this.type = type
        this.debit = debit
        this.credit = credit
        this.remarks = remarks
        this.particular = particular
    }

    InvestorPaymentLedger(Investor investor, Subscription subscription, InvestorLedgerTransactionType type, BigDecimal debit, BigDecimal credit, String remarks, String particular) {
        this.investor = investor
        this.subscription = subscription
        this.type = type
        this.debit = debit
        this.credit = credit
        this.remarks = remarks
        this.particular = particular
    }

    InvestorPaymentLedger(Investor investor, PaymentTracker paymentTracker, Subscription subscription, InvestorLedgerTransactionType type, BigDecimal debit, BigDecimal credit, String remarks, String particular) {
        this.investor = investor
        this.paymentTracker = paymentTracker
        this.subscription = subscription
        this.type = type
        this.debit = debit
        this.credit = credit
        this.remarks = remarks
        this.particular = particular
    }

    @Override
    String getDomain() {
        return InvestorPaymentLedger.class.name
    }

    @Transient
    String flagValue

    @Transient
    BigDecimal subscribedShareCapitalPremium,  shareCapitalCommonShare,  shareCapitalPremium

    @Override
    Map<String, String> getDetails() {
        return [:]
    }

}