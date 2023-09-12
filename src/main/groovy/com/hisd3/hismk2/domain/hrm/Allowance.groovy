package com.hisd3.hismk2.domain.hrm

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NaturalId
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.persistence.Transient

@Entity
@TypeChecked
@Table(schema = "hrm", name = "allowance" )
@SQLDelete(sql = "UPDATE hrm.allowance SET deleted = true where id = ? ")
@Where(clause = "deleted <> true or deleted is null")
class Allowance extends AbstractAuditingEntity {

    Allowance() {
    }

    Allowance(UUID id) {
        this.id = id
    }

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id


    @GraphQLQuery
    @Column(name = "name", columnDefinition = "varchar")
    String name

    @GraphQLQuery
    @Column(name = "pay_frequency", columnDefinition = "varchar")
    String payFrequency

    @GraphQLQuery
    @Column(name = "taxable", columnDefinition = "boolean")
    Boolean taxable

    @GraphQLQuery
    @Column(name = "notes", columnDefinition = "varchar")
    String notes

    @GraphQLQuery
    @Column(name = "payroll_type", columnDefinition = "varchar")
    String payrollType

    @GraphQLQuery
    @Column(name = "amount", columnDefinition = "numeric")
    Double amount

    @GraphQLQuery
    @OneToMany(
    mappedBy = "allowance",
    cascade = CascadeType.ALL,
    orphanRemoval = true
    )
    Set<AllowanceTemplateItem> templateItems = new HashSet<AllowanceTemplateItem>()

}
