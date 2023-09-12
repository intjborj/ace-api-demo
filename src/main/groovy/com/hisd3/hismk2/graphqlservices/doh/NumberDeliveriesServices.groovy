package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.hisd3.hismk2.domain.doh.DOH_REPORT_TYPE
import com.hisd3.hismk2.domain.doh.DischargeNumberDeliveries
import com.hisd3.hismk2.domain.doh.DohLogs
import com.hisd3.hismk2.domain.doh.OperationDeaths
import com.hisd3.hismk2.graphqlservices.doh.dto.DeliveriesDto
import com.hisd3.hismk2.graphqlservices.doh.dto.HospOptDischargesNumberDeliveriesDto
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.DischargeNumberDeliveriesRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.xmlsoap.schemas.soap.encoding.Int
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesMorbidityResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesNumberDeliveries
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesNumberDeliveriesResponse

import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId

@Component
@GraphQLApi
class NumberDeliveriesServices {

	@Autowired
	DischargeNumberDeliveriesRepository dischargeNumberDeliveriesRepository

	@Autowired
	DohReportService dohReportService

	@Autowired
	DohAPIService dohAPIService

	@Autowired
	DohLogsServices dohLogsServices

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	CaseRepository caseRepository


	@Autowired
	SOAPConnector soapConnector

	@Autowired
	HospitalConfigService hospitalConfigService

	@GraphQLQuery(name = "findAllDischargeNumberDeliveries", description = "Find all classification")
	List<DischargeNumberDeliveries> findAllDischargeNumberDeliveries() {
		return dischargeNumberDeliveriesRepository.findAllDischargeNumberDeliveries()
	}

	@GraphQLQuery(name = "getAllDeliveryCounts")
	GraphQLRetVal<DischargeNumberDeliveries> getAllDeliveryCounts(@GraphQLArgument(name='year') Integer year){
		DischargeNumberDeliveries dto = new DischargeNumberDeliveries()

		LocalDate start =  LocalDate.of(year, Month.JANUARY, 1)
		LocalDate end =  LocalDate.of(year, Month.DECEMBER, 31)

		Instant instantStart = start.atStartOfDay(ZoneId.systemDefault()).toInstant()
		Instant instantEnd = end.atStartOfDay(ZoneId.systemDefault()).toInstant()



		dto.reportingYear = year
		dto.totalBvDelivery = caseRepository.countDischargeDeliviries('NORMAL', instantStart, instantEnd)
		dto.totalLbcDelivery = caseRepository.countDischargeDeliviries('CESAREAN', instantStart, instantEnd)
		dto.totalIfDelivery = dto.totalLbcDelivery + dto.totalBvDelivery
		dto.totalOtherDelivery = 0

		return new GraphQLRetVal<DischargeNumberDeliveries>(dto, true)
	}

	//==================================Mutation ============
	@GraphQLMutation
	def postNumberDeliveries(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def numberDeliveries = dischargeNumberDeliveriesRepository.findById(id).get()
			objectMapper.updateValue(numberDeliveries, fields)
			numberDeliveries.submittedDateTime = Instant.now()

			return dischargeNumberDeliveriesRepository.save(numberDeliveries)
		} else {

			def numberDeliveries = objectMapper.convertValue(fields, DischargeNumberDeliveries)

			return dischargeNumberDeliveriesRepository.save(numberDeliveries)
		}
	}

	@GraphQLMutation
	GraphQLRetVal<HospOptDischargesNumberDeliveriesDto> saveDeliveriesLogs(
		@GraphQLArgument(name = "year") Integer year
	) {


		DeliveriesDto deliveriesDto = dohReportService.getCountDeliveries(year)
		GraphQLRetVal<HospOptDischargesNumberDeliveriesDto> result = dohAPIService.hospOptDischargesNumberDeliveries(deliveriesDto.infacility,deliveriesDto.vaginal, deliveriesDto.caesarian,deliveriesDto.other,year.toString())
		Map<String,Object> fields = [:]
		fields['infacility'] = deliveriesDto.infacility
		fields['vaginal'] = deliveriesDto.vaginal
		fields['caesarian'] = deliveriesDto.caesarian
		fields['other'] = deliveriesDto.other
		fields['year'] = year.toString()

		if(result){
			DohLogs dohLogs = new DohLogs()
			dohLogs.type = DOH_REPORT_TYPE.DELIVERIES.name()
			dohLogs.submittedReport = new JSONObject(fields)
			dohLogs.reportResponse = new Gson().toJson(result.payload)
			dohLogs.reportingYear = year
			dohLogs.status = result.payload.response_desc
			dohLogsServices.save(dohLogs)
		}
		return result
	}

	@GraphQLMutation(name = 'postNumberDeliverisDoh')
	GraphQLRetVal<String> postNumberDeliverisDoh(@GraphQLArgument(name = 'fields') Map<String, Object> fields){
		try {
			HospOptDischargesNumberDeliveries request = new HospOptDischargesNumberDeliveries()

			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.totalifdelivery = fields.get('totalIfDelivery') as Integer
			request.totallbvdelivery = fields.get('totalBvDelivery') as Integer
			request.totallbcdelivery = fields.get('totalLbcDelivery') as Integer
			request.totalotherdelivery = fields.get('totalOtherDelivery') as Integer
			request.reportingyear = fields.get('reportingYear') as Integer

			HospOptDischargesNumberDeliveriesResponse response =
					(HospOptDischargesNumberDeliveriesResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesNumberDeliveries", request)

			UUID id = UUID.fromString(fields.get('id') as String)
			DischargeNumberDeliveries dto = dischargeNumberDeliveriesRepository.getOne(id)
			dto.dohResponse = response.return

			dischargeNumberDeliveriesRepository.save(dto)

			return new GraphQLRetVal<String>(response.return, true)
		} catch (Exception e) {
			return new GraphQLRetVal<String>(e.message, false)

		}
	}
 }
