package com.hisd3.hismk2.graphqlservices.doh

import com.hisd3.hismk2.domain.doh.TestProcedureType
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.doh.dto.DohTestingDetailsDTo
import com.hisd3.hismk2.graphqlservices.doh.dto.DohTestingDto
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service



@GraphQLApi
@Component
@Service
class TestingProcedureServices extends AbstractDaoService<TestProcedureType> {
    TestingProcedureServices(){
        super(TestProcedureType.class)
    }

    @Autowired
    JdbcTemplate jdbcTemplate


    @GraphQLQuery(name="testProcedurePageable")
    Page<TestProcedureType> testProcedurePageable(
            @GraphQLArgument(name="filter") String filter,
            @GraphQLArgument(name="page") Integer page,
            @GraphQLArgument(name="size") Integer size
    ){
        Page testPage = Page.empty()
        def result = getPageable("""
            Select t from TestProcedureType t 
                where
                    (
						lower(t.description) like lower(concat('%',:filter,'%'))
              		) order by t.description asc""",
            """
            Select count(t) from TestProcedureType t 
                where lower(t.description) like lower(concat('%',:filter,'%'))""",
            page,
            size,
            [
                   filter:filter
            ]
        )

        if(result)
            testPage = result

        return  testPage

    }

    @GraphQLQuery(name="dohTestingPerYear")
    DohTestingDto dohTestingPerYear(
            @GraphQLArgument(name="year") String year
    ){
        DohTestingDto dohTestingDto = new DohTestingDto()

        List<DohTestingDetailsDTo> result = jdbcTemplate.query('''
            select tpt.code  as "code", tpt.description, count(oi.id) as "number", tpt.group_code as "groupCode" 
            from doh.test_procedure_type tpt 
            left join ancillary.services s on s.test_procedure_type = tpt.code and s.service_type = 'SINGLE'
            left join ancillary.orderslip_item oi on oi.service = s.id and date_part('year'::text, oi.created_date) =  ?::DOUBLE PRECISION and oi.status != 'CANCELLED' and ((tpt.code = 19 and oi.posted is true) or (tpt.code != 19))
            group by tpt.group_code, tpt.code,tpt.description
            order by tpt.group_code asc,tpt.code asc
        ''',new BeanPropertyRowMapper(DohTestingDetailsDTo.class),
                year
        )

        if(result)
            result.each {
                it->
                if (it.groupCode == "1")
                    dohTestingDto.imaging.push(it)
                else
                    dohTestingDto.laboratoryAndDiagnostic.push(it)

            }

        dohTestingDto.imaging.sort{it.code}
        dohTestingDto.laboratoryAndDiagnostic.sort{it.code}
        return dohTestingDto
    }




}
