package com.hisd3.hismk2.domain.pms

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.inventory.Item
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
@Table(schema = "pms", name = "vaccination")
@SQLDelete(sql = "UPDATE pms.vaccination SET deleted = true where id = ? ", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted <> true or deleted is null")

class Vaccination extends AbstractAuditingEntity {

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
    @Column(name = "vaccinated", columnDefinition = "boolean")
    Boolean vaccinated

    @GraphQLQuery
    @Column(name = "full_vaccinated", columnDefinition = "boolean")
    Boolean fullVaccinated

    @GraphQLQuery
    @Column(name = "brand", columnDefinition = "varchar")
    String brand

    @GraphQLQuery
    @Column(name = "other", columnDefinition = "varchar")
    String other

    @GraphQLQuery
    @Column(name = "first_dose", columnDefinition = "timestamp")
    Instant firstDose

    @GraphQLQuery
    @Column(name = "second_dose", columnDefinition = "timestamp")
    Instant secondDose

    @GraphQLQuery
    @Column(name = "not_applicable", columnDefinition = "boolean")
    Boolean notApplicable
}
