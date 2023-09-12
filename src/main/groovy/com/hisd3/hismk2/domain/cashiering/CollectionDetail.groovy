package com.hisd3.hismk2.domain.cashiering

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.accounting.Bank
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

enum  CollectionDetailType{
    CASH,
    CHECK,
    CASHOVERSHORT,
    CHECKENCASHMENT

}
@Entity
@Table(name = "collection_detail", schema = "cashiering")
class CollectionDetail extends AbstractAuditingEntity implements Serializable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection", referencedColumnName = "id")
    CollectionDeposit collection


    @GraphQLQuery
    @Column(name = "amount", columnDefinition = "numeric")
    BigDecimal amount

    @GraphQLQuery
    @Column(name = "deposit_reference_no", columnDefinition = "varchar")
    String depositReferenceNo


    @Enumerated(EnumType.STRING)
    @GraphQLQuery
    @Column(name = "`type`", columnDefinition = "varchar")
    CollectionDetailType type

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank", referencedColumnName = "id")
    Bank bank


    @GraphQLQuery
    @Column(name = "ptd_ids", columnDefinition = "varchar")
    String ptdIds //comma separated values


    @OneToMany(fetch = FetchType.LAZY,cascade = [CascadeType.MERGE], mappedBy = "collectionDetail")
    List<PaymentTrackerDetails> paymentTrackerDetails = []

    @OneToMany(fetch = FetchType.LAZY,cascade = [CascadeType.MERGE], mappedBy = "collectionDetail")
    List<ChequeEncashment> chequeEncashmentDetails = []

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminal", referencedColumnName = "id")
    CashierTerminal terminal
}
