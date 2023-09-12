package com.hisd3.hismk2.domain.hrm.dto

import com.hisd3.hismk2.domain.hrm.DepartmentSchedule
import io.leangen.graphql.annotations.GraphQLQuery

class EmployeeDto {

    public static final String ID_ALIAS = "e_id"
    public static final String FULL_NAME_ALIAS = "e_full_name"
    public static final String DEPARTMENT_ALIAS = "d_department_of_duty_id"
    public static final String DEPARTMENT_NAME_ALIAS = "d_department_name"
    public static final String DEPARTMENT_OF_DUTY_ALIAS = "dd_department_of_duty_id"
    public static final String DEPARTMENT_OF_DUTY_NAME_ALIAS = "dd_department_name"

    EmployeeDto(Object[] tuples, Map<String, Integer> aliasToIndexMap) {
        this.id = tuples[aliasToIndexMap.get(ID_ALIAS)] ? UUID.fromString(tuples[aliasToIndexMap.get(ID_ALIAS)] as String) : null
        this.fullName = tuples[aliasToIndexMap.get(FULL_NAME_ALIAS)]
        this.department = tuples[aliasToIndexMap.get(DEPARTMENT_ALIAS)] ? UUID.fromString(tuples[aliasToIndexMap.get(DEPARTMENT_ALIAS)] as String) : null
        this.departmentName = tuples[aliasToIndexMap.get(DEPARTMENT_NAME_ALIAS)]
        this.departmentOfDuty = tuples[aliasToIndexMap.get(DEPARTMENT_OF_DUTY_ALIAS)] ? UUID.fromString(tuples[aliasToIndexMap.get(DEPARTMENT_OF_DUTY_ALIAS)] as String) : null
        this.departmentOfDutyName = tuples[aliasToIndexMap.get(DEPARTMENT_OF_DUTY_NAME_ALIAS)]
    }

    EmployeeDto(UUID id, String fullName, UUID department, String departmentName) {
        this.id = id
        this.fullName = fullName
        this.department = department
        this.departmentName = departmentName
    }

    EmployeeDto(UUID id, String fullName, UUID department, String departmentName, List<DepartmentSchedule> schedule) {
        this.id = id
        this.fullName = fullName
        this.department = department
        this.departmentName = departmentName
        this.schedule = schedule
    }

    EmployeeDto(UUID id, String fullName, UUID department, String departmentName, UUID departmentOfDuty, String departmentOfDutyName, List<DepartmentSchedule> schedule) {
        this.id = id
        this.fullName = fullName
        this.department = department
        this.departmentName = departmentName
        this.departmentOfDuty = departmentOfDuty
        this.departmentOfDutyName = departmentOfDutyName
        this.schedule = schedule
    }
    @GraphQLQuery
    private UUID id

    @GraphQLQuery
    private String fullName

    @GraphQLQuery
    private UUID department

    @GraphQLQuery
    private String departmentName

    @GraphQLQuery
    private UUID departmentOfDuty

    @GraphQLQuery
    private String departmentOfDutyName

    @GraphQLQuery
    List<DepartmentSchedule> schedule

    @GraphQLQuery
    private LinkedHashMap<String, EmployeeScheduleDto> employeeSchedule = new LinkedHashMap<String, EmployeeScheduleDto>()

    @GraphQLQuery
    private LinkedHashMap<String, EmployeeScheduleDto> overtimeSchedule = new LinkedHashMap<String, EmployeeScheduleDto>()

    LinkedHashMap<String, Object> getEmployeeSchedule() {
        return employeeSchedule
    }

    LinkedHashMap<String, EmployeeScheduleDto> getOvertimeSchedule() {
        return overtimeSchedule
    }

    void setEmployeeSchedule(LinkedHashMap<String, EmployeeScheduleDto> employeeSchedule) {
        this.employeeSchedule = employeeSchedule
    }

    String getId() {
        return id
    }

    String getFullName() {
        return fullName
    }

    UUID getDepartment() {
        return department
    }

    void setDepartment(UUID department) {
        this.department = department
    }

    String getDepartmentName() {
        return departmentName
    }

    void setDepartmentName(String departmentName) {
        this.departmentName = departmentName
    }

    UUID getDepartmentOfDuty() {
        return departmentOfDuty
    }

    void setDepartmentOfDuty(UUID departmentOfDuty) {
        this.departmentOfDuty = departmentOfDuty
    }

    String getDepartmentOfDutyName() {
        return departmentOfDutyName
    }

    void setDepartmentOfDutyName(String departmentOfDutyName) {
        this.departmentOfDutyName = departmentOfDutyName
    }
}
