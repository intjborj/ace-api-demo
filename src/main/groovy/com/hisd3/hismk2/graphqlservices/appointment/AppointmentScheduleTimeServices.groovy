package com.hisd3.hismk2.graphqlservices.appointment

import com.hisd3.hismk2.domain.appointment.AppointmentSchedule
import com.hisd3.hismk2.domain.appointment.AppointmentScheduleTime
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.services.GeneratorService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.Duration

@Service
@GraphQLApi
class AppointmentScheduleTimeServices extends AbstractDaoService<AppointmentScheduleTime> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	AppointmentConfigServices appointmentConfigServices

	@Autowired
	AppointmentScheduleServices appointmentScheduleServices


    AppointmentScheduleTimeServices() {
		super(AppointmentScheduleTime.class)
	}
	
	@GraphQLQuery(name = "scheduleTimeById")
	AppointmentScheduleTime scheduleTimeById(
			@GraphQLArgument(name = "id") UUID id
	) {
		if(id){
			findOne(id)
		}else{
			return null;
		}

	}
	
	@GraphQLQuery(name = "scheduleTimeAllList")
	List<AppointmentScheduleTime> scheduleTimeAllList() {
		findAll().sort { it.createdDate }
	}

	@GraphQLQuery(name = "timeBySchedule")
	List<AppointmentScheduleTime> timeBySchedule(@GraphQLArgument(name = "id") UUID id) {
		createQuery("Select s from AppointmentScheduleTime s where s.schedule.id = :id",
				[id: id]).resultList.sort { it.schedTime.formattedTime }
	}

	@GraphQLQuery(name = "timeByScheduleActive")
	List<AppointmentScheduleTime> timeByScheduleActive(@GraphQLArgument(name = "id") UUID id) {
		if(id){
			def sched = appointmentScheduleServices.findOne(id)
			def selectedDate = sched.scheduleDate.atZone(ZoneOffset.UTC).toLocalDate();
			//time
			def instant = Instant.now();
			LocalTime time = instant.atZone(ZoneOffset.UTC).toLocalTime();
			def timeNow = time.plusHours(8).toString();
			def dateNow = Instant.now().plus(Duration.ofHours(8)).atZone(ZoneOffset.UTC).toLocalDate();
			//
			String query = '''Select s from AppointmentScheduleTime s where s.schedule.id = :id and s.status = true ''';
			def params = [id: id]
			if(dateNow == selectedDate){
				query = '''Select s from AppointmentScheduleTime s where s.schedule.id = :id and 
		to_char(s.schedTime.timeStart + make_interval(0, 0, 0, 0, 8), 'HH24:MI') > :timeNow and s.status = true '''
				params = [id: id, timeNow: timeNow]
			}
			createQuery(query,
					params).resultList.sort { it.schedTime.formattedTime }
		}else{
			return [];
		}

	}


	@GraphQLQuery(name = "timeByScheduleFilter")
	List<AppointmentScheduleTime> timeByScheduleFilter(@GraphQLArgument(name = "id") UUID id) {
		if(id){
			String query = '''Select s from AppointmentScheduleTime s where s.schedule.id = :id and s.status = true ''';
			def params = [id: id]
			createQuery(query,
					params).resultList.sort { it.schedTime.formattedTime }
		}else{
			return [];
		}

	}

	
	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertScheduleTime")
	AppointmentScheduleTime upsertScheduleTime(
			@GraphQLArgument(name = "parent") AppointmentSchedule parent
	) {
		def configTime = appointmentConfigServices.configTimeActive(true)
		def time = new AppointmentScheduleTime()
		configTime.each {
			time = new AppointmentScheduleTime()
			time.schedule = parent
			time.schedTime = it
			time.maxPerson = it.defaultMaxPerson
			time.allowedStat = it.allowedStat
			time.maxStat = it.defaultMaxStat
			time.status = true
			save(time)
		}
		time
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertSchedConfig")
	AppointmentScheduleTime upsertSchedConfig(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		upsertFromMap(id, fields, { AppointmentScheduleTime entity, boolean forInsert ->

		})
	}
	
}
