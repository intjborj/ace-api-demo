package com.hisd3.hismk2.rest.dto

class EmployeeDto {
    UUID id
    String fullName
    DepartmentDto department

    EmployeeDto(UUID id, String fullName, UUID departmentId, String departmentName) {
        this.id = id
        this.fullName = fullName
        this.department = new DepartmentDto(departmentId, departmentName)
    }
}
