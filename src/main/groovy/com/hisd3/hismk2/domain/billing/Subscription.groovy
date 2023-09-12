package com.hisd3.hismk2.domain.billing

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.ancillary.Service
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@Entity
@Table(name = "investor_subscriptions", schema = "billing")
class Subscription extends AbstractAuditingEntity implements Serializable, AutoIntegrateable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor", referencedColumnName = "id")
    Investor investor

    @Column(name = "subscription_code", columnDefinition = "varchar")
    String subscriptionCode

    @Column(name = "shares", columnDefinition = "int")
    BigDecimal shares

    @Column(name = "subscription_price", columnDefinition = "int")
    BigDecimal subscriptionPrice

    @Column(name = "par_value", columnDefinition = "int")
    BigDecimal parValue

    @Column(name = "full_payment_date", columnDefinition = "timestamp")
    Instant fullPaymentDate

    @Column(name = "single_payment", columnDefinition = "timestamp")
    Boolean singlePayment

    @Formula("shares * subscription_price")
    BigDecimal total

    @Formula("""(SELECT 
                     sum(coalesce(ipl.debit,0) + coalesce(ipl.advances_from_investors,0)) - sum(coalesce(ipl.credit,0))
                FROM 
                    billing.investor_payment_ledger ipl
                WHERE ipl.subscription = id
                GROUP BY ipl.subscription)""")
    BigDecimal balance

    @GraphQLQuery
    @Column(name = "ledger_header", columnDefinition = "varchar")
    UUID ledgerHeader

    @Transient
    BigDecimal subscriptionReceivable,
               subscribedShareCapital,
               additionalPaidInCapital,
               discountOnShareCapital,
               advancesFromInvestors

    @Override
    String getDomain() {
        return Subscription.class.name
    }

    @Transient
    String flagValue

    @Override
    Map<String, String> getDetails() {
        return [:]
    }
}
