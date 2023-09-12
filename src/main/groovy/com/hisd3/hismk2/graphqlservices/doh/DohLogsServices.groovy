package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.Classification
import com.hisd3.hismk2.domain.doh.DohExpenses
import com.hisd3.hismk2.domain.doh.DohLogs
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.doh.dto.OpdMinorOpDto
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.ClassificationRepository
import com.hisd3.hismk2.repository.doh.DohLogsRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoClassification
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoClassificationResponse

import java.time.Instant

@Component
@GraphQLApi
class DohLogsServices extends AbstractDaoService<DohLogs> {

    DohLogsServices() {
        super(DohLogs.class)
    }

    @Autowired
    DohLogsRepository dohLogsRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    JdbcTemplate jdbcTemplate

    @GraphQLQuery(name = "findAllLogs", description = "Find all doh logs")
    List<DohLogs> findAllLogs() {
        return dohLogsRepository.findAll()
    }

    @GraphQLQuery(name = "getLatestExpensesData", description = "Find the latest submitted expenses report")
    DohLogs getLatestExpensesData(
            @GraphQLArgument(name = "year") Integer year
    ) {
        def result = jdbcTemplate.query('''
				select * from doh.doh_logs l where l.type = 'EXPENSES' and reporting_year = ''' + year + '''ORDER by   l.created_date DESC limit 1
			''', new BeanPropertyRowMapper(DohLogs.class))


        return result[0] as DohLogs
    }


    @GraphQLQuery(name = "getLatestRevenuesData", description = "Find the latest submitted revenues report")
    DohLogs getLatestRevenuesData(@GraphQLArgument(name = "year") Integer year) {
        def result = jdbcTemplate.query('''
				select * from doh.doh_logs l where l.type = 'REVENUES' and reporting_year = ''' + year + '''ORDER by   l.created_date DESC limit 1
			''', new BeanPropertyRowMapper(DohLogs.class))

        return result[0] as DohLogs

    }
    //==================================Mutation ============

    @GraphQLMutation(name = "saveDohLogs")
    GraphQLRetVal<String> e(@GraphQLArgument(name = "id") UUID id,
                            @GraphQLArgument(name = "fields") Map<String, Object> fields) {
        if (id) {
            //blablablablabla
        } else {
            def logs = objectMapper.convertValue(fields, DohLogs)
            return dohLogsRepository.save(logs)
        }
    }
}
