package com.hisd3.hismk2.domain.billing

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.enums.InvestorPaymentMode
import com.hisd3.hismk2.domain.cashiering.Shifting
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*
import java.time.Instant

@Entity
@Table(name = "payment_ledger", schema = "billing")
@SQLDelete(sql = "UPDATE billing.payment_ledger SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class PaymentLedgerDetails extends AbstractAuditingEntity implements Serializable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "investor", referencedColumnName = "id")
    Investor investor

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shift", referencedColumnName = "id")
    Shifting shift

    @GraphQLQuery
    @Column(name = "date_received", columnDefinition = "timestamp")
    Instant dateReceived

    @GraphQLQuery
    @Enumerated(value = EnumType.STRING)
    @Column(name = "description", columnDefinition = "varchar")
    InvestorPaymentMode paymentMode

    @GraphQLQuery
    @Column(name = "check_date", columnDefinition = "timestamp")
    Instant checkDate

    @GraphQLQuery
    @Column(name = "particular", columnDefinition = "varchar")
    String particular


    @GraphQLQuery
    @Column(name = "amount", columnDefinition = "numeric(15,2)")
    BigDecimal amount

    @GraphQLQuery
    @Column(name = "date_deposited", columnDefinition = "timestamp")
    Instant dateDeposited

    @GraphQLQuery
    @Column(name = "date_cleared", columnDefinition = "timestamp")
    Instant dateCleared

    @GraphQLQuery
    @Column(name = "subscription_number", columnDefinition = "varchar")
    String subscriptionNumber

    @GraphQLQuery
    @Column(name = "reference_number", columnDefinition = "varchar")
    String referenceNumber

    @GraphQLQuery
    @Column(name = "acquiring_bank", columnDefinition = "varchar")
    String acquiringBank

    @GraphQLQuery
    @Column(name = "depository_bank", columnDefinition = "varchar")
    String depositoryBank

    @GraphQLQuery
    @Column(name = "remarks", columnDefinition = "varchar")
    String remarks

}
