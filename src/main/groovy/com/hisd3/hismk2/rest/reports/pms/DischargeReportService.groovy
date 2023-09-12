package com.hisd3.hismk2.rest.reports.pms

import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.domain.hrm.dto.EmployeeDto
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Patient
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.PatientRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.time.Instant

@Component
@GraphQLApi
class DischargeReportService {

    @Autowired
    JdbcTemplate jdbcTemplate

    @Autowired
    CaseRepository caseRepository

    @PersistenceContext
    EntityManager entityManager

    @GraphQLQuery(name = 'discharge_report')
    GraphQLRetVal<Page<Case>> dischargeReport(
            @GraphQLArgument(name = 'start') Instant start,
            @GraphQLArgument(name = 'end') Instant end,
            @GraphQLArgument(name = 'page') int page,
            @GraphQLArgument(name = 'size') int size,
            @GraphQLArgument(name = 'search') String search,
            @GraphQLArgument(name = 'sort') String sort,
            @GraphQLArgument(name = 'sortBy') String sortBy,
            @GraphQLArgument(name = 'filterCode') String filterCode,
            @GraphQLArgument(name = 'filterStatus') String filterStatus
    ) {

        String queryString = '''
			select c from Case c
            where lower(concat(c.patient.lastName , coalesce(', ' || nullif(c.patient.firstName,'') , ''), coalesce(' ' || nullif(c.patient.middleName,'') , ''), coalesce(' ' || nullif(c.patient.nameSuffix,'') , ''))) like lower(concat('%',:search,'%'))
            AND c.registryType IN ('IPD', 'ERD')
            '''
        String countString = '''
			select count(c) from Case c
            where lower(concat(c.patient.lastName , coalesce(', ' || nullif(c.patient.firstName,'') , ''), coalesce(' ' || nullif(c.patient.middleName,'') , ''), coalesce(' ' || nullif(c.patient.nameSuffix,'') , ''))) like lower(concat('%',:search,'%'))
            AND c.registryType IN ('IPD', 'ERD') 
		'''

        if(filterStatus == 'ALL'){
            queryString += ''' AND (c.mayGoHomeDatetime IS NOT NULL AND status = 'ACTIVE') OR (c.dischargedDatetime IS NOT NULL AND status = 'DISCHARGED') '''
            countString += ''' AND (c.mayGoHomeDatetime IS NOT NULL AND status = 'ACTIVE') OR (c.dischargedDatetime IS NOT NULL AND status = 'DISCHARGED') '''
        }

        if(filterStatus == 'FOR DISCHARGE'){
            queryString += ''' AND (c.mayGoHomeDatetime IS NOT NULL AND status = 'ACTIVE') '''
            countString += ''' AND (c.mayGoHomeDatetime IS NOT NULL AND status = 'ACTIVE') '''
        }

        if(filterStatus == 'DISCHARGED'){
            queryString += ''' AND (c.dischargedDatetime IS NOT NULL AND status = 'DISCHARGED') '''
            countString += ''' AND (c.dischargedDatetime IS NOT NULL AND status = 'DISCHARGED') '''
        }

        queryString += ''' AND c.dischargedDatetime between :start and :end '''
        countString += ''' AND c.dischargedDatetime between :start and :end '''


        queryString += "ORDER BY ${sortBy} ${sort}"

        def query = entityManager.createQuery(queryString)
        def countQuery = entityManager.createQuery(countString, Long.class)

        Map<String, Object> params = new HashMap<>()
        params.put('search', search)
        params.put('start', start)
        params.put('end', end)

        params.each {
            query.setParameter(it.key, it.value)
            countQuery.setParameter(it.key, it.value)
        }
        List<Case> patients = query
                .setFirstResult(page * size)
                .setMaxResults(size)
                .resultList


        Long count = countQuery.singleResult
        Page<Case> patientPage = new PageImpl<Case>(patients, PageRequest.of(page, size),
                count)
        return new GraphQLRetVal<Page<Case>>(patientPage, true, "Found redundant price tier")

    }
}
