package com.hisd3.hismk2.domain.billing

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.Column
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@javax.persistence.Entity
@javax.persistence.Table(name = "soa_groupings", schema = "billing")
class SoaGrouping extends AbstractAuditingEntity implements Serializable {
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_id", referencedColumnName = "id")
    Billing billing

    @GraphQLQuery
    @Column(name = "group_name", columnDefinition = "varchar")
    String groupName


    @javax.persistence.OrderBy("recordNo")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "soaGrouping")
    List<BillingItem> contents = []

}
