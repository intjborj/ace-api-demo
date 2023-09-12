package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "accounting", name = "ar_payment_tracker_trans")
class ArPaymentTrackerTransaction extends  AbstractAuditingEntity implements Serializable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid" , strategy = "uuid2")
    @Column(name="id" , columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ar_payment_tracker_id" , referencedColumnName = "id")
    ArPaymentTracker arPaymentTracker

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_receivable_id" , referencedColumnName = "id")
    AccountReceivable accountReceivable

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ar_transaction_id" , referencedColumnName = "id")
    ArTransaction arTransaction

    @GraphQLQuery
    @Column(name = "amount")
    BigDecimal amount

    @GraphQLQuery
    @Column(name = "status")
    String status

}
