package com.hisd3.hismk2.domain.fixed_assets

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.ResultCheckStyle
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
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
@Table(schema = "fixed_assets", name = "fixed_asset_depreciation")
@SQLDelete(sql = "UPDATE fixed_assets.fixed_asset_depreciation SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
class FixedAssetDepreciation extends AbstractAuditingEntity implements Serializable {

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
    @JoinColumn(name = "fixed_asset_item", referencedColumnName = "id")
    FixedAssetItem fixedAssetItem

    @GraphQLQuery
    @Column(name = "cost", columnDefinition = "numeric")
    Float cost

    @GraphQLQuery
    @Column(name = "est_useful_life", columnDefinition = "varchar")
    String estUsefulLife

    @GraphQLQuery
    @Column(name = "est_salvage_value", columnDefinition = "numeric")
    Float estSalvageValue

    @GraphQLQuery
    @Column(name = "unit_of_time", columnDefinition = "varchar")
    String unitOfTime

    @GraphQLQuery
    @Column(name = "depreciation_date_start", columnDefinition = "timestamp")
    Instant depreciationDateStart

}
