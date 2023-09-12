package com.hisd3.hismk2.graphqlservices.appointment

import com.hisd3.hismk2.domain.appointment.AppointmentSchedule
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.Duration
import java.time.Instant

@Service
@GraphQLApi
class AppointmentScheduleServices extends AbstractDaoService<AppointmentSchedule> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	AppointmentScheduleTimeServices appointmentScheduleTimeServices

    AppointmentScheduleServices() {
		super(AppointmentSchedule.class)
	}
	
	@GraphQLQuery(name = "scheduleById")
	AppointmentSchedule scheduleById(
			@GraphQLArgument(name = "id") UUID id
	) {
		if(id){
			findOne(id)
		}else{
			return null;
		}

	}
	
	@GraphQLQuery(name = "scheduleAllList")
	List<AppointmentSchedule> scheduleAllList() {
		findAll().sort { it.scheduleDate }
	}

	@GraphQLQuery(name = "getScheduleDateListAll")
	List<AppointmentSchedule> getScheduleDateListAll(@GraphQLArgument(name = "filter") String filter) {
		createQuery('''Select s from AppointmentSchedule s where (lower(s.formattedScheduleDate) like lower(concat('%',:filter,'%'))) and s.status = true ''',
				[filter: filter]).resultList.sort { it.scheduleDate }
	}

	@GraphQLQuery(name = "getScheduleDateList")
	List<AppointmentSchedule> getScheduleDateList(@GraphQLArgument(name = "dateNow") String dateNow,
												  @GraphQLArgument(name = "filter") String filter) {
		createQuery('''Select s from AppointmentSchedule s where to_date(to_char(s.scheduleDate, 'YYYY-MM-DD'),'YYYY-MM-DD') >= to_date(:dateNow,'YYYY-MM-DD')
		and (lower(s.formattedScheduleDate) like lower(concat('%',:filter,'%'))) and s.status = true ''',
				[dateNow: dateNow, filter: filter]).resultList.sort { it.scheduleDate }
	}


	@GraphQLQuery(name = "scheduleByStatus")
	List<AppointmentSchedule> scheduleByStatus(@GraphQLArgument(name = "status") Boolean status) {
		createQuery("Select s from AppointmentSchedule s where s.status = :status",
				[status: status]).resultList.sort { it.scheduleDate }
	}

	@GraphQLQuery(name = "schedulePage")
	Page<AppointmentSchedule> schedulePage(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select s from AppointmentSchedule s where
						lower(s.scheduleCode) like lower(concat('%',:filter,'%'))'''

		String countQuery = '''Select count(s) from AppointmentSchedule s where
							lower(s.scheduleCode) like lower(concat('%',:filter,'%'))'''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)

		query += ''' ORDER BY s.scheduleCode'''

		getPageable(query, countQuery, page, size, params)
	}
	
	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertSchedule")
	AppointmentSchedule upsertSchedule(
			@GraphQLArgument(name = "startDate") String startDate,
			@GraphQLArgument(name = "interval") Integer interval
	) {
		def start = Instant.parse(startDate)
		def counter = 1;
		def sched = new AppointmentSchedule()

		while(counter <= (interval + 1)){
			sched = new AppointmentSchedule()
			sched.scheduleCode = generatorService.getNextValue(GeneratorType.SCHED_CODE, {
				return "SCHED-" + StringUtils.leftPad(it.toString(), 6, "0")
			})
			sched.scheduleDate = start.plus(Duration.ofDays(counter))
			sched.status = true
			def afterSave = save(sched)
			appointmentScheduleTimeServices.upsertScheduleTime(afterSave);

			//println("date to be inserted => "+ start.plus(Duration.ofDays(counter)))
			counter++;
		}
		return sched
	}
	
}
