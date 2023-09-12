package com.hisd3.hismk2.graphqlservices.appointment

import com.hisd3.hismk2.domain.appointment.AppointmentSummary
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.services.GeneratorService
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.time.LocalDate

@Service
@GraphQLApi
class AppointmentSummaryServices extends AbstractDaoService<AppointmentSummary> {

	@Autowired
	GeneratorService generatorService


    AppointmentSummaryServices() {
		super(AppointmentSummary.class)
	}

	@GraphQLQuery(name = "agtDashboard")
	List<AppointmentSummary> agtDashboard() {
		findAll().sort{ it.start_}
	}

	@GraphQLQuery(name = "agtDashboardSummaryPerCurrentDate")
	List<AppointmentSummary> agtDashboardSummaryPerCurrentDate() {
		def now = LocalDate.now().toString()
		createQuery('''Select a from AppointmentSummary a where 
to_date(a.start_,'YYYY-MM-DD') = to_date(:now, 'YYY-MM-DD')''',
				[now: now]).resultList.sort { it.start_ }

	}


	
}
