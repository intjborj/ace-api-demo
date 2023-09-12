package com.hisd3.hismk2.domain.fixed_assets

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.inventory.Item
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*
import org.javers.core.metamodel.annotation.ShallowReference


import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

import java.time.Instant

@Entity
@Table(schema = "fixed_assets", name = "fixed_assets_transactions")
@SQLDelete(sql = "UPDATE fixed_assets.fixed_asset_depreciation SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
class FixedAssetTransactions extends AbstractAuditingEntity implements Serializable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @ShallowReference
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixed_assets_item", referencedColumnName = "id")
    FixedAssetItem fixedAssetItem

    @ShallowReference
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    Item item

    @GraphQLQuery
    @Column(name = "amount", columnDefinition = "numeric")
    BigDecimal amount

    @GraphQLQuery
    @Column(name = "transaction_type", columnDefinition = "varchar")
    String transactionType

    @GraphQLQuery
    @Column(name = "transaction_date")
    Instant transactionDate
}
