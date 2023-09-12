package com.hisd3.hismk2.domain.accounting.dto

import groovy.transform.Canonical

// START OF GLOBAL DTO ----------------------------------------


// END OF GLOBAL DTO ------------------------------------------
@Canonical
class GuarantorDto implements Serializable {
    UUID id
    String recordNo
    String name
    String address
    String type
}

@Canonical
class GuarantorBillingDto implements  Serializable {
    UUID id
    String billingNo
    String finalSoa
}


@Canonical
class AttendingPhysicianDto implements Serializable {
    UUID id
    String employeeNo
    String firstName
    String middleName
    String lastName
    String PhysNo
    String titleInit
    Boolean vatOrNon
    BigDecimal pfVatRate
    BigDecimal expandedWTaxRate
}


@Canonical
class GuarantorBillingItemDto implements  Serializable {
    UUID id
    String recordNo
    String description
    String itemType
    String transactionDate
    AttendingPhysicianDto attendingPhysician
    BigDecimal amount
}

@Canonical
class GuarantorPatientDto implements  Serializable {
    UUID id
    String patientNo
    String firstName
    String middleName
    String lastName
    String suffix
    String address
    String gender
}


@Canonical
class GuarantorPatientCaseDto implements  Serializable {
    UUID id
    String caseNo
    String registryType
    String admissionDatetime
    String dischargedDatetime
}

// START OF CUSTOMER DTO -----------------------------------------
@Canonical
class PeriodWithUOT implements Serializable{
    Integer period
    String unitOfTime
    String description
}

@Canonical
class PaymentDiscounts implements Serializable{
    Integer id
    BigDecimal discount
    String unitOfTime
    Integer start
    Integer end
}

@Canonical
class OverduePenalties implements Serializable{
    BigDecimal penalty
    String unitOfTime
    PeriodWithUOT condition
}


@Canonical
class CompanyDiscountAndPenalties {
    BigDecimal creditLimit
    PeriodWithUOT creditPeriod
    PeriodWithUOT disputePaymentPeriod
    PeriodWithUOT reconcileDisputePaymentPeriod
    PaymentDiscounts[] paymentDiscounts
    OverduePenalties overduePenalties

}

// END OF CUSTOMER DTO -----------------------------------------------