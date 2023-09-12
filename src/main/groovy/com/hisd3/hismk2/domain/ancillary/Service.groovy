package com.hisd3.hismk2.domain.ancillary

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.doh.TestProcedureType
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

enum ServiceTypes {
    PANEL,
    PACKAGE,
    BUNDLE,
    SINGLE
}

enum SystemType {
    RIS,
    LIS
}

@Entity
@Table(schema = "ancillary", name = "services")
class Service extends AbstractAuditingEntity {

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
    @JoinColumn(name = "department", referencedColumnName = "id")
    Department department

    @GraphQLQuery
    @Column(name = "servicename", columnDefinition = "varchar")
    String serviceName

    @GraphQLQuery
    @Column(name = "service_code", columnDefinition = "varchar")
    String serviceCode

    @GraphQLQuery
    @Column(name = "process_code", columnDefinition = "varchar")
    String processCode

    @GraphQLQuery
    @Column(name = "description", columnDefinition = "varchar")
    String description

    @GraphQLQuery
    @Column(name = "category", columnDefinition = "varchar")
    String category

    @GraphQLQuery
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", columnDefinition = "varchar")
    ServiceTypes serviceType

    @GraphQLQuery
    @Column(name = "diagnostic_service", columnDefinition = "boolean")
    Boolean diagnostic

    @GraphQLQuery
    @Column(name = "life_support", columnDefinition = "boolean")
    Boolean isLifeSupport

    @GraphQLQuery
    @Column(name = "base_price", columnDefinition = "numeric")
    BigDecimal basePrice

    @GraphQLQuery
    @Column(name = "cost", columnDefinition = "numeric")
    BigDecimal cost

    @GraphQLQuery
    @Column(name = "markup", columnDefinition = "numeric")
    BigDecimal markup

    @GraphQLQuery
    @Column(name = "readersfee", columnDefinition = "numeric")
    BigDecimal readersFee

    @GraphQLQuery
    @Column(name = "rf_percentage", columnDefinition = "numeric")
    BigDecimal rfPercentage

    @GraphQLQuery
    @Column(name = "flag_value", columnDefinition = "varchar")
    String flagValue

    @GraphQLQuery
    @Transient
    BigDecimal calculatedAmount = BigDecimal.ZERO

    @GraphQLQuery
    @Column(name = "generic_service", columnDefinition = "boolean")
    Boolean genericService

    @GraphQLQuery
    @Column(name = "revenue_to_user", columnDefinition = "boolean")
    Boolean revenueToUser

    @GraphQLQuery
    @Column(name = "hide_in_patient_diagnostics", columnDefinition = "boolean")
    Boolean hideInPatientDiagnostics

    @GraphQLQuery
    @Column(name = "hidden", columnDefinition = "boolean")
    Boolean hidden

    @GraphQLQuery
    @Column(name = "available", columnDefinition = "boolean")
    Boolean available

    @Transient
    Boolean stat = false

    @GraphQLQuery
    @Formula("concat(servicename, coalesce(' (' || category || ')', ''))")
    String serviceNameCategory

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_procedure_type", referencedColumnName = "code")
    TestProcedureType testProcedureType

    @GraphQLQuery
    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "varchar")
    SystemType type

    @GraphQLQuery
    @Column(name = "device", columnDefinition = "varchar")
    String device

    @GraphQLQuery
    @Column(name = "abbott_liscode", columnDefinition = "varchar")
    String abbottLISCode

    @Transient
    List<PackageContent> packageItems = []
}
