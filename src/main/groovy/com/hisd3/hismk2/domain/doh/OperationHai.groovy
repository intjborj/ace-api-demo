package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(name = "operation_hai", schema = "doh")
class OperationHai extends AbstractAuditingEntity{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "numhai", columnDefinition = "float")
    Float numHai

    @GraphQLQuery
    @Column(name = "numdischarges", columnDefinition = "float")
    Float numDischarges

    @GraphQLQuery
    @Column(name = "infectionrate", columnDefinition = "float")
    Float infectionRate

    @GraphQLQuery
    @Column(name = "patientnumvap", columnDefinition = "float")
    Float patientNumVap

    @GraphQLQuery
    @Column(name = "totalventilatordays", columnDefinition = "float")
    Float totalVentilatorDays

    @GraphQLQuery
    @Column(name = "resultvap", columnDefinition = "float")
    Float resultVap

    @GraphQLQuery
    @Column(name = "patientnumbsi", columnDefinition = "float")
    Float patientNumbsi

    @GraphQLQuery
    @Column(name = "totalnumcentralline", columnDefinition = "float")
    Float totalNumcentralline

    @GraphQLQuery
    @Column(name = "resultbsi", columnDefinition = "float")
    Float resultBsi

    @GraphQLQuery
    @Column(name = "patientnumuti", columnDefinition = "float")
    Float patientNumuti

    @GraphQLQuery
    @Column(name = "totalcatheterdays", columnDefinition = "float")
    Float totalcatheterDays

    @GraphQLQuery
    @Column(name = "resultuti", columnDefinition = "float")
    Float resultUti

    @GraphQLQuery
    @Column(name = "numssi", columnDefinition = "float")
    Float numSsi

    @GraphQLQuery
    @Column(name = "totalproceduresdone", columnDefinition = "float")
    Float totalProceduresDone

    @GraphQLQuery
    @Column(name = "resultssi", columnDefinition = "float")
    Float resultSsi

    @GraphQLQuery
    @Column(name = "reporting_year", columnDefinition = "Integer")
    Integer reportingYear

    @GraphQLQuery
    @Column(name = "submitted_date_time", columnDefinition = "timestamp")
    Instant submittedDateTime


    @GraphQLQuery
    @Column(name = "doh_response", columnDefinition = "varchar")
    String dohResponse

}
