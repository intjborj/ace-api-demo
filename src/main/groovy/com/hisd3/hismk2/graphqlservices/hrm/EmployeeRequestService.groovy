package com.hisd3.hismk2.graphqlservices.hrm

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.EmployeeRequest
import com.hisd3.hismk2.domain.hrm.EmployeeRequestApproval
import com.hisd3.hismk2.domain.hrm.EmployeeSchedule
import com.hisd3.hismk2.domain.hrm.enums.*
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRequestApprovalRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRequestRepository
import com.hisd3.hismk2.repository.hrm.EmployeeScheduleRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.hibernate.annotations.QueryHints
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.time.Instant

@TypeChecked
@Component
@GraphQLApi
class EmployeeRequestService {

    final String LEAVE_LABEL = "Leave"
    final String LEAVE_COLOR = "#2ecc71"
    final String LEAVE_TITLE = "Leave Schedule"

    final String REST_LABEL = "R"
    final String REST_COLOR = "#95a5a6"
    final String REST_TITLE = "Rest Day"


    @Autowired
    EmployeeRequestRepository employeeRequestRepository

    @Autowired
    EmployeeScheduleRepository employeeScheduleRepository

    @Autowired
    DepartmentRepository departmentRepository

    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    EmployeeRequestApprovalRepository employeeRequestApprovalRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    ObjectMapper objectMapper

    @PersistenceContext
    EntityManager entityManager


    //=================================Query=================================\\
    @GraphQLQuery(name = "leaveRequest", description = "Get one employee leave request")
    EmployeeRequest leaveRequest(@GraphQLArgument(name = "id") UUID id) {
        try {
            def request = entityManager.createQuery(""" 
                Select DISTINCT r from EmployeeRequest r
                    left join fetch r.requestedBy rb
                    left join fetch r.hrApprovedBy hab
                    left join fetch r.department d
                    left join fetch r.approvals ap
                    left join fetch ap.employee e
                where r.id = :id""", EmployeeRequest.class)
                    .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                    .setParameter("id", id)
                    .singleResult
            return request
        } catch (Exception e) {
            return null
        }

    }

//    @GraphQLQuery
//    Page<EmployeeRequest> findEmployeeRequests(
//            @GraphQLArgument(name = "type") String type,
//            @GraphQLArgument(name = "status") List<String> status,
//            @GraphQLArgument(name = "page") Integer page,
//            @GraphQLArgument(name = "size") Integer size
//    ) {
//        List<EmployeeRequestStatus> newStatus = []
//        newStatus = status.stream().map({ EmployeeRequestStatus.valueOf(it) }).collect() as List<EmployeeRequestStatus>
//        return employeeRequestRepository.findEmployeeRequests(EmployeeRequestType.valueOf(type), newStatus, new PageRequest(page, size, Sort.Direction.DESC, "createdDate"))
//    }

    @GraphQLQuery
    Page<EmployeeRequest> findEmployeeRequestByRequestedEmployee(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "type") String type,
            @GraphQLArgument(name = "status") List<String> status,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ) {
        List<EmployeeRequestStatus> newStatus = []
        newStatus = status.stream().map({ EmployeeRequestStatus.valueOf(it) }).collect() as List<EmployeeRequestStatus>
        return employeeRequestRepository.findEmployeeRequestsByRequestedEmployee(id, newStatus, EmployeeRequestType.valueOf(type), new PageRequest(page, size, Sort.Direction.DESC, "createdDate"))
    }

    @GraphQLQuery
    Page<EmployeeRequest> findEmployeeRequestByHrApprovedEmployee(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "status") List<String> status,
            @GraphQLArgument(name = "type") String type,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ) {
        List<EmployeeRequestStatus> newStatus = []
        newStatus = status.stream().map({ EmployeeRequestStatus.valueOf(it) }).collect() as List<EmployeeRequestStatus>
        return employeeRequestRepository.findEmployeeRequestsByHrApprovedEmployee(id, newStatus, EmployeeRequestType.valueOf(type), new PageRequest(page, size, Sort.Direction.DESC, "createdDate"))
    }

    @GraphQLQuery
    Page<EmployeeRequest> findEmployeeRequests(
            @GraphQLArgument(name = "status") List<String> status = [],
            @GraphQLArgument(name = "startDate") Instant startDate,
            @GraphQLArgument(name = "endDate") Instant endDate,
            @GraphQLArgument(name = "withPay") List<Boolean> withPay = [],
            @GraphQLArgument(name = "datesType") List<String> datesType = [],
            @GraphQLArgument(name = "approvals") List<UUID> approvals,
            @GraphQLArgument(name = "requestedBy") UUID requestedBy,
            @GraphQLArgument(name = "hrApprovedBy") UUID hrApprovedBy,
            @GraphQLArgument(name = "revertedBy") UUID revertedBy,
            @GraphQLArgument(name = "department") UUID department,
            @GraphQLArgument(name = "type") String type,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size
    ) {
        List<EmployeeRequestStatus> newStatus = status.stream().map({ EmployeeRequestStatus.valueOf(it) }).collect() as List<EmployeeRequestStatus>
        List<EmployeeRequestDateType> newDatesType = datesType.stream().map({ EmployeeRequestDateType.valueOf(it) }).collect() as List<EmployeeRequestDateType>

        // get all approvals and join fetch request and employee
//        entityManager.createQuery("""
//            Select ap from EmployeeRequestApproval ap
//            left join fetch ap.request r
//            left join fetch ap.employee e
//        """, EmployeeRequestApproval.class).resultList
        List<UUID> approvalIds = []
        if (approvals.size() > 0) {
            approvalIds = entityManager.createQuery("""
                select distinct ap.id from EmployeeRequestApproval ap
                left join ap.employee e
                where e.id in :approvals
            """, UUID.class).setParameter("approvals", approvals)
                    .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                    .resultList
            if (approvalIds.size() == 0)
                return new PageImpl<EmployeeRequest>([], PageRequest.of(page, size), 0)
        }

        def query = """ Select DISTINCT r from EmployeeRequest r
                    left join fetch r.requestedBy rb
                    left join fetch r.hrApprovedBy hab
                    left join fetch r.revertedBy reb
                    left join fetch r.department d
                    left join fetch r.approvals ap
                where r.type = :type
            """
        def countQueryString = """ Select count(DISTINCT r.id) from EmployeeRequest r
                    left join  r.requestedBy rb
                    left join  r.hrApprovedBy hab
                    left join  r.revertedBy reb
                    left join  r.department d
                    left join  r.approvals ap
                where r.type = :type
            """

        // startDate
        // endDate
        LinkedHashMap<String, Object> params = ["type": EmployeeRequestType.valueOf(type)] as LinkedHashMap<String, Object>
        if (status.size() > 0) {
            query += " AND r.status IN :status"
            countQueryString += " AND r.status IN :status"
            params.put("status", newStatus)
        }
        if (withPay.size() > 0) {
            query += " AND ("
            countQueryString += " AND ("
            withPay.eachWithIndex { Boolean it, int i ->
                query += " r.withPay IS ${it ? "TRUE" : "FALSE"}"
                countQueryString += " r.withPay IS ${it ? "TRUE" : "FALSE"}"
                if (i != withPay.size() - 1) {
                    query += " or"
                    countQueryString += " or"
                }
            }
            query += ")"
            countQueryString += ")"
        }
        if (datesType.size() > 0) {
            params.put("datesType", newDatesType)
            query += " AND r.datesType IN :datesType"
            countQueryString += " AND r.datesType IN :datesType"
        }
        if (hrApprovedBy) {
            params.put("hrApprovedBy", hrApprovedBy)
            query += " AND hab.id = :hrApprovedBy"
            countQueryString += " AND hab.id = :hrApprovedBy"
        }
        if (revertedBy) {
            params.put("revertedBy", revertedBy)
            query += " AND reb.id = :revertedBy"
            countQueryString += " AND reb.id = :revertedBy"
        }
        if (requestedBy) {
            params.put("requestedBy", requestedBy)
            query += " AND rb.id = :requestedBy"
            countQueryString += " AND rb.id = :requestedBy"
        }
        if (department) {
            params.put("department", department)
            query += " AND d.id = :department"
            countQueryString += " AND d.id = :department"
        }

        if (approvalIds.size() > 0) {
            params.put("approvalIds", approvalIds)
            query += " AND ap.id in :approvalIds"
            countQueryString += " AND ap.id in :approvalIds"
        }

        if (startDate && endDate) {
            params.put("startDate", startDate)
            params.put("endDate", endDate)
            query += " and r.requestedDate >= :startDate and r.requestedDate <= :endDate"
            countQueryString += " and r.requestedDate >= :startDate and r.requestedDate <= :endDate"
        } else if (startDate != null || endDate != null) {
            throw new Exception("Input not valid, startDate and endDate must both have value or both are null.")
        }
        query += " order by r.requestedDate"
        def requestsQuery = entityManager.createQuery(query, EmployeeRequest.class).setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
        def countQuery = entityManager.createQuery(countQueryString, Long.class).setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
        params.each {
            requestsQuery.setParameter(it.key as String, it.value)
            countQuery.setParameter(it.key as String, it.value)
        }
        List<EmployeeRequest> requests = requestsQuery.resultList
        Long count = countQuery.singleResult

        return new PageImpl<EmployeeRequest>(requests, PageRequest.of(page, size), count)

    }


    //=================================Query=================================\\


    //===============================Mutation==================================\\

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation
    GraphQLRetVal<EmployeeRequest> upsertEmployeeRequest(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "department") UUID department,
            @GraphQLArgument(name = "requestedBy") UUID requestedBy,
            @GraphQLArgument(name = "approvals") List<UUID> approvals,
            @GraphQLArgument(name = "hrApprovedBy") UUID hrApprovedBy,
            @GraphQLArgument(name = "approvedByHrOverride") Boolean approvedByHrOverride


    ) {
        try {
            if (!department || !requestedBy) return new GraphQLRetVal<EmployeeRequest>(null, false, "Failed to update employee request.")
            Department foundDepartment = null
            Employee foundRequestedBy = null
            Employee foundHrApprovedBy = null
            def getDepartment = departmentRepository.findById(department)
            def getRequestedBy = employeeRepository.findById(requestedBy)

            if (getDepartment.present) foundDepartment = getDepartment.get()
            else return new GraphQLRetVal<EmployeeRequest>(null, false, "Failed to update employee request.")

            if (getRequestedBy.present) foundRequestedBy = getRequestedBy.get()
            else return new GraphQLRetVal<EmployeeRequest>(null, false, "Failed to update employee request.")

            if (hrApprovedBy) {
                def getHrApprovedBy = employeeRepository.findById(hrApprovedBy)
                if (getHrApprovedBy.present) foundHrApprovedBy = getHrApprovedBy.get()
                else return new GraphQLRetVal<EmployeeRequest>(null, false, "Failed to update employee request.")
            }

            if (id) {
                def foundEmployeeRequest = employeeRequestRepository.findById(id)
                if (foundEmployeeRequest.present) {

                    EmployeeRequest request = foundEmployeeRequest.get()
                    request = objectMapper.updateValue(request, fields)
                    if (!request.reason) throw new Exception("Failed to update employee request: no reason")
                    else if (!request.status) throw new Exception("Failed to update employee request: no status")
                    else if (!request.type) throw new Exception("Failed to update employee request: no type")

                    if (!request.remarks && (request.status == EmployeeRequestStatus.APPROVED || request.status == EmployeeRequestStatus.REJECTED))
                        throw new Exception("Failed to update employee request: no remarks")

                    request.department = foundDepartment
                    request.requestedBy = foundRequestedBy

                    if (foundHrApprovedBy) {
                        request.hrApprovedBy = foundHrApprovedBy
                        request.hrApprovedDate = Instant.now()
                    }

                    if ((request.status == EmployeeRequestStatus.PENDING_SUPERVISOR || request.status == EmployeeRequestStatus.PENDING) && !request.requestedDate)
                        request.requestedDate = Instant.now()


                    def addApprovals = []
                    if (approvals.size() > 0) {
                        approvals.each {
                            employeeRepository.findById(it).ifPresent {
                                EmployeeRequestApproval approval = new EmployeeRequestApproval()
                                approval.status = EmployeeRequestApprovalStatus.PENDING
//                        approval.remarks = "This is automatically approved. This remarks is generated by the system."
//                        approval.status = EmployeeRequestApprovalStatus.APPROVED
                                approval.employee = it
                                approval.request = request
                                if (approval.status == EmployeeRequestApprovalStatus.APPROVED)
                                    approval.approvedDate = Instant.now()
                                addApprovals.push(approval)
                            }
                        }
                    }
                    request.approvals.clear()
                    request.approvals.addAll(addApprovals)

                    employeeRequestRepository.save(request)

                    return new GraphQLRetVal<EmployeeRequest>(request, true, "Successfully updated employee request.", id)
                } else {
                    return new GraphQLRetVal<EmployeeRequest>(null, false, "No Employee Request Found", id)
                }

            } else {
                EmployeeRequest request = objectMapper.convertValue(fields, EmployeeRequest)

                if (!request.reason) throw new Exception("Failed to update employee request: no reason")
                else if (!request.status) throw new Exception("Failed to update employee request: no status")
                else if (!request.type) throw new Exception("Failed to update employee request: no type")

                if (!request.remarks && request.status == EmployeeRequestStatus.APPROVED || request.status == EmployeeRequestStatus.REJECTED)
                    throw new Exception("Failed to update employee request: no remarks")

                request.department = foundDepartment
                request.requestedBy = foundRequestedBy

                if ((request.status == EmployeeRequestStatus.PENDING_SUPERVISOR || request.status == EmployeeRequestStatus.PENDING) && !request.requestedDate)
                    request.requestedDate = Instant.now()

                employeeRequestRepository.save(request)

                def addApprovals = []
                approvals.each {
                    employeeRepository.findById(it).ifPresent {
                        EmployeeRequestApproval approval = new EmployeeRequestApproval()
                        approval.status = EmployeeRequestApprovalStatus.PENDING
                        if (approvedByHrOverride) {
                            approval.remarks = "This is automatically approved. This remarks is generated by the system."
                            approval.status = EmployeeRequestApprovalStatus.APPROVED
                        }
                        approval.employee = it
                        approval.request = request
                        if (approval.status == EmployeeRequestApprovalStatus.APPROVED)
                            approval.approvedDate = Instant.now()
                        addApprovals.push(approval)
                    }
                }
                employeeRequestApprovalRepository.saveAll(addApprovals)
                return new GraphQLRetVal<EmployeeRequest>(request, true, "Successfully updated employee request request.", id)
            }
        } catch (Exception e) {
            println(e.message)
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new GraphQLRetVal<EmployeeRequest>(null, false, "Failed to update employee request request.", id)
        }

    }

    @GraphQLMutation
    GraphQLRetVal<String> hrApprovalOrRejectEmployeeRequest(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "hrApprovedBy") UUID hrApprovedBy
    ) {
        if (!id || !hrApprovedBy) return new GraphQLRetVal<String>(null, false, "Failed to update Employee Request.")

        EmployeeRequest request = null
        Employee hrApproved = null

        employeeRepository.findById(hrApprovedBy).ifPresent { hrApproved = it }
        employeeRequestRepository.findById(id).ifPresent { request = it }

        if (!hrApproved || !request) return new GraphQLRetVal<String>(null, false, "Failed to update Employee Request")

        request = objectMapper.updateValue(request, fields)
        request.hrApprovedBy = hrApproved
        request.hrApprovedDate = Instant.now()

        List<EmployeeRequestApproval> foundApprovals = employeeRequestApprovalRepository.findEmployeeRequestApproval(id, [EmployeeRequestApprovalStatus.PENDING, EmployeeRequestApprovalStatus.REJECTED])

        if (request.status == EmployeeRequestStatus.APPROVED && foundApprovals.empty && request.hrApprovedBy) {
            List<EmployeeSchedule> toSaveSchedules = []
            request.dates.each {
                EmployeeSchedule employeeSchedule = new EmployeeSchedule()
                employeeSchedule.department = request.department
                employeeSchedule.employee = request.requestedBy
                employeeSchedule.dateTimeStartRaw = it.startDatetime
                employeeSchedule.dateTimeEndRaw = it.endDatetime

                if (
                        it.scheduleType != "REST"
                ) {
                    employeeSchedule.label = LEAVE_LABEL
                    employeeSchedule.color = LEAVE_COLOR
                    employeeSchedule.title = LEAVE_TITLE
                    employeeSchedule.isLeave = true
                } else if (it.scheduleType == "REST") {
                    employeeSchedule.label = REST_LABEL
                    employeeSchedule.color = REST_COLOR
                    employeeSchedule.title = REST_TITLE
                    employeeSchedule.isLeave = true
                    employeeSchedule.isRestDay = true
                } else throw new Exception("EmployeeRequestScheduleType is not supported: ${it.scheduleType}")

                employeeSchedule.withPay = request.withPay && it.scheduleType != "REST"

                employeeSchedule.isOvertime = false
                employeeSchedule.isOIC = false
                employeeSchedule.withHoliday = false
                employeeSchedule.withNSD = false
                employeeSchedule.request = request
                toSaveSchedules.push(employeeSchedule)
            }
            employeeScheduleRepository.saveAll(toSaveSchedules)
            employeeRequestRepository.save(request)
        } else if (request.status == EmployeeRequestStatus.APPROVED) {

            throw new Exception("Failed to update employee request to status: APPROVED, approvedBy & hrApprovedBy required")
        }
        employeeRequestRepository.save(request)


        employeeRequestRepository.save(request)

        return new GraphQLRetVal<String>(null, true, "Successfully updated Employee Request.")
    }

    @GraphQLMutation
    GraphQLRetVal<String> revertEmployeeRequest(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "remarks") String remarks
    ) {
        if (!id) return new GraphQLRetVal<String>(null, false, "Failed to revert employee request.")
        EmployeeRequest request = null
        employeeRequestRepository.findById(id).ifPresent { request = it }
        if (!request) return new GraphQLRetVal<String>(null, false, "Failed to revert employee request: No request found.")

        Employee employee = userRepository.findOneByLogin(SecurityContextHolder.getContext().getAuthentication().getName()).employee
        if (!employee) return new GraphQLRetVal<String>(null, false, "Failed to revert employee request.")

        request.schedules.clear()
        request.status = EmployeeRequestStatus.REVERTED
        request.revertedBy = employee
        request.revertedDate = Instant.now()
        request.remarks += """\n\n####REVERT REMARKS####\n""" + remarks

        employeeRequestRepository.save(request)

        return new GraphQLRetVal<String>(null, true, "Successfully reverted employee request.")
    }

    @GraphQLMutation
    GraphQLRetVal<String> submitEmployeeRequest(
            @GraphQLArgument(name = "id") UUID id
    ) {
        if (!id) return new GraphQLRetVal<String>(null, false, "Failed to submit employee request.")
        EmployeeRequest request = null
        employeeRequestRepository.findById(id).ifPresent { request = it }
        if (!request) return new GraphQLRetVal<String>(null, false, "Failed to submit employee request.")

        request.requestedDate = Instant.now()
        request.status = EmployeeRequestStatus.PENDING_SUPERVISOR

        employeeRequestRepository.save(request)

        return new GraphQLRetVal<String>(null, true, "Successfully submitted employee request.")

    }

    //===============================Mutation==================================\\

}
