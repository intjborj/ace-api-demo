package com.hisd3.hismk2.graphqlservices.payroll

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.dto.EmployeeAccumulatedAttendanceDto
import com.hisd3.hismk2.domain.hrm.enums.PayrollStatus
import com.hisd3.hismk2.domain.payroll.AccumulatedLog
import com.hisd3.hismk2.domain.payroll.AccumulatedLogSummary
import com.hisd3.hismk2.domain.payroll.FinalAccumulatedLogs
import com.hisd3.hismk2.domain.payroll.OriginalAccumulatedLogs
import com.hisd3.hismk2.domain.payroll.OtherDeductionEmployee
import com.hisd3.hismk2.domain.payroll.Payroll
import com.hisd3.hismk2.domain.payroll.PayrollEmployee
import com.hisd3.hismk2.domain.payroll.PayrollOtherDeduction
import com.hisd3.hismk2.domain.payroll.Timekeeping
import com.hisd3.hismk2.domain.payroll.TimekeepingEmployee
import com.hisd3.hismk2.domain.payroll.Totals
import com.hisd3.hismk2.domain.payroll.enums.AccumulatedLogStatus
import com.hisd3.hismk2.domain.payroll.enums.PayrollEmployeeStatus
import com.hisd3.hismk2.domain.payroll.enums.TimekeepingEmployeeStatus
import com.hisd3.hismk2.domain.payroll.enums.TimekeepingStatus
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.payroll.AccumulatedLogRepository
import com.hisd3.hismk2.repository.payroll.AccumulatedLogSummaryRepository
import com.hisd3.hismk2.repository.payroll.PayrollRepository
import com.hisd3.hismk2.repository.payroll.TimekeepingEmployeeRepository
import com.hisd3.hismk2.repository.payroll.TimekeepingRepository
import com.hisd3.hismk2.services.PayrollTimeKeepingCalculatorService
import com.hisd3.hismk2.services.TimeKeepingCalculatorService
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.xmlsoap.schemas.soap.encoding.Time

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.time.Instant

@TypeChecked
@Component
@GraphQLApi
@Transactional(rollbackFor = Exception.class)
class TimekeepingService implements IPayrollOperation<Timekeeping, TimekeepingEmployee> {

    @Autowired
    TimekeepingRepository timekeepingRepository

    @Autowired
    PayrollRepository payrollRepository

    @Autowired
    TimekeepingEmployeeRepository timekeepingEmployeeRepository

    @Autowired
    TimeKeepingCalculatorService timeKeepingCalculatorService

    @Autowired
    AccumulatedLogSummaryRepository accumulatedLogSummaryRepository

    @Autowired
    AccumulatedLogRepository accumulatedLogRepository


    @Autowired
    ObjectMapper objectMapper

    @PersistenceContext
    EntityManager entityManager

    private Timekeeping timekeeping;


    //=================================QUERY=================================\\
    @GraphQLQuery(name = "timekeepings", description = "Get All timekeepings")
    List<Timekeeping> findAll() {
        timekeepingRepository.findAll().sort { it.createdDate }
    }

    @GraphQLQuery(name = "getTimekeepingById", description = "Get timekeeping by ID")
    Timekeeping findById(@GraphQLArgument(name = "id") UUID id) {
        if (id) {
            return timekeepingRepository.findById(id).get()
        } else {
            return null
        }

    }

    @GraphQLQuery(name = "getTimekeepingByPayrollId", description = "Get timekeeping by ID")
    Timekeeping findByPayrollId(@GraphQLArgument(name = "id") UUID id) {
        if (id) {
            return timekeepingRepository.findByPayrollId(id).get()
        } else {
            return null
        }

    }


    @GraphQLQuery(name = "getTimekeepingAndEmployees", description = "Get All timekeepings")
    Timekeeping getTimekeepingAndEmployees(@GraphQLArgument(name = "timekeepingId") UUID timekeepingId) {

        Timekeeping timekeeping = timekeepingRepository.findById(timekeepingId).get()
        // List<TimekeepingEmployee> timekeepingEmployeeList = timekeepingEmployeeRepository.findByTimekeeping(timekeepingId)

        return timekeeping
        //List <Employee> employees =timekeepingEmployeeRepository.getTimekeepingEmployees(timekeepingId)
    }


    //=================================QUERY=================================\\


    //================================MUTATION================================\\
    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation
    GraphQLRetVal<Timekeeping> updateTimekeepingStatus(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "status") String status

    ) {

        Timekeeping timekeeping = timekeepingRepository.findById(id).get()
        if (status == 'ACTIVE')
            timekeeping.status = TimekeepingStatus.ACTIVE
        else if (status == 'CANCELLED')
            timekeeping.status = TimekeepingStatus.CANCELLED
        else if (status == 'FINALIZED')
            timekeeping.status = TimekeepingStatus.FINALIZED

        timekeepingRepository.save(timekeeping)

        return new GraphQLRetVal<Timekeeping>(timekeeping, true, "Successfully updated timekeeping")

    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation
    GraphQLRetVal<Timekeeping> calculateAllAccumulatedLogs(
            @GraphQLArgument(name = "id") UUID id
    ) {
        Payroll payroll = payrollRepository.findById(id).get()
        recalculateAllEmployee(payroll)
        return new GraphQLRetVal<Timekeeping>(payroll.timekeeping, true, "Successfully updated timekeeping")

    }


    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation
    GraphQLRetVal<String> calculateOneTimekeepingEmployee(
            @GraphQLArgument(name = "id") UUID id
    ) {
            TimekeepingEmployee timekeepingEmployee = timekeepingEmployeeRepository.findById(id).get()
        Payroll payroll = timekeepingEmployee.payrollEmployee.payroll
        PayrollEmployee payrollEmployee= timekeepingEmployee.payrollEmployee
        recalculateEmployee(payrollEmployee, payroll)
        return new GraphQLRetVal<String>(null, true, "Successfully recalculated timekeeping employee.")
    }




//===================================================================

    @Override
    Timekeeping startPayroll(Payroll payroll) {
        Timekeeping timekeeping = new Timekeeping();
        timekeeping.payroll = payroll
        timekeeping.status = TimekeepingStatus.ACTIVE
        timekeeping = timekeepingRepository.save(timekeeping)

        payroll.timekeeping = timekeeping
        addEmployees(payroll.payrollEmployees, payroll)
        return timekeeping

    }

    @Override
    void finalizePayroll(Payroll payroll) {

    }

//===================================================================


    @Override
    List<TimekeepingEmployee> addEmployees(List<PayrollEmployee> payrollEmployees, Payroll payroll) {

        Timekeeping timekeeping = payroll.timekeeping
        List<TimekeepingEmployee> timekeepingEmployeeList = []
        if (payrollEmployees.size() > 0) {
            payrollEmployees.each {
                if (!it.isOld) {
                    TimekeepingEmployee timekeepingEmployee = new TimekeepingEmployee()
                    timekeepingEmployee.status = PayrollEmployeeStatus.DRAFT
                    timekeepingEmployee.payrollEmployee = it
                    timekeepingEmployee.timekeeping = timekeeping
                    timekeepingEmployeeList.push(timekeepingEmployee)
                }
            }
        }
        timekeeping.timekeepingEmployees = timekeepingEmployeeRepository.saveAll(timekeepingEmployeeList)
        generateSummaries(timekeeping.timekeepingEmployees,payroll)
        payroll.timekeeping = timekeeping
        return timekeepingEmployeeList
    }

    @Override
    void recalculateAllEmployee(Payroll payroll) {
        ArrayList<AccumulatedLogSummary> accumulatedLogSummaryList = []
        payroll.timekeeping.timekeepingEmployees.each {
            it.accumulatedLogSummaryList.clear()
            it.status = PayrollEmployeeStatus.DRAFT
            timekeepingEmployeeRepository.save(it)

        }
        generateSummaries(payroll.timekeeping.timekeepingEmployees, payroll)

    }

    @Override
    TimekeepingEmployee addEmployee(PayrollEmployee payrollEmployee, Payroll payrollModule) {
        return null
    }

    @Override
    void removeEmployee(PayrollEmployee payrollEmployee, Payroll payrollModule) {

    }


    @Override
    void removeEmployees(List<PayrollEmployee> payrollEmployees, Payroll payrollModule) {

    }

    @Override
    TimekeepingEmployee recalculateEmployee(PayrollEmployee payrollEmployee, Payroll payroll) {
        payrollEmployee.timekeepingEmployee.accumulatedLogSummaryList.clear()
        List<TimekeepingEmployee> timekeepingEmployees = [payrollEmployee.timekeepingEmployee]
        generateSummaries(timekeepingEmployees, payroll)
        return null
    }


//============================================================UTILITY METHODS====================================================================

    List<AccumulatedLogSummary> generateSummaries(List<TimekeepingEmployee> timekeepingEmployees, Payroll payroll) {
    //TODO: Logic inside "each" loop can be moved to get timeKeepingCalculatorService.getAccumulatedLogs(). Return type to be decided later.

        List<AccumulatedLogSummary> summaryList  = []
        timekeepingEmployees.each {TimekeepingEmployee timekeepingEmployee ->
          summaryList = timeKeepingCalculatorService.getAccumulatedLogs(timekeepingEmployee.payrollEmployee.employee.id, payroll.dateStart, payroll.dateEnd)
            List<AccumulatedLog> logs = []
            summaryList.each { AccumulatedLogSummary summary ->
                summary.timekeepingEmployee = timekeepingEmployee
                summary.accumulatedLogs.each { AccumulatedLog log ->
                    log.summary = summary
                    logs.add(log)
                }
            }
            accumulatedLogSummaryRepository.saveAll(summaryList)
            accumulatedLogRepository.saveAll(logs)
        }
        return summaryList
    }

}






