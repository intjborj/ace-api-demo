package com.hisd3.hismk2.domain.dietary

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Case
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.ResultCheckStyle
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.annotation.ShallowReference

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table


@Entity
@Table(name = "diet_notes", schema = "dietary")
@SQLDelete(sql = "UPDATE dietary.diet_notes SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted <> true or deleted is  null ")
class DietNotes extends AbstractAuditingEntity implements Serializable {


    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @DiffIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`case`", referencedColumnName = "id")
    Case caseId

    @ShallowReference
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee", referencedColumnName = "id")
    Employee employee

    @Column(name = "date_time", columnDefinition = "date")
    Date dateTime

    @Column(name = "pes_statement", columnDefinition = "varchar")
    String pesStatement

    @Column(name = "dietitian_note", columnDefinition = "varchar")
    String dietitianNote

}
