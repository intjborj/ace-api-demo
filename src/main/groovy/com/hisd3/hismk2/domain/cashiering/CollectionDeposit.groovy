package com.hisd3.hismk2.domain.cashiering

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.persistence.Transient
import java.time.Instant


@Entity
@Table(name = "collection", schema = "cashiering")
class CollectionDeposit extends AbstractAuditingEntity implements Serializable {



    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id



    @GraphQLQuery
    @Column(name = "transaction_date_time", columnDefinition = "timestamp")
    Instant transactionDateTime

    @GraphQLQuery
    @UpperCase
    @Column(name = "remarks", columnDefinition = "varchar")
    String remarks


    @GraphQLQuery
    @UpperCase
    @Column(name = "collection_id", columnDefinition = "varchar")
    String collectionId



    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "collection")
    List<CollectionDetail> collectionDetails = []


    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL],  orphanRemoval = true, mappedBy = "collection")
    List<Cdctr> cdctrList = []


    @GraphQLQuery
    @Column(name = "ledger_header", columnDefinition = "uuid")
    UUID ledgerHeader


    @GraphQLQuery
    @Column(name = "total_hard_cash", columnDefinition = "numeric")
    BigDecimal totalHardCash

    @GraphQLQuery
    @Column(name = "total_check", columnDefinition = "numeric")
    BigDecimal totalCheck

    @GraphQLQuery
    @Column(name = "total_card", columnDefinition = "numeric")
    BigDecimal totalCard

    @GraphQLQuery
    @Column(name = "total_bankdeposit", columnDefinition = "numeric")
    BigDecimal totalBankdeposit

}
