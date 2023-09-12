package com.hisd3.hismk2.domain.hrm

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.accounting.ArTransaction
import com.hisd3.hismk2.domain.annotations.UpperCase
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NaturalId
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OrderBy
import javax.persistence.Table
import java.time.Instant

@Entity
@TypeChecked
@Table(schema = "hrm", name = "allowance_templates" )
@SQLDelete(sql = "UPDATE hrm.allowance_templates SET deleted = true where id = ? ")
@Where(clause = "deleted <> true or deleted is null")
class AllowanceTemplate extends AbstractAuditingEntity implements Serializable {

    AllowanceTemplate() {
    }

    AllowanceTemplate(UUID id) {
        this.id = id
    }

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    @NaturalId
    UUID id

    @GraphQLQuery
    @Column(name = "name", columnDefinition = "varchar")
        String name

    @GraphQLQuery
    @Column(name = "active", columnDefinition = "boolean")
    Boolean active


    @GraphQLQuery
    @Formula("(select sum(a.amount) from hrm.allowance_template_items i left join hrm.allowance a on a.id = i.allowance where i.template = id and i.active is true and COALESCE(i.deleted,false) != true)")
    BigDecimal total

    @GraphQLQuery
    @OneToMany(
    mappedBy = "template",
    cascade = CascadeType.ALL,
    orphanRemoval = true
    )
    Set<AllowanceTemplateItem> templates = new HashSet<AllowanceTemplateItem>()

}

