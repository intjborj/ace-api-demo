package com.hisd3.hismk2.domain.hrm.dto

class EmployeeDepartmentSalaryDto {
    UUID id
    String fullName
    String department
    String payFreq
    BigDecimal basicSalary
    String scheduleType
    Double contributionPagIbig
    String employeeNo

    EmployeeDepartmentSalaryDto(UUID id, String fullName, String department, String payFreq, BigDecimal basicSalary, String scheduleType, Double contributionPagIbig, String employeeNo) {
        this.id = id
        this.fullName = fullName
        this.department = department
        this.payFreq = payFreq
        this.basicSalary = basicSalary
        this.scheduleType = scheduleType
        this.contributionPagIbig = contributionPagIbig
        this.employeeNo = employeeNo
    }
}
