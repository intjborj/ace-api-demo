package com.hisd3.hismk2.rest.dto

class DepartmentDto {
    UUID id
    String departmentName

    DepartmentDto(UUID id, String departmentName) {
        this.id = id
        this.departmentName = departmentName
    }
}
