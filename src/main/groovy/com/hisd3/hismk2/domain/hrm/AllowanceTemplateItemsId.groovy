package com.hisd3.hismk2.domain.hrm

import com.hisd3.hismk2.domain.AbstractAuditingEntity

import javax.persistence.Column
import javax.persistence.Embeddable
@Embeddable
class AllowanceTemplateItemsId
        implements Serializable {

    @Column(name = "template")
     UUID templateId

    @Column(name = "allowance")
     UUID allowanceId

    AllowanceTemplateItemsId() {

    }

    AllowanceTemplateItemsId(
            UUID templateId,
            UUID allowanceId) {
        this.templateId = templateId
        this.allowanceId = allowanceId
    }

    @Override
    boolean equals(Object o) {
        if (this.is(o)) return true

        if (o == null || getClass() != o.getClass())
            return false

        AllowanceTemplateItemsId that = (AllowanceTemplateItemsId) o
        return Objects.equals(templateId, that.templateId) &&
                Objects.equals(allowanceId, that.allowanceId)
    }

    @Override
    int hashCode() {
        return Objects.hash(templateId, allowanceId)
    }
}