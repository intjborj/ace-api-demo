package com.hisd3.hismk2.graphqlservices.hrm

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.EmployeeRequest
import com.hisd3.hismk2.domain.hrm.EmployeeRequestApproval
import com.hisd3.hismk2.domain.hrm.enums.EmployeeRequestApprovalStatus
import com.hisd3.hismk2.domain.hrm.enums.EmployeeRequestStatus
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hrm.EmployeeRequestApprovalRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRequestRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.time.Instant

@TypeChecked
@Component
@GraphQLApi
class EmployeeRequestApprovalService {

    @Autowired
    EmployeeRequestApprovalRepository employeeRequestApprovalRepository

    @Autowired
    EmployeeRequestRepository employeeRequestRepository

    @Autowired
    ObjectMapper objectMapper

    @PersistenceContext
    EntityManager entityManager

    @GraphQLMutation
    GraphQLRetVal<EmployeeRequestApproval> upsertEmployeeRequestApproval(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "request") UUID request
    ) {
        if (!request) return new GraphQLRetVal<EmployeeRequestApproval>(null, false, "No employee request found.")

        EmployeeRequest employeeRequest = null
        EmployeeRequestApproval approval = null

        employeeRequestRepository.findById(request).ifPresent { EmployeeRequest foundRequest -> employeeRequest = foundRequest }
        employeeRequestApprovalRepository.findById(id).ifPresent { EmployeeRequestApproval foundApproval -> approval = foundApproval }

        if (!employeeRequest || !approval)
            return new GraphQLRetVal<EmployeeRequestApproval>(null, false, "Something went wrong: Found no employee request or approval request.", id)

        if(employeeRequest.status != EmployeeRequestStatus.PENDING_SUPERVISOR)
            return new GraphQLRetVal<EmployeeRequestApproval>(null, false, "Something went wrong: Can't approve rejected leave request.")
        if (id) {
            approval = objectMapper.updateValue(approval, fields)
            if(approval.status == EmployeeRequestApprovalStatus.APPROVED || approval.status == EmployeeRequestApprovalStatus.REJECTED)
                approval.approvedDate = Instant.now()

            employeeRequestApprovalRepository.save(approval)
            if (approval.status == EmployeeRequestApprovalStatus.APPROVED) {
                List<EmployeeRequestApproval> approvals = entityManager.createQuery("""
                            Select a from EmployeeRequestApproval a
                            left join fetch a.request r
                            where r.id = :request and a.status in :status
                        """, EmployeeRequestApproval.class)
                        .setParameter("request", request)
                        .setParameter("status", [EmployeeRequestApprovalStatus.PENDING, EmployeeRequestApprovalStatus.REJECTED])
                        .resultList
                if (approvals.empty) {
                    // set employee request to PENDING since all approval status is APPROVED
                    employeeRequest.status = EmployeeRequestStatus.PENDING
                    employeeRequest.approvedDate = Instant.now()
                    employeeRequestRepository.save(employeeRequest)
                    return new GraphQLRetVal<EmployeeRequestApproval>(approval, true, "Successfully updated, Employee request status has changed to Pending.")
                } else return new GraphQLRetVal<EmployeeRequestApproval>(approval, true, "Successfully updated request approval.")
            }else if(approval.status == EmployeeRequestApprovalStatus.REJECTED){
                employeeRequest.status = EmployeeRequestStatus.REJECTED_SUPERVISOR
                employeeRequestRepository.save(employeeRequest)
                return new GraphQLRetVal<EmployeeRequestApproval>(approval, true, "Successfully updated, Employee request status has changed to Rejected Supervisor.")
            }
            return new GraphQLRetVal<EmployeeRequestApproval>(approval, true, "Successfully updated request approval.")
        } else {
            approval = objectMapper.convertValue(fields, EmployeeRequestApproval)
            approval.request = employeeRequest
            employeeRequestApprovalRepository.save(approval)
            return new GraphQLRetVal<EmployeeRequestApproval>(approval, true, "Successfully updated request approval.")
        }
    }

}
