package com.hisd3.hismk2.graphqlservices.pms

import com.hisd3.hismk2.domain.pms.FlowRate
import com.hisd3.hismk2.repository.pms.FlowRateRepository
import com.hisd3.hismk2.rest.dto.DashboardCensusDto
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@TypeChecked
@Component
@GraphQLApi
class PatientDashboardCensusService {



	@Autowired
	JdbcTemplate jdbcTemplate

	@GraphQLQuery(name = "patientDashboardCensus", description = "Get Patient Dashboard Census")
	DashboardCensusDto patientDashboardCensus() {

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.systemDefault())

		String testDate = "20200923"

		LocalDate dateNow = LocalDate.parse(testDate,formatter)
		LocalDate dateTommorow = LocalDate.parse(testDate,formatter).plusDays(1)


		DashboardCensusDto dash = new DashboardCensusDto()
		LocalDate.now().format(formatter)


		String queryNewAdmission = """
						SELECT count(*)
						   FROM ((pms.cases cs
							 LEFT JOIN pms.patients pt ON ((pt.id = cs.patient)))
							 LEFT JOIN hrm.employees emp ON ((emp.id = cs.attending_physician)))
						  WHERE ((cs.registry_type)::text = 'IPD'::text) and (cs.admission_datetime + '08:00:00'::interval) BETWEEN TO_DATE('${dateNow.format(formatter)}','YYYYMMDD') and TO_DATE('${dateTommorow.format(formatter)}','YYYYMMDD')
						  """

		String queryNewbornAdmission = """select count(*) from pms.cases cs 
										  left join pms.patients pt on pt."id" = cs.patient
											where pt.civil_status = 'newborn' or (
											date_part('year'::text, age(((cs.admission_datetime)::date)::timestamp with time zone, (pt.dob)::timestamp with time zone)) = 0
											and 
											date_part('month'::text, age(((cs.admission_datetime)::date)::timestamp with time zone, (pt.dob)::timestamp with time zone)) = 0
											and 
											date_part('month'::text, age(((cs.admission_datetime)::date)::timestamp with time zone, (pt.dob)::timestamp with time zone)) < 2
											and (cs.admission_datetime + '08:00:00'::interval) 
											BETWEEN TO_DATE('${dateNow.format(formatter)}','YYYYMMDD') and TO_DATE('${dateTommorow.format(formatter)}','YYYYMMDD')
											)
										"""
		String queryOldAdmissions = """
									select count(*) from pms.cases cs 
									left join pms.patients pt on pt."id" = cs.patient
									where  (cs.admission_datetime + '08:00:00'::interval) 
									< TO_DATE('${dateNow.format(formatter)}','YYYYMMDD')
									and ((cs.discharged_datetime + '08:00:00'::interval) BETWEEN TO_DATE('${dateNow.format(formatter)}','YYYYMMDD') and TO_DATE('${dateTommorow.format(formatter)}','YYYYMMDD') or (cs.discharged_datetime is null and cs.status='ACTIVE'))
									and cs.registry_type = 'IPD'
									;
									"""

		dash.new_admission = jdbcTemplate.queryForObject(queryNewAdmission, Integer) as Integer
		dash.newborn = jdbcTemplate.queryForObject(queryNewbornAdmission, Integer) as Integer
		dash.old_admissions = jdbcTemplate.queryForObject(queryOldAdmissions, Integer) as Integer

		return dash
	}
}
