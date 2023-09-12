package com.hisd3.hismk2.graphqlservices.hrm

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Permission
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.JobTitle
import com.hisd3.hismk2.domain.payroll.Timekeeping
import com.hisd3.hismk2.domain.payroll.TimekeepingEmployee
import com.hisd3.hismk2.domain.payroll.enums.TimekeepingEmployeeStatus
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hrm.JobTitleRepository
import com.hisd3.hismk2.repository.payroll.TimekeepingEmployeeRepository
import com.hisd3.hismk2.repository.payroll.TimekeepingRepository
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

@TypeChecked
@Component
@GraphQLApi
class JobTitleService {


    @Autowired
    JobTitleRepository jobTitleRepository

    @Autowired
    ObjectMapper objectMapper

    @PersistenceContext
    EntityManager entityManager

    //=================================QUERY=================================\\

    @GraphQLQuery(name = "jobTitleList", description = "Get all job titles")
    List<JobTitle> findAll() {
        return jobTitleRepository.findAll().sort { it.createdDate }
    }

    @GraphQLQuery(name = "jobTitleById", description = "Get job title by id")
    JobTitle findById(@GraphQLArgument(name = "id") UUID id) {
        return jobTitleRepository.findById(id).get()
    }

    @GraphQLQuery(name = "jobTitleActive", description = "Get active job titles")
    List<JobTitle> jobTitleActive() {
        return jobTitleRepository.findByActive()
    }


    //=================================QUERY=================================\\


    //================================MUTATION================================\\

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation
    GraphQLRetVal<String> upsertJobTitle(
            @GraphQLArgument(name = "value") String value,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {

        if (value) {
            JobTitle jobTitle = entityManager.createQuery("""
                Select j from JobTitle j  where j.value = :value
            """, JobTitle.class).setParameter("value", value)
                    .singleResult
            jobTitle = objectMapper.updateValue(jobTitle, fields)
            jobTitleRepository.save(jobTitle)
            return new GraphQLRetVal<String>("OK", true, "Successfully updated  Job Title")
        } else {
            JobTitle jobTitle = objectMapper.convertValue(fields, JobTitle)
            def val = jobTitle.value
            String res = jobTitleRepository.findByValue(val)
            if (res) {
                return new GraphQLRetVal<String>("OK", false, "Job title's value already exists")

            } else {
                jobTitleRepository.save(jobTitle)
                return new GraphQLRetVal<String>("OK", true, "Successfully created new Job Title")
            }

        }


        //================================MUTATION================================\\

    }
}