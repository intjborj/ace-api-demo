package com.hisd3.hismk2.graphqlservices.hrm

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.hrm.DepartmentSchedule
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.hrm.DepartmentScheduleRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@TypeChecked
@Component
@GraphQLApi
class DepartmentScheduleService {

    @Autowired
    DepartmentScheduleRepository deptScheduleRepository

    @Autowired
    DepartmentRepository departmentRepository

    @Autowired
    ObjectMapper objectMapper


    //================================Query================================\\

    @GraphQLQuery(name = "getDepartmentSchedule", description = "get department schedule config")
    List<Department> getDepartmentSchedule(
            @GraphQLArgument(name = "id") UUID id
    ) {
        if (!id) return departmentRepository.getDepartmentSchedule()
        else return departmentRepository.getOneDepartmentSchedule(id)
    }

    @GraphQLQuery(name = "getOneDepartmentSchedule", description = "get one department schedule config")
    List<DepartmentSchedule> getOneDepartmentSchedule(
            @GraphQLArgument(name = "id") UUID id
    ) {
        return deptScheduleRepository.getOneDepartmentSchedule(id)
    }

    //================================Query================================\\

    //===============================Mutation==============================\\

    @GraphQLMutation(name = "upsertDepartementSchedule", description = "create or update department schedule config.")
    GraphQLRetVal<DepartmentSchedule> upsertDepartementSchedule(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "department_id") UUID department_id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        if (!department_id) return new GraphQLRetVal<DepartmentSchedule>(null, false, "Failed to create department schedule.")
        Department department = departmentRepository.findById(department_id).get()
        if (id) {
            DepartmentSchedule schedule = deptScheduleRepository.findById(id).get()
            schedule = objectMapper.updateValue(schedule, fields)
            schedule.department = department
            deptScheduleRepository.save(schedule)
            return new GraphQLRetVal<DepartmentSchedule>(schedule, true, "Successfully updated department schedule.")
        } else {
            DepartmentSchedule schedule = objectMapper.convertValue(fields, DepartmentSchedule)
            schedule.department = department
            deptScheduleRepository.save(schedule)
            return new GraphQLRetVal<DepartmentSchedule>(schedule, true, "Successfully created department schedule")
        }
    }

    @GraphQLMutation(name = "deleteDepartmentSchedule", description = "Delete one department schedule config.")
    GraphQLRetVal<String> deleteDepartmentSchedule(
            @GraphQLArgument(name = "id") UUID id
    ) {
        if (!id) return new GraphQLRetVal<String>("ERROR", false, "Failed to delete department schedule config.")
        DepartmentSchedule schedule = deptScheduleRepository.findById(id).get()
        deptScheduleRepository.delete(schedule)
        return new GraphQLRetVal<String>("OK", true, "Successfully deleted department schedule config")
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "copyDepartmentSchedule", description = "Copy department schedule from other department")
    GraphQLRetVal<String> copyDepartmentSchedule(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "department") UUID department
    ) {
        if (!id || !department) return new GraphQLRetVal<String>("ERROR", false, "Failed to copy department schedule.")
        if (id == department) return new GraphQLRetVal<String>("ERROR", false, "Can't copy department's own schedule")

        Department departmentToCopy = departmentRepository.getOneDepartmentWithSchedule(department)
        Department selectedDepartment = departmentRepository.getOneDepartmentWithSchedule(id)

        List<DepartmentSchedule> deleteSchedule = []
        selectedDepartment.workSchedule.each { deleteSchedule.add(it) }
        deptScheduleRepository.deleteAll(deleteSchedule)

        List<DepartmentSchedule> schedules = []
        departmentToCopy.workSchedule.each {
            DepartmentSchedule schedule = new DepartmentSchedule()
            schedule.title = it.title
            schedule.label = it.label
            schedule.dateTimeStartRaw = it.dateTimeStartRaw
            schedule.dateTimeEndRaw = it.dateTimeEndRaw
            schedule.color = it.color
            schedule.mealBreakEnd = it.mealBreakEnd
            schedule.mealBreakStart = it.mealBreakStart
            schedule.department = selectedDepartment
            schedules.add(schedule)
        }
        deptScheduleRepository.saveAll(schedules)


        return new GraphQLRetVal<String>("OK", true, "Successfully copied department schedule.")
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "clearSchedule")
    GraphQLRetVal<String> clearSchedule(
            @GraphQLArgument(name = "id") UUID id
    ) {
        if (!id) return new GraphQLRetVal<String>("ERROR", false, "Failed to clear department schedule")

        List<DepartmentSchedule> schedules = deptScheduleRepository.getOneDepartmentSchedule(id)
        deptScheduleRepository.deleteAll(schedules)

        return new GraphQLRetVal<String>("OK", true, "Successfully cleared department schedule.")
    }


    //===============================Mutation==============================\\
}
