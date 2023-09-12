package com.hisd3.hismk2.domain.hrm

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.accounting.AccountReceivable
import com.hisd3.hismk2.domain.annotations.UpperCase
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where

import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.persistence.Table
import javax.persistence.Transient

@Entity
@TypeChecked
@Table(schema = "hrm", name = "allowance_template_items" )
//@SQLDelete(sql = "UPDATE hrm.allowance_template_items SET deleted = true where   allowance = ? and template = ?")
//@Where(clause = "deleted <> true or deleted is null")
class AllowanceTemplateItem extends AbstractAuditingEntity implements Serializable {

    AllowanceTemplateItem() {
    }

    AllowanceTemplateItem(AllowanceTemplate templates, Allowance allowance) {
        this.template = templates
        this.allowance = allowance
        this.id = new AllowanceTemplateItemsId(templates.id, allowance.id)
    }

    @EmbeddedId
    AllowanceTemplateItemsId id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template", referencedColumnName = "id")
    @MapsId("templateId")
    AllowanceTemplate template

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allowance", referencedColumnName = "id")
    @MapsId("allowanceId")
    Allowance allowance

    @GraphQLQuery
    @Column(name = "note", columnDefinition = "varchar")
    String notes

    @GraphQLQuery
    @Column(name = "active", columnDefinition = "boolean")
    Boolean active

    @Transient
    Allowance getAllowance() {
        return allowance
    }




    @Override
    boolean equals(Object o) {
        if (this == o) return true

        if (o == null || getClass() != o.getClass())
            return false

        AllowanceTemplateItem that = (AllowanceTemplateItem) o
        return Objects.equals(template, that.template) &&
                Objects.equals(allowance, that.allowance)
    }

    @Override
    int hashCode() {
        return Objects.hash(template, allowance)
    }
}