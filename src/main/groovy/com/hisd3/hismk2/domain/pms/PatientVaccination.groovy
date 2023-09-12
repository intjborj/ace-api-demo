package com.hisd3.hismk2.domain.pms

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.ResultCheckStyle
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import java.time.Instant

@Entity
@TypeChecked
@Table(schema = "pms", name = "patient_vaccinations" )
@SQLDelete(sql = "UPDATE pms.patient_vaccinations SET deleted = true where id = ? ")
@Where(clause = "deleted <> true or deleted is null")
class PatientVaccination extends AbstractAuditingEntity implements Serializable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient", referencedColumnName = "id")
    Patient patient

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", referencedColumnName = "id")
    Case caseId

    @GraphQLQuery
    @Column(name = "brand", columnDefinition = "varchar")
    String brand

    @GraphQLQuery
    @Column(name = "vaccine_date", columnDefinition = "date")
    Date vaccineDate

    @GraphQLQuery
    @Column(name = "dose_frequency", columnDefinition = "varchar")
    String doseFrequency

    @GraphQLQuery
    @Column(name = "administered_by", columnDefinition = "varchar")
    String administeredBy

    @GraphQLQuery
    @Column(name = "health_facility", columnDefinition = "varchar")
    String healthFacility
}