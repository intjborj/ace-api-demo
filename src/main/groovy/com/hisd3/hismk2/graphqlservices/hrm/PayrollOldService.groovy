package com.hisd3.hismk2.graphqlservices.hrm

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.Payroll
import com.hisd3.hismk2.domain.hrm.Payslip
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.domain.hrm.enums.PayrollStatus
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hrm.EmployeeRepository

import com.hisd3.hismk2.repository.hrm.PayslipRepository
import com.hisd3.hismk2.services.PayrollTimeKeepingCalculatorService
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.time.Instant


@TypeChecked
@Component
@GraphQLApi
class PayrollOldService extends AbstractDaoService<Payroll> {

    PayrollOldService() {
        super(Payroll.class)
    }

    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    PayslipRepository payslipRepository

    @Autowired
    PayrollTimeKeepingCalculatorService payrollCalculatorService

    @PersistenceContext
    EntityManager entityManager


    //==============================QUERY==============================\\

    @GraphQLQuery(name = "getOnePayroll", description = "Get/View one payroll")
    Payroll getOnePayroll(
            @GraphQLArgument(name = "id") UUID id
    ) {
        List<Employee> payrollEmployees = entityManager.createQuery("""
            Select e from Payroll p
                left join p.payslip ps
                left join ps.employee e
            where p.id = :id
            order by e.fullName
        """, Employee.class).setParameter("id", id).getResultList()

        List<Payroll> payroll = createQuery("""
            Select p from Payroll p
                left join fetch p.payslip ps
                left join ps.employee e
            where p.id = :id
            order by e.fullName
        """, Payroll).setParameter("id", id).getResultList()

        payroll = createQuery("""
            Select p from Payroll p
                left join p.payslip ps
                left join ps.employee e
                left join fetch p.logFlags l
            where p.id = :id
            order by e.fullName
        """).setParameter("id", id).getResultList()
        return payroll[0]
    }

    @GraphQLQuery(name = "getAllPayrolls", description = "Get all the payrolls")
    List<Payroll> getAllPayrolls() {
        List<Payroll> payrolls = createQuery("""
            Select p from Payroll p
            order by p.createdDate DESC
        """, Payroll).getResultList()
        return payrolls
    }

    @GraphQLQuery
    Map<String, Object> testLeaveSchedule(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "startDate") Instant startDate,
            @GraphQLArgument(name = "endDate") Instant endDate

    ) {
        payrollCalculatorService.getLeaveSchedule(id, startDate, endDate)
    }

    //==============================QUERY==============================\\

    //============================MUTATION=============================\\
//
//    @GraphQLMutation(name = "upsertPayroll", description = "Create payroll")
//    GraphQLRetVal<Payroll> upsertPayroll(
//            @GraphQLArgument(name = "id") UUID id,
//            @GraphQLArgument(name = "fields") Map<String, Object> fields,
//            @GraphQLArgument(name = "employees") List<UUID> employees
//    ) {
//        if (id) {
//            Payroll payroll = findOne(id)
//            if (payroll.status == PayrollStatus.NEW) {
//                return new GraphQLRetVal<Payroll>(null, false, "Can't update payroll details after calculated or finalized.")
//            }
//            payroll = objectMapper.updateValue(payroll, fields)
//            List<Employee> selectedEmployees = employeeRepository.getEmployees(employees)
//            payroll.payslip.clear()
//            selectedEmployees.each { it ->
//                Payslip payslip = new Payslip()
//                payslip.payroll = payroll
//                payslip.employee = it
//                payroll.payslip.add(payslip)
//            }
//
//            payroll = save(payroll)
//
//            return new GraphQLRetVal<Payroll>(payroll, true, "Successfully updated Payroll details.")
//        } else {
//            Payroll payroll = objectMapper.convertValue(fields, Payroll)
//            payroll.status = PayrollStatus.NEW
//            List<Employee> selectedEmployees = employeeRepository.getEmployees(employees)
//            List<Payslip> payslips = []
//            payroll = save(payroll)
//            selectedEmployees.each { it ->
//                Payslip payslip = new Payslip()
//                payslip.employee = it
//                payslip.payroll = payroll
//                payslip.basicSalary = it.basicSalary.doubleValue()
//                payslip.payFreq = it.payFreq
//                payslips.add(payslip)
//            }
//            payslipRepository.saveAll(payslips)
//
//            return new GraphQLRetVal<Payroll>(payroll, true, "Successfully created Payroll details.")
//        }
//
//    }
//
//    @GraphQLMutation(name = "removeEmployeeFromPayroll", description = "Remove employee from payroll")
//    GraphQLRetVal<Employee> removeEmployeeFromPayroll(
//            @GraphQLArgument(name = "id") UUID id
//    ) {
//        if (!id) return new GraphQLRetVal<Employee>(null, false, "Failed to remove employee from payroll")
//
//        Payslip payslip = payslipRepository.findPayslipWithPayroll(id)
//        payslip = payslipRepository.findPayslipWithEmployee(id)
//        if (!payslip) return new GraphQLRetVal<Employee>(null, false, "Failed to remove employee from payroll")
//        if (payslip.payroll.status == PayrollStatus.FINALIZED)
//            return new GraphQLRetVal<Employee>(null, false, "Failed to remove employee from payroll")
//
//        payslipRepository.delete(payslip)
//
//        return new GraphQLRetVal<Employee>(payslip.employee, true, "Successfully removed employee from payroll")
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @GraphQLMutation(name = "deletePayroll", description = "Delete one payroll")
//    GraphQLRetVal<String> deletePayroll(
//            @GraphQLArgument(name = "id") UUID id
//    ) {
//        if (!id) return new GraphQLRetVal<String>(null, false, "Failed to delete Payroll.")
//        List<Payroll> payroll = createQuery("""
//            Select p from Payroll p
//            left join fetch p.payslip
//            where p.id = :id
//        """).setParameter("id", id).getResultList()
//        Payroll foundPayroll = payroll[0]
//        if (!foundPayroll) return new GraphQLRetVal<String>(null, false, "Failed to delete Payroll.")
//        payslipRepository.deleteAll(foundPayroll.payslip)
//        delete(foundPayroll)
//        return new GraphQLRetVal<String>(null, true, "Successfully deleted payroll.")
//    }

    //============================MUTATION=============================\\

}
