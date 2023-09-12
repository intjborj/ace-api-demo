package com.hisd3.hismk2.graphqlservices.ancillary

import com.hisd3.hismk2.dao.doh.DohPatientVisitsDao
import com.hisd3.hismk2.rest.dto.LeadingErConsult
import com.hisd3.hismk2.rest.dto.VisitsDTO
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class DohPatientVisitsService {
	
	@Autowired
	DohPatientVisitsDao dohPatientVisitsDao
	
	@GraphQLQuery(name = "getAllVisits", description = "Search Visits")
	
	VisitsDTO getAllVisits(@GraphQLArgument(name = "start") String start, @GraphQLArgument(name = "end") String end) {
		dohPatientVisitsDao.getVisits(start, end)
	}
	
	@GraphQLQuery(name = "getLeadingERConsult", description = "Search Leading ER Consult")
	
	List<LeadingErConsult> getLeadingERConsult(@GraphQLArgument(name = "filter") String filter, @GraphQLArgument(name = "start") String start, @GraphQLArgument(name = "end") String end) {
		dohPatientVisitsDao.LeadingErConsult(filter, start, end)
	}
}
