package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "discharge_opv", schema = "doh")
class OptDischargeOpv  extends AbstractAuditingEntity{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "new_patient", columnDefinition = "numeric")
    Integer newPatient

    @GraphQLQuery
    @Column(name = "revisit", columnDefinition = "numeric")
    Integer revisit

    @GraphQLQuery
    @Column(name = "adult", columnDefinition = "numeric")
    Integer adult

    @GraphQLQuery
    @Column(name = "pediatric", columnDefinition = "numeric")
    Integer pediatric

    @GraphQLQuery
    @Column(name = "adult_general_medicine", columnDefinition = "numeric")
    Integer adultGeneralMedicine

    @GraphQLQuery
    @Column(name = "specialty_non_surgical", columnDefinition = "numeric")
    Integer specialtyNonSurgical

    @GraphQLQuery
    @Column(name = "surgical", columnDefinition = "numeric")
    Integer surgical

    @GraphQLQuery
    @Column(name = "antenatal", columnDefinition = "numeric")
    Integer antenatal

    @GraphQLQuery
    @Column(name = "postnatal", columnDefinition = "numeric")
    Integer postnatal

    @GraphQLQuery
    @Column(name = "reporting_year", columnDefinition = "numeric")
    Integer reportingYear

    @GraphQLQuery
    @Column(name = "submitted_date_time", columnDefinition = "timestamp")
    Instant submittedDateTime

    @GraphQLQuery
    @Column(name = "doh_response", columnDefinition = "varchar")
    String dohResponse

}
