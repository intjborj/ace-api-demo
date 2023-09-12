package com.hisd3.hismk2.graphqlservices.payroll

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.dto.EmployeeAccumulatedAttendanceDto
import com.hisd3.hismk2.domain.payroll.AccumulatedLog
import com.hisd3.hismk2.domain.payroll.AccumulatedLogSummary
import com.hisd3.hismk2.domain.payroll.FinalAccumulatedLogs
import com.hisd3.hismk2.domain.payroll.OriginalAccumulatedLogs
import com.hisd3.hismk2.domain.payroll.Timekeeping
import com.hisd3.hismk2.domain.payroll.TimekeepingEmployee
import com.hisd3.hismk2.domain.payroll.Totals
import com.hisd3.hismk2.domain.payroll.enums.AccumulatedLogStatus
import com.hisd3.hismk2.domain.payroll.enums.TimekeepingStatus
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.payroll.AccumulatedLogRepository
import com.hisd3.hismk2.repository.payroll.AccumulatedLogSummaryRepository
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

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.time.Instant

@TypeChecked
@Component
@GraphQLApi
class AccumulatedLogService {

    @Autowired
    PayrollTimeKeepingCalculatorService payrollTimeKeepingCalculatorService

    @Autowired
    AccumulatedLogRepository accumulatedLogRepository

    @Autowired
    AccumulatedLogSummaryRepository accumulatedLogSummaryRepository

    @Autowired
    TimeKeepingCalculatorService timeKeepingCalculatorService

    @Autowired
    ObjectMapper objectMapper

    @PersistenceContext
    EntityManager entityManager

    //=================================QUERY=================================\\
//    @GraphQLQuery(name = "getTimekeepingEmployeeAccumulatedLogsById", description = "Get timekeeping by ID")
//    List<AccumulatedLog> getTimekeepingEmployeeAccumulatedLogsByEmpId(@GraphQLArgument(name = "id") UUID id) {
//        if(id){
//             accumulatedLogRepository.findByTimekeepingEmployee(id).sort{it.date}
//        }else{
//            return null
//        }
//    }

    @GraphQLQuery(name = "getTimekeepingEmployeeAccumulatedLogsById", description = "Get timekeeping by ID")
    List<AccumulatedLogSummary> getTimekeepingEmployeeAccumulatedLogsByEmpId(@GraphQLArgument(name = "id") UUID id) {
        if (id) {
            accumulatedLogSummaryRepository.findByTimekeepingEmployee(id).sort { it.totals.date }
        } else {
            return null
        }
    }

    @GraphQLQuery(name = "getAccumulatedLogById", description = "Get accumulated Log by ID")
    AccumulatedLog getAccumulatedLogById(@GraphQLArgument(name = "id") UUID id) {
        if (id) {

            accumulatedLogRepository.findById(id).get()
        } else {
            return null
        }

    }
    //=================================MUTATIONS=================================\\
    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation
    GraphQLRetVal<String> updateHours(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "logs") Map<String, Object> logs
    ) {
        AccumulatedLog accumulatedLog = accumulatedLogRepository.findById(id).get()
        def newLogs = objectMapper.convertValue(logs, FinalAccumulatedLogs)

        EmployeeAccumulatedAttendanceDto totals = new EmployeeAccumulatedAttendanceDto()

        accumulatedLog.summary.accumulatedLogs.each {
            if (it.id == id) {
                it.finalLogs = newLogs
            }
            totals = timeKeepingCalculatorService.addAccumulatedLogs(totals, objectMapper.convertValue(it.finalLogs, EmployeeAccumulatedAttendanceDto))
        }
        Totals newTotals = new Totals()
        newTotals = objectMapper.convertValue(totals, Totals)
        newTotals.date = accumulatedLog.summary.totals.date
        newTotals.inTime = accumulatedLog.summary.totals.inTime
        newTotals.outTime = accumulatedLog.summary.totals.outTime
        newTotals.message = accumulatedLog.summary.totals.message
        newTotals.isError = accumulatedLog.summary.totals.isError
        newTotals.isRestDay = accumulatedLog.summary.totals.isRestDay
        newTotals.withNSD = accumulatedLog.summary.totals.withNSD
        newTotals.isLeave = accumulatedLog.summary.totals.isLeave
        accumulatedLog.summary.totals = newTotals
        accumulatedLogRepository.save(accumulatedLog)
        return new GraphQLRetVal<String>("OK", true, "Successfully updated updated logs")


    }


    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation
    GraphQLRetVal<String> recalculateOneSummary(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "empId") UUID empId,
            @GraphQLArgument(name = "startDate") Instant startDate,
            @GraphQLArgument(name = "endDate") Instant endDate
    ) {

        AccumulatedLogSummary accumulatedLogSummary = accumulatedLogSummaryRepository.findById(id).get()
        accumulatedLogRepository.deleteAll(accumulatedLogSummary.accumulatedLogs)

        List<AccumulatedLogSummary> summaryList = timeKeepingCalculatorService.getAccumulatedLogs(empId, startDate, endDate)

        accumulatedLogSummary.totals = summaryList[0].totals
        summaryList[0].accumulatedLogs.each {
            it.summary = accumulatedLogSummary
        }

        accumulatedLogSummaryRepository.save(accumulatedLogSummary)
        accumulatedLogRepository.saveAll(summaryList[0].accumulatedLogs)
        return new GraphQLRetVal<String>("OK", true, "Successfully recalculated log")
    }



}
