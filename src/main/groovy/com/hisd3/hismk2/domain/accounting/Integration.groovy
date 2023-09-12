package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table



@Entity
@Table(name = "integration", schema = "accounting")
class Integration  extends AbstractAuditingEntity implements Serializable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id


    @GraphQLQuery
    @Column(name = "description", columnDefinition = "varchar")
    @UpperCase
    String description

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "integration_group", referencedColumnName = "id")
    IntegrationGroup integrationGroup

    // default TagValue property in AutoIntegrateable
   /* @GraphQLQuery
    @Column(name = "flag_property", columnDefinition = "varchar")
    String flagProperty
   */

    @GraphQLQuery
    @Column(name = "flag_value", columnDefinition = "varchar")
    String flagValue

    @GraphQLQuery
    @Column(name = "domain", columnDefinition = "varchar")
    String domain



    @GraphQLQuery
    @Column(name = "order_priority", columnDefinition = "int")
    Integer orderPriority


    @GraphQLQuery
    @javax.persistence.OrderBy("createdDate")
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "integration", cascade = [CascadeType.ALL], orphanRemoval = true)
    List<IntegrationItem>  integrationItems  = []

}
