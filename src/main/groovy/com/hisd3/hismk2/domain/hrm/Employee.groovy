package com.hisd3.hismk2.domain.hrm

import com.fasterxml.jackson.annotation.JsonFormat
import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.types.JaversResolvable
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDateTime

@Canonical
class EducationalBackground implements Serializable {
    String highestEducation
    String degreeCourse
    String school
    Integer yearGraduated
}

@Canonical
class EducationalBackgroundList implements Serializable {
    List<EducationalBackground> educationalBackgrounds
}

@javax.persistence.Entity
@javax.persistence.Table(schema = "hrm", name = "employees")
class Employee extends AbstractAuditingEntity implements JaversResolvable {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    @NaturalId
    UUID id

    @OneToMany(
            mappedBy = "employee",
            cascade = javax.persistence.CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    List<EmployeeAllowance> employeeAllowance = new ArrayList<>()

    @ManyToMany(mappedBy = "employees")
    List<OtherDeduction> otherDeductions = new ArrayList<>()

    @GraphQLQuery
    @Formula("(select sum( COALESCE(a.amount,0)) from hrm.employee_allowance a where a.employee = id and COALESCE(a.deleted,false) != true)")
    BigDecimal totalAllowance

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`user`", referencedColumnName = "id")
    User user

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department", referencedColumnName = "id")
    Department department

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_of_duty", referencedColumnName = "id")
    Department departmentOfDuty

    @GraphQLQuery
    @Column(name = "employee_no", columnDefinition = "varchar")
    String employeeNo

    @GraphQLQuery
    @Column(name = "employee_id", columnDefinition = "varchar")
    String employeeId

    @GraphQLQuery
    @Column(name = "contribution_pagibig", columnDefinition = "numeric")
    Double contributionPagIbig

    @GraphQLQuery
    @Column(name = "first_name", columnDefinition = "varchar")
    @UpperCase
    String firstName

    @GraphQLQuery
    @Column(name = "last_name", columnDefinition = "varchar")
    @UpperCase
    String lastName

    @GraphQLQuery
    @UpperCase
    @Column(name = "middle_name", columnDefinition = "varchar")
    String middleName

    @GraphQLQuery
    @Column(name = "name_suffix", columnDefinition = "varchar")
    String nameSuffix

    @GraphQLQuery
    @Column(name = "email_address", columnDefinition = "varchar")
    String emailAddress

    @GraphQLQuery
    @Column(name = "nationality", columnDefinition = "varchar")
    String nationality

    @GraphQLQuery
    @Column(name = "civil_status", columnDefinition = "varchar")
    String civilStatus

    @GraphQLQuery
    @Column(name = "withold_tax_rate", columnDefinition = "numeric")
    BigDecimal witholdTaxRate

    @GraphQLQuery
    @Column(name = "address", columnDefinition = "varchar")
    String address

    @GraphQLQuery
    @Column(name = "country", columnDefinition = "varchar")
    String country

    @GraphQLQuery
    @Column(name = "state_province", columnDefinition = "varchar")
    String stateProvince

    @GraphQLQuery
    @Column(name = "city_municipality", columnDefinition = "varchar")
    String cityMunicipality

    @GraphQLQuery
    @Column(name = "barangay", columnDefinition = "varchar")
    String barangay

    @GraphQLQuery
    @Column(name = "gender", columnDefinition = "varchar")
    String gender

    @GraphQLQuery
    @Column(name = "dob", columnDefinition = "date")
    LocalDateTime dob

    @GraphQLQuery
    @Column(name = "emergency_contact_name", columnDefinition = "varchar")
    String emergencyContactName

    @GraphQLQuery
    @Column(name = "emergency_contact_address", columnDefinition = "varchar")
    String emergencyContactAddress

    @Column(name = "emergency_contact_relationship", columnDefinition = "varchar")
    String emergencyContactRelationship

    @GraphQLQuery
    @Column(name = "emergency_contact_no", columnDefinition = "varchar")
    String emergencyContactNo

    @GraphQLQuery
    @Column(name = "zip_code", columnDefinition = "varchar")
    String zipCode

    @GraphQLQuery
    @Column(name = "address_2", columnDefinition = "varchar")
    String address2

    @GraphQLQuery
    @Column(name = "employee_tel_no", columnDefinition = "varchar")
    String employeeTelNo

    @GraphQLQuery
    @Column(name = "employee_cel_no", columnDefinition = "varchar")
    String employeeCelNo

    @GraphQLQuery
    @Column(name = "philhealth_no", columnDefinition = "varchar")
    String philhealthNo

    @GraphQLQuery
    @Column(name = "sss_no", columnDefinition = "varchar")
    String sssNo

    @GraphQLQuery
    @Column(name = "tin_no", columnDefinition = "varchar")
    String tinNo

    @GraphQLQuery
    @Column(name = "blood_type", columnDefinition = "varchar")
    String bloodType

    @GraphQLQuery
    @Column(name = "basic_salary", columnDefinition = "money")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    BigDecimal basicSalary

    @GraphQLQuery
    @Column(name = "pay_freq", columnDefinition = "varchar")
    String payFreq

    @GraphQLQuery
    @Column(name = "schedule_type", columnDefinition = "varchar")
    String scheduleType

    @GraphQLQuery
    @Column(name = "position", columnDefinition = "uuid")
    UUID position

    @GraphQLQuery
    @Column(name = "position_type", columnDefinition = "varchar")
    String positionType

    //position_code is based on list of DOH specific positions/designation
    @GraphQLQuery
    @Column(name = "position_code", columnDefinition = "numeric")
    Integer positionCode

    @GraphQLQuery
    @Column(name = "position_code_others", columnDefinition = "numeric")
    Integer positionCodeOthers

    @GraphQLQuery
    @Formula("concat(last_name , coalesce(', ' || nullif(first_name,'') , ''), coalesce(' ' || nullif(middle_name,'') , ''), coalesce(' ' || nullif(name_suffix,'') , ''))")
    String fullName

    @GraphQLQuery
    @Formula("concat(last_name , coalesce(', ' || nullif(first_name,'') , ''), coalesce(' ' || nullif(substring(middle_name, 1, 1),'') , ''), coalesce(' ' || nullif(name_suffix,'') , ''))")
    String fullInitialName

    @GraphQLQuery
    @Formula("concat(coalesce(nullif(left(first_name,1),'') , '. '), coalesce('' || nullif(left(middle_name,1),'') , '. '), last_name)")
    String shortName

    @GraphQLQuery
    @Column(name = "bank_accnt_no", columnDefinition = "varchar")
    String bankAcnntNo

    @GraphQLQuery
    @Column(name = "bank_accnt_name", columnDefinition = "varchar")
    String bankAccntName

    @GraphQLQuery
    @Column(name = "prc_license_type", columnDefinition = "varchar")
    String prcLicenseType

    @GraphQLQuery
    @Column(name = "prc_license_no", columnDefinition = "varchar")
    String prcLicenseNo

    @GraphQLQuery
    @Column(name = "prc_expiry_date", columnDefinition = "timestamp without time zone")
    Instant prcExpiryDate

    @GraphQLQuery
    @Column(name = "ptr_no", columnDefinition = "varchar")
    String ptrNo

    @GraphQLQuery
    @Column(name = "s2_no", columnDefinition = "varchar")
    String s2No

    @GraphQLQuery
    @Column(name = "phic_no", columnDefinition = "varchar")
    String phicNo

    @GraphQLQuery
    @Column(name = "phic_group", columnDefinition = "varchar")
    String phicGroup

    @GraphQLQuery
    @Column(name = "phic_expiry_date", columnDefinition = "timestamp without time zone")
    Instant phicExpiryDate

    @GraphQLQuery
    @Column(name = "pmmc_no", columnDefinition = "varchar")
    String pmmcNo

    @GraphQLQuery
    @Column(name = "service_class", columnDefinition = "varchar")
    String serviceClass

    @GraphQLQuery
    @Column(name = "specialization", columnDefinition = "varchar")
    String specialization

    @GraphQLQuery
    @Column(name = "service_type", columnDefinition = "varchar")
    String serviceType

    @GraphQLQuery
    @Column(name = "vatable_or_non", columnDefinition = "bool")
    Boolean vatable

    @GraphQLQuery
    @Column(name = "pf_vat_rate", columnDefinition = "numeric")
    BigDecimal pfVatRate

    @GraphQLQuery
    @Column(name = "expanded_wtax_rate", columnDefinition = "numeric")
    BigDecimal expandedVatRate

    @GraphQLQuery
    @Column(name = "biometric_no", columnDefinition = "numeric")
    Integer biometricNo

    @GraphQLQuery
    @Column(name = "rf_vat", columnDefinition = "numeric")
    BigDecimal rfVat

    @GraphQLQuery
    @Column(name = "care_provider_type", columnDefinition = "varchar")
    String careProvider

    @GraphQLQuery
    @Column(name = "is_active", columnDefinition = "boolean")
    Boolean isActive

    @GraphQLQuery
    @Column(name = "bank_account_name", columnDefinition = "varchar")
    String bankAccountName

    @GraphQLQuery
    @Column(name = "bank_account_no", columnDefinition = "varchar")
    String bankAccountNo

    @GraphQLQuery
    @Column(name = "pag_ibig_id", columnDefinition = "varchar")
    String pagIbigId

    @GraphQLQuery
    @Column(name = "profession_designation", columnDefinition = "varchar")
    String professionDesignation

    @GraphQLQuery
    @Column(name = "employee_type", columnDefinition = "varchar")
    String employeeType

    @GraphQLQuery
    @Column(name = "signature1", columnDefinition = "varchar")
    String signature1

    @GraphQLQuery
    @Column(name = "signature2", columnDefinition = "varchar")
    String signature2

    @GraphQLQuery
    @Column(name = "signature3", columnDefinition = "varchar")
    String signature3

    @GraphQLQuery
    @Column(name = "title_initials", columnDefinition = "varchar")
    String titleInitials

    @GraphQLQuery
    @Column(name = "supplier_id", columnDefinition = "uuid")
    UUID supplierId

    @GraphQLQuery
    @Column(name = "position_designation", columnDefinition = "varchar")
    String positionDesignation

    @GraphQLQuery
    @Formula("concat(first_name , coalesce(' ' || nullif(middle_name,'') , ''), coalesce(' ' || nullif(last_name,'') , ''), coalesce(' ' || nullif(name_suffix,'') , ''), coalesce(', ' || nullif(title_initials,'') , ''))")
    String fullnameWithTitle

    @GraphQLQuery
    @Formula("concat(first_name , coalesce(' ' || nullif(concat(left(middle_name,1),'.'),'') , ''), coalesce(' ' || nullif(last_name,'') , ''), coalesce(' ' || nullif(name_suffix,'') , ''), coalesce(', ' || nullif(title_initials,'') , ''))")
    String fullnameMiddleInitialWithTitle

    @GraphQLQuery
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    List<EmployeeSchedule> employeeSchedule

    @GraphQLQuery
    @Column(name = "is_allowed_co_manage", columnDefinition = "boolean default false")
    Boolean isAllowedCoManage

    @GraphQLQuery
    @Column(name = "exclude_payroll", columnDefinition = "boolean default false")
    Boolean excludePayroll

    @GraphQLQuery
    @Column(name = "is_specialty_board_certified ", columnDefinition = "boolean")
    Boolean isSpecialtyBoardCertified

    @GraphQLQuery
    @Column(name = "birthplace", columnDefinition = "varchar")
    String birthplace

    @GraphQLQuery
    @Type(type = "jsonb")
    @Column(name = "educational_background", columnDefinition = "jsonb")
    EducationalBackgroundList educationalBackgroundList


    @GraphQLQuery
    @Transient
    BigDecimal getWtxDefault() {
        // detect if theres an integer part...
        if (witholdTaxRate) {
            if (witholdTaxRate.toBigInteger() > 0) {
                // this needs to be convert to be multiplier
                return (witholdTaxRate / 100.0).setScale(2, RoundingMode.HALF_EVEN)
            } else {
                return witholdTaxRate
            }
        }
        return null
    }

    @GraphQLQuery
    @Transient
    BigDecimal getPfRateDefault() {
        // detect if theres an integer part...
        if (pfVatRate) {
            if (pfVatRate.toBigInteger() > 0) {
                // this needs to be convert to be multiplier
                return (pfVatRate / 100.0).setScale(2, RoundingMode.HALF_EVEN)
            } else {
                return pfVatRate
            }
        }
        return null
    }

    // `notificationTyp` will tell FE what notif service to use
    // this will have to be removed as soon as we've determined
    // what final notif service to use (ably vs default)
    @GraphQLQuery
    @Transient
    String notificationType;

    @Override
    String resolveEntityForJavers() {
        return fullName
    }


}
