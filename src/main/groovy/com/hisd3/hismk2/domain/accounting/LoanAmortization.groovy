package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
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

enum LOAN_AMORTIZATION_INTEGRATION  {
    LOANM_PAYMENT,
}

@Entity
@Table(name = "loan_amortization", schema = "accounting")
class LoanAmortization extends  AbstractAuditingEntity implements Serializable, AutoIntegrateable{

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "loan",referencedColumnName = "id")
    Loan loan

    @GraphQLQuery
    @Column(name = "order_no")
    Integer orderNo

    @GraphQLQuery
    @Column(name = "record_no")
    String recordNo

    @GraphQLQuery
    @Column(name = "reference_no")
    String referenceNo

    @GraphQLQuery
    @Column(name = "payment_date")
    Date paymentDate

    @GraphQLQuery
    @Column(name = "beginning_balance")
    BigDecimal beginningBalance

    @GraphQLQuery
    @Column(name = "payment")
    BigDecimal payment

    @GraphQLQuery
    @Column(name = "principal")
    BigDecimal principal

    @GraphQLQuery
    @Column(name = "interest")
    BigDecimal interest

    @GraphQLQuery
    @Column(name = "ending_balance")
    BigDecimal endingBalance

    @GraphQLQuery
    @Column(name="posted_ledger")
    UUID postedLedger

    @Override
    String getDomain() {
        return LoanAmortization.class.name
    }

    @Override
    Map<String, String> getDetails() {
        return [:]
    }


    @Transient
    String flagValue

    @Transient
    BigDecimal negativePayment
    BigDecimal getNegativePayment() {
        def b = -payment
        return b
    }


    @Transient
    BigDecimal negativePrincipal
    BigDecimal getNegativePrincipal() {
        def b = -principal
        return b
    }

    @Transient
    BigDecimal negativeInterest
    BigDecimal getNegativeInterest() {
        def b = -interest
        return b
    }

    @Transient
    Bank bank

}
