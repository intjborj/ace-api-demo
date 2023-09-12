package com.hisd3.hismk2.domain.pms

import com.hisd3.hismk2.domain.AbstractAuditingEntity
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
@Table(schema = "pms", name = "vaccination_shot")
@SQLDelete(sql = "UPDATE pms.vaccination_shot SET deleted = true where id = ? ", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted <> true or deleted is null")
class VaccinationShot extends AbstractAuditingEntity {

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
    @Column(name = "vaccine_name", columnDefinition = "varchar")
    String brand

    @GraphQLQuery
    @Column(name = "dose", columnDefinition = "varchar")
    String dose

    @GraphQLQuery
    @Column(name = "date_administered", columnDefinition = "timestamp")
    Instant dateAdministered

    @GraphQLQuery
    @Column(name = "administered_by", columnDefinition = "varchar")
    String administeredBy

    @GraphQLQuery
    @Column(name = "covid_shot", columnDefinition = "boolean")
    Boolean covidShot
}
