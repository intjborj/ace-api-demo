package com.hisd3.hismk2.rest.reports.pms

import com.google.common.graph.Graph
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import graphql.GraphQL
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class InPatientReportService {

    @Autowired
    JdbcTemplate jdbcTemplate

    @GraphQLQuery(name = 'getPatientReport')
    GraphQLRetVal<Page<Map<String, Object>>> getPatientReport(
            @GraphQLArgument(name = 'start') String start,
            @GraphQLArgument(name = 'end') String end,
            @GraphQLArgument(name = 'page') int page,
            @GraphQLArgument(name = 'size') int size
    ) {


        String query = """
                    select *,CONCAT  (p."Lastname", ', ', p."Middlename",' ', p."Firstname") AS "fullName"  
                    from pms.inpatient_report_pamela p
                    where to_date("Admission Date", 'MM/DD/YYYY') 
                    between to_date('${start}', 'MM/DD/YYYY') + time '00:00' 
                    and to_date('${end}', 'MM/DD/YYYY') + time '23:59'
                    order by "Lastname" asc
					"""


        List<Map<String, Object>> qResult = jdbcTemplate.queryForList(query)
        List<Map<String, Object>> list = []

        Integer startItem = page * size

        if (qResult.size() < startItem) {
            list = Collections.emptyList()
        } else {
            int toIndex = Math.min(startItem + size, qResult.size())
            list = qResult.subList(startItem, toIndex)
        }

        Page<Map<String, Object>> pages = new PageImpl<>(list, PageRequest.of(page, size, Sort.Direction.ASC, 'Lastname'), qResult.size())


        return new GraphQLRetVal<Page<Map<String, Object>>>(pages, true)
    }
}
