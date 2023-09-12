package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.graphqlservices.accounting.CoaComponentContainer
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLQuery
import org.apache.commons.lang3.StringUtils
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapKeyColumn
import javax.persistence.Table

@Canonical
class CoaPattern{

    UUID subAccountSetupId
    String subAccountName

    @GraphQLQuery
    CoaComponentContainer motherAccount = new CoaComponentContainer()

    @GraphQLQuery
    CoaComponentContainer subAccount = new CoaComponentContainer()

    @GraphQLQuery
    CoaComponentContainer subSubAccount = new CoaComponentContainer()

    String code
    String getCode(){

        String concat = ""
        concat = StringUtils.defaultIfEmpty(motherAccount?.code,"0000")
        concat += "-" + StringUtils.defaultIfEmpty(subAccount?.code,"0000")
        concat += "-" + StringUtils.defaultIfEmpty(subSubAccount?.code,"0000")
        return concat
    }

    String description

    String getDescription(){

        String concat = ""
        concat = StringUtils.defaultIfEmpty(motherAccount?.description,"")

        if(subAccount?.description)
            concat += "-" + subAccount?.description?:""

        if(subSubAccount?.description)
            concat += "-" + subSubAccount?.description?:""

        return concat
    }


}

@Entity
@Table(name = "integration_items", schema = "accounting")
class IntegrationItem  extends AbstractAuditingEntity implements Serializable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id


    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "integration", referencedColumnName = "id")
    Integration integration

    @GraphQLQuery
    @Type(type = "jsonb")
    @Column(name="journal_account",columnDefinition = "jsonb")
    CoaPattern journalAccount


    @GraphQLQuery
    @Column(name = "value_property", columnDefinition = "varchar")
    String valueProperty

    @GraphQLQuery
    @Column(name = "disabled_property", columnDefinition = "varchar")
    String disabledProperty


    @GraphQLQuery
    @Column(name = "disabled_value", columnDefinition = "varchar")
    String disabledValue

    @GraphQLQuery
    @Column(name = "source_column", columnDefinition = "varchar")
    String sourceColumn

    @GraphQLQuery
    @Column(name = "multiple", columnDefinition = "boolean")
    Boolean multiple


    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(schema = "accounting", name = "integration_items_details",
            joinColumns = [@JoinColumn(name = "integration_item")])
    @MapKeyColumn(name = "field_name")
    @Column(name = "field_value")
    @BatchSize(size = 20)
    Map<String, String> details = [:]


}
