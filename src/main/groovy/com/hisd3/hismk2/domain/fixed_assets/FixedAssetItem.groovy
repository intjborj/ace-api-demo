package com.hisd3.hismk2.domain.fixed_assets

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.inventory.AccountingCategory
import com.hisd3.hismk2.domain.inventory.Item
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.ResultCheckStyle
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where
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
@Table(schema = "fixed_assets", name = "fixed_asset_items")
@SQLDelete(sql = "UPDATE fixed_assets.fixed_asset_items SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted <> true or deleted is  null ")
class FixedAssetItem extends AbstractAuditingEntity implements Serializable {

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
    @JoinColumn(name = "fixed_asset", referencedColumnName = "id")
    FixedAssets fixedAssets

    @ShallowReference
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "items", referencedColumnName = "id")
    Item item

    @GraphQLQuery
    @Column(name = "serial_no", columnDefinition = "varchar")
    String serialNo

    @GraphQLQuery
    @Column(name = "status", columnDefinition = "varchar")
    String status

    @GraphQLQuery
    @Column(name = "depreciable", columnDefinition = "bool")
    Boolean depreciable

    @ShallowReference
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "latest_dept", referencedColumnName = "id")
    Department department

    @GraphQLQuery
    @Column(name = "latest_cost", columnDefinition = "numeric")
    BigDecimal latestCost

    @GraphQLQuery
    @Column(name = "latest_est_useful_life", columnDefinition = "varchar")
    String latestEstUsefulLife

    @GraphQLQuery
    @Column(name = "latest_est_salvage_value", columnDefinition = "numeric")
    BigDecimal latestEstSalvageValue

    @GraphQLQuery
    @Column(name = "notes", columnDefinition = "varchar")
    String notes

    @GraphQLQuery
    @Column(name = "description", columnDefinition = "varchar")
    String description

    @GraphQLQuery
    @Column(name = "category", columnDefinition = "UUID")
    UUID category

    @GraphQLQuery
    @Column(name = "unit_of_time", columnDefinition = "varchar")
    String unitOfTime

    @GraphQLQuery
    @Column(name = "depreciation_date_start", columnDefinition = "timestamp")
    Instant depreciationDateStart

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categories", referencedColumnName = "id")
    AccountingCategory accountingCategory
}
