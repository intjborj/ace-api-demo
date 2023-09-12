package com.hisd3.hismk2.graphqlservices.hrm

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.OtherDeduction
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.hrm.OtherDeductionRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class OtherDeductionService {

    @Autowired
    private OtherDeductionRepository otherDeductionRepository

    @Autowired
    private EmployeeRepository employeeRepository

    @Autowired
    private ObjectMapper objectMapper


    @GraphQLQuery(name = 'getOtherDeductionByPagination', description = 'list of all other deductions with pagination')
    Page<OtherDeduction> getOtherDeductionByPagination(
            @GraphQLArgument(name = "pageSize") Integer pageSize,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "filter") String filter
    ) {
        return otherDeductionRepository.getOtherDeductions(filter, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, 'createdDate')))
    }

    @GraphQLQuery(name = 'getOtherDeductionEmployeesId', description = 'Get all ids of employees of other deduction')
    List<UUID> getOtherDeductionEmployeesId(
            @GraphQLArgument(name = "id") UUID id
    ) {
        return otherDeductionRepository.findOtherDeductionEmployeeId(id)
    }

    @GraphQLQuery(name = 'getOneOtherDeduction', description = 'get one other deduction')
    OtherDeduction getOneOtherDeduction(
            @GraphQLArgument(name = "id") UUID id
    ) {
        try {
            if (id) {
                OtherDeduction otherDeduction = null
                otherDeductionRepository.findById(id).ifPresent { otherDeduction = it }
                if (!otherDeduction) throw new RuntimeException("Failed to fetch other deduction.")
                return otherDeduction
            } else
                throw new RuntimeException("Failed to fetch other deduction.")
        }
        catch (e) {
            throw new RuntimeException("Failed to fetch other deduction.")

        }
    }

    @GraphQLQuery(name = "getOtherDeductionEmployees")
    Page<Employee> getOtherDeductionEmployees(@GraphQLArgument(name = "id") UUID id,
                                              @GraphQLArgument(name = "pageSize") Integer pageSize,
                                              @GraphQLArgument(name = "page") Integer page,
                                              @GraphQLArgument(name = "filter") String filter = "") {
        return otherDeductionRepository
                .findOneOtherDeductionEmployees(
                        id,
                        filter,
                        PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, 'createdDate'))
                )
    }

    @GraphQLMutation
    GraphQLResVal<OtherDeduction> upsertOtherDeduction(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        try {
            if (id) {
                OtherDeduction otherDeduction = otherDeductionRepository.findById(id).get()
                if (otherDeduction) {
                    objectMapper.updateValue(otherDeduction, fields)
                    def newSave = otherDeductionRepository.save(otherDeduction)
                    return new GraphQLResVal<OtherDeduction>(newSave, true, 'Successfully Updated Other Deduction.')
                }
                return new GraphQLResVal<OtherDeduction>(null, false, 'Failed to Update Other Deduction.')
            } else {
                OtherDeduction otherDeduction = new OtherDeduction()
                objectMapper.updateValue(otherDeduction, fields)
                def newSave = otherDeductionRepository.save(otherDeduction)
                return new GraphQLResVal<OtherDeduction>(newSave, true, 'Successfully Created Other Deduction.')
            }
        } catch (e) {
            return new GraphQLResVal<OtherDeduction>(null, false, e.message)
        }
    }

    @GraphQLMutation
    GraphQLResVal<String> upsertEmployeeToOtherDeduction(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "employees") List<UUID> employees
    ) {
        try {
            if (id) {
                OtherDeduction otherDeduction = null
                otherDeductionRepository.findOneOtherDeductionWithEmployees(id).ifPresent { otherDeduction = it }

                if (otherDeduction && employees.size() > 0) {

                    def removeEmployees = []
                    otherDeduction.employees.each {
                        // if in employees we remove
                        if (!employees.contains(it.id)) {
                            employees.remove(it.id)
                            removeEmployees.add(it)
                        }
                    }
                    otherDeduction.employees.removeAll(removeEmployees)
                    def employeeList = employeeRepository.getEmployees(employees)
                    otherDeduction.employees.addAll(employeeList)

                    otherDeductionRepository.save(otherDeduction)
                    return new GraphQLResVal<>(null, true, 'Successfully updated other deduction employees.')
                } else if (employees.size() == 0) {
                    otherDeduction.employees.clear()
                    otherDeductionRepository.save(otherDeduction)
                    return new GraphQLResVal<>(null, true, 'Successfully updated other deduction employees.')
                }
                return new GraphQLResVal<>(null, false, 'Failed to update other deduction employees.')
            } else {
                return new GraphQLResVal<>(null, false, 'Failed to update other deduction employees.')
            }
        } catch (e) {
            e.printStackTrace()
            return new GraphQLResVal<>(null, false, e.message)
        }
    }

    @GraphQLMutation
    GraphQLResVal<Boolean> deleteOtherDeduction(
            @GraphQLArgument(name = "id") UUID id
    ) {
        try {
            if (id) {
                otherDeductionRepository.deleteById(id)
                return new GraphQLResVal<Boolean>(true, true, 'Successfully deleted Other Deduction.')
            }
            return new GraphQLResVal<Boolean>(false, false, 'Failed to delete Other Deduction.')
        } catch (e) {
            return new GraphQLResVal<Boolean>(false, false, e.message)
        }
    }

    @GraphQLMutation
    GraphQLResVal<String> removeEmployeeFromOtherDeduction(
            @GraphQLArgument(name = "id", description = "ID of the other deduction that you want to delete the employee from.") UUID id,
            @GraphQLArgument(name = "employeeId", description = "ID of the employee that you want to delete") UUID employeeId
    ) {
        try {
            if (id) {
                OtherDeduction otherDeduction = null
                Employee employee = null
                otherDeductionRepository.findOneOtherDeductionWithEmployees(id).ifPresent { otherDeduction = it }
                employeeRepository.findById(employeeId).ifPresent { employee = it }
                if (!otherDeduction || !employee) return new GraphQLResVal<String>(null, false, "Failed to remove employee from other deduction.")

                otherDeduction.employees.remove(employee)
                otherDeductionRepository.save(otherDeduction)
                return new GraphQLResVal<String>(null, true, "Successfully removed employee from other deduction.")
            }
            return new GraphQLResVal<String>(null, false, "Failed to remove employee from other deduction.")

        } catch (e) {
            return new GraphQLResVal<String>(null, false, "Failed to remove employee from other deduction.")
        }
    }
}
