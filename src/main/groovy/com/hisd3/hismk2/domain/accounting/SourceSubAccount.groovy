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
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OrderBy

enum SourceValueType{

    NORMAL,
    CONTRA,
    BALANCE,
    PERIODIC_BALANCE
}

@javax.persistence.Entity
@javax.persistence.Table(name = "source_subaccount", schema = "accounting")
class SourceSubAccount extends AbstractAuditingEntity implements Serializable{

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id


    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "line_type", referencedColumnName = "id")
    LineType lineType

    @UpperCase
    @GraphQLQuery
    @Column(name = "code", columnDefinition = "varchar")
    String code


    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subaccount", referencedColumnName = "id")
    SubAccountSetup subaccount

    @Enumerated
    @Column(name = "value_type", columnDefinition = "varchar")
    SourceValueType valueType


    @GraphQLQuery
    @OneToMany(mappedBy="sourceSubAccount",cascade = [CascadeType.ALL],fetch = FetchType.LAZY,orphanRemoval = true)
    List<SourceSubAccountExclude> excludes= []


}
