package com.hisd3.hismk2.graphqlservices.dietary

import com.hisd3.hismk2.domain.dietary.DietLog
import com.hisd3.hismk2.repository.dietary.DietLogRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class DietLogService {
	@Autowired
	DietLogRepository dietLogRepository
	
	@GraphQLQuery(name = "get_all_diet_logs")
	Page<DietLog> getAllDietLogs(@GraphQLArgument(name = "caseId") UUID caseId, @GraphQLArgument(name = "filter") String filter, @GraphQLArgument(name = "page") Integer page, @GraphQLArgument(name = "pageSize") Integer pageSize) {
		return dietLogRepository.findByCaseId(caseId, PageRequest.of(page, pageSize))
	}
}
