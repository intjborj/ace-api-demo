package com.hisd3.hismk2.domain.dietary

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Case
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.Transient
import java.time.Instant


enum MealSeched {
    BREAKFAST,
    LUNCH,
    DINNER
}

@Entity
@Table(name = "patient_diet_list", schema = "dietary")
class DietList extends AbstractAuditingEntity {
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @Column(name = "meal_sched", columnDefinition = "varchar")
    String mealSched

    @GraphQLQuery
    @Column(name = "meal_date", columnDefinition = "timestamp")
    Instant mealDate

    @Column(name = "diet_type", columnDefinition = "varchar")
    String dietType

    @Column(name = "status", columnDefinition = "varchar")
    String status

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_case", referencedColumnName = "id")
    Case patientCase

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    Employee employee

    @Column(name = "alias", columnDefinition = "varchar")
    String alias

    @Column(name = "meal_to_companion", columnDefinition = "bool")
    Boolean mealToCompanion

    @GraphQLQuery
    @Column(name = "served", columnDefinition = "timestamp")
    Instant served

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "served_by", referencedColumnName = "id")
    Employee servedBy

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_from", referencedColumnName = "id")
    Department requestedFrom

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", referencedColumnName = "id")
    Employee requestedBy

    @Transient
    Instant getCreated() {
        return createdDate
    }

    @Transient
    String getFullName() {
        String fullName
        if(alias) {
            fullName = alias
        }else if(employee){
            fullName = employee.fullName
        }else {
            fullName = patientCase.patient.fullName
        }
        return fullName
    }

}
