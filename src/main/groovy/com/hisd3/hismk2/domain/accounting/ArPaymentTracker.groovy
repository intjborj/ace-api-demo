package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
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

@Entity
@Table(schema = "accounting", name = "ar_payment_tracker")
class ArPaymentTracker extends  AbstractAuditingEntity implements Serializable {

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
    @JoinColumn(name = "payment_tracker_id" , referencedColumnName = "id")
    PaymentTracker paymentTracker

    @GraphQLQuery
    @Column(name = "or_number")
    String orNumber

    @GraphQLQuery
    @Column(name = "amount")
    BigDecimal amount

    @GraphQLQuery
    @Column(name = "credit")
    BigDecimal credit

    @GraphQLQuery
    @Column(name = "status")
    String status

    @Transient
    BigDecimal tempCredit
}
