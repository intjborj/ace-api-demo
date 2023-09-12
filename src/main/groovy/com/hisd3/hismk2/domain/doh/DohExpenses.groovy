package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(name = "expenses", schema = "doh")
class DohExpenses extends AbstractAuditingEntity{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "salarieswages", columnDefinition = "numeric")
    Double salariesWages

    @GraphQLQuery
    @Column(name = "employeebenefits", columnDefinition = "numeric")
    Double employeeBenefits

    @GraphQLQuery
    @Column(name = "allowances", columnDefinition = "numeric")
    Double allowances

    @GraphQLQuery
    @Column(name = "totalps", columnDefinition = "numeric")
    Double totalps

    @GraphQLQuery
    @Column(name = "totalamountmedicine", columnDefinition = "numeric")
    Double totalAmountMedicine

    @GraphQLQuery
    @Column(name = "totalamountmedicalsupplies", columnDefinition = "numeric")
    Double totalAmountMedicalSupplies

    @GraphQLQuery
    @Column(name = "totalamountutilities", columnDefinition = "numeric")
    Double totalAmountUtilities

    @GraphQLQuery
    @Column(name = "totalamountnonmedicalservice", columnDefinition = "numeric")
    Double totalAmountNonMedicalService

    @GraphQLQuery
    @Column(name = "totalmooe", columnDefinition = "numeric")
    Double totalMooe

    @GraphQLQuery
    @Column(name = "amountinfrastructure", columnDefinition = "numeric")
    Double amountInfrastructure

    @GraphQLQuery
    @Column(name = "amountequipment", columnDefinition = "numeric")
    Double amountEquipment

    @GraphQLQuery
    @Column(name = "totalco", columnDefinition = "numeric")
    Double totalCo

    @GraphQLQuery
    @Column(name = "grandtotal", columnDefinition = "numeric")
    Double grandTotal

    @GraphQLQuery
    @Column(name = "reporting_year", columnDefinition = "numeric")
    Integer reportingYear

    @GraphQLQuery
    @Column(name = "submitted_date_time", columnDefinition = "timestamp")
    Instant submittedDateTime
}
