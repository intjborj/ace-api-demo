package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.Transient

enum CompoundType {
    annually,
    monthly
}

enum LOAN_INTEGRATION {
    LOAN_ENTRY
}

@Entity
@Table(name = "loans", schema = "accounting")
class Loan extends  AbstractAuditingEntity implements Serializable, AutoIntegrateable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "loan_no")
    String loanNo

    @GraphQLQuery
    @Column(name="reference_no")
    String referenceNo

    @GraphQLQuery
    @Column(name="start_date")
    Date startDate

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name="account_no",referencedColumnName = "id")
    LoanAccounts bankAccount

    @GraphQLQuery
    @Column(name="compound_type")
    String compoundType

    @GraphQLQuery
    @Column(name="interest_rate")
    BigDecimal interestRate

    @GraphQLQuery
    @Column(name="loan_period")
    Integer loanPeriod

    @GraphQLQuery
    @Column(name="loan_amount")
    BigDecimal loanAmount

    @GraphQLQuery
    @Column(name="number_of_payments")
    Integer numberOfPayments

    @GraphQLQuery
    @Column(name="total_interest")
    BigDecimal totalInterest

    @GraphQLQuery
    @Column(name="loan_payment")
    BigDecimal loanPayment

    @GraphQLQuery
    @Column(name="total_cost_of_loan")
    BigDecimal totalCostOfLoan

    @GraphQLQuery
    @Column(name="posted_ledger")
    UUID postedLedger

    @GraphQLQuery
    @Formula("(Select coalesce(sum(la.payment),0) from accounting.loan_amortization la where la.posted_ledger is not null and la.loan=id)")
    BigDecimal paidPayments

    @Transient
    BigDecimal remainingBalance
    BigDecimal getRemainingBalance() {
        def b = totalCostOfLoan-paidPayments
        return b
    }

    @Override
    String getDomain() {
        return Loan.class.name
    }

    @Override
    Map<String, String> getDetails() {
        return [:]
    }

    @Transient
    String flagValue


    @Transient
    BigDecimal negativeLoanAmount
    BigDecimal getNegativeLoanAmount() {
        def b = -loanAmount
        return b
    }

    @Transient
    BigDecimal negativeLoanPayment
    BigDecimal getNegativeLoanPayment() {
        def b = -loanPayment
        return b
    }

    @Transient
    BigDecimal negativeInterestRate
    BigDecimal getNegativeInterestRate() {
        def b = -interestRate
        return b
    }

    @Transient
    Bank bank

}
