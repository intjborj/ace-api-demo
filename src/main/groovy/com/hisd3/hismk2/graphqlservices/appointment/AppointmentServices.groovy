package com.hisd3.hismk2.graphqlservices.appointment

import com.hisd3.hismk2.domain.appointment.Appointment
import com.hisd3.hismk2.domain.appointment.AppointmentSchedule
import com.hisd3.hismk2.domain.appointment.AppointmentScheduleTime
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetValAppointment
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
class AppointmentServices extends AbstractDaoService<Appointment> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	AppointmentScheduleTimeServices appointmentScheduleTimeServices

    AppointmentServices() {
		super(Appointment.class)
	}
	
	@GraphQLQuery(name = "appointmentByID")
	Appointment appointmentByID(
			@GraphQLArgument(name = "id") UUID id
	) {
		if(id){
			findOne(id)
		}else{
			return null;
		}

	}
	
	@GraphQLQuery(name = "appointmentAll")
	List<Appointment> appointmentAll() {
		findAll().sort { it.appNo }
	}

	@GraphQLQuery(name = "appointmentByStatus")
	List<Appointment> appointmentByStatus(@GraphQLArgument(name = "status") String status) {
		createQuery("Select s from Appointment s where s.status = :status",
				[status: status]).resultList.sort { it.appNo }
	}

	@GraphQLQuery(name = "appointmentByPatient")
	List<Appointment> appointmentByPatient(@GraphQLArgument(name = "id") UUID id) {
		createQuery("Select a from Appointment a where a.patient.id = :id",
				[id: id]).resultList.sort { it.appNo }
	}

	@GraphQLQuery(name = "checkAppByPatientSchedule")
	Long checkAppByPatientSchedule(@GraphQLArgument(name = "id") UUID id, @GraphQLArgument(name = "patient") UUID patient) {
		def count = createCountQuery("Select count(s) from Appointment s where s.schedule.id = '${id}' and s.patient.id = '${patient}'").resultList
		return count[0]
	}

	@GraphQLQuery(name = "appointmentCheck")
	GraphQLRetValAppointment<Boolean> scheduleCheck(@GraphQLArgument(name = "id") UUID id, @GraphQLArgument(name = "type") String type) {
		def result = new GraphQLRetValAppointment<Boolean>(true,true,"Please select date of appointment and time. Price may vary for STAT order. Please be mindful on selecting STAT order.", "Appointment Information")
		if(id){
			def schedTime = appointmentScheduleTimeServices.findOne(id);

			def check = createCountQuery("Select count(s) from Appointment s where s.scheduleTime.id = '${id}' and s.orderStatus = 'NORMAL'").resultList
			def checkStat = createCountQuery("Select count(s) from Appointment s where s.scheduleTime.id = '${id}' and s.orderStatus = 'STAT'").resultList
			def availableSlot = schedTime.maxPerson - check[0];
			def availableSlotStat = schedTime.maxStat - checkStat[0];

			if(type.equalsIgnoreCase("NORMAL")){
				if(availableSlot == 0){
					result = new GraphQLRetValAppointment<Boolean>(false,false,"Slot is full. Please select another time.", "Selected appointment time not available")
				}else{
					result = new GraphQLRetValAppointment<Boolean>(true,true,"There are ${availableSlot} slot available.", "Selected appointment time is available")
				}
			}else {
				if(availableSlotStat == 0){
					result = new GraphQLRetValAppointment<Boolean>(false,false,"Slot is full for STAT order. Please select another time.", "Selected appointment time not available for STAT Order")
				}else{
					result = new GraphQLRetValAppointment<Boolean>(true,true,"There are ${availableSlotStat} slot available for STAT order.", "Selected appointment time is available for STAT Order")
				}
			}


		}

		result
	}

	@GraphQLQuery(name = "appointmentPage")
	Page<Appointment> appointmentPage(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "schedule") UUID schedule,
			@GraphQLArgument(name = "schedtime") UUID schedtime,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select s from Appointment s where
						(lower(s.appNo) like lower(concat('%',:filter,'%')) or 
						lower(s.patient.fullName) like lower(concat('%',:filter,'%')))'''

		String countQuery = '''Select count(s) from Appointment s where
							(lower(s.appNo) like lower(concat('%',:filter,'%')) or 
						lower(s.patient.fullName) like lower(concat('%',:filter,'%')))'''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)

		if (schedule) {
			query += ''' and (s.schedule.id = :schedule) '''
			countQuery += ''' and (s.schedule.id = :schedule) '''
			params.put("schedule", schedule)
		}

		if (schedtime) {
			query += ''' and (s.scheduleTime.id = :schedtime) '''
			countQuery += ''' and (s.scheduleTime.id = :schedtime) '''
			params.put("schedtime", schedtime)
		}

		query += ''' ORDER BY s.appNo'''

		getPageable(query, countQuery, page, size, params)
	}

	@GraphQLQuery(name = "appointmentPageByPatient")
	Page<Appointment> appointmentPageByPatient(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select s from Appointment s where
						(lower(s.appNo) like lower(concat('%',:filter,'%')) or 
						lower(s.patient.fullName) like lower(concat('%',:filter,'%'))) and
						s.patient.id = :id '''

		String countQuery = '''Select count(s) from Appointment s where
							(lower(s.appNo) like lower(concat('%',:filter,'%')) or 
						lower(s.patient.fullName) like lower(concat('%',:filter,'%'))) and
						s.patient.id = :id '''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)
		params.put('id', id)

		query += ''' ORDER BY s.appNo'''

		getPageable(query, countQuery, page, size, params)
	}
	
	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertAppointment")
	Appointment upsertAppointment(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		upsertFromMap(id, fields, { Appointment entity, boolean forInsert ->
			if(forInsert){
				entity.appNo = generatorService.getNextValue(GeneratorType.APP_CODE, {
					return "APT-" + StringUtils.leftPad(it.toString(), 6, "0")
				})

				if(entity.outcomeCondition){
					if(entity.outcomeCondition.equalsIgnoreCase("RECOVERED")){
						entity.dor = entity.dor.plusDays(1);
						entity.dod = null;
						entity.immediateCause = null;
						entity.antecedentCause = null;
						entity.underlyingCause = null;
						entity.contributoryConditions = null;
					}
					if(entity.outcomeCondition.equalsIgnoreCase("DEAD")){
						entity.dod.plusDays(1);
						entity.dor = null;
					}
				}

			}else{
				if(entity.outcomeCondition){
					if(entity.outcomeCondition.equalsIgnoreCase("RECOVERED")){
						entity.dor = entity.dor.plusDays(1);
						entity.dod = null;
						entity.immediateCause = null;
						entity.antecedentCause = null;
						entity.underlyingCause = null;
						entity.contributoryConditions = null;
					}
					if(entity.outcomeCondition.equalsIgnoreCase("DEAD")){
						entity.dod.plusDays(1);
						entity.dor = null;
					}
				}
			}
		})
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "appUpdateStatus")
	Appointment appUpdateStatus(
			@GraphQLArgument(name = "id") UUID id
	) {
		def up = findOne(id)
		up.status = true;
		save(up)
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "appUpdateStatusAdmin")
	Appointment appUpdateStatusAdmin(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "status") Boolean status
	) {
		def up = findOne(id)
		up.status = status
		save(up)
	}
	
}
