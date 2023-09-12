package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.VitalSign
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.VitalSignRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.sql.Timestamp
import java.time.Instant

@TypeChecked
@Component
@GraphQLApi
class VitalSignService {
	
	@Autowired
	private VitalSignRepository vitalSignRepository

	@Autowired
	private EmployeeRepository employeeRepository

	@Autowired
	private CaseRepository caseRepository
	
	@Autowired
	ObjectMapper objectMapper
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "vitalSigns", description = "Get all VitalSigns")
	List<VitalSign> findAll() {
		return vitalSignRepository.findAll().sort { it.entryDateTime }
	}
	
	@GraphQLQuery(name = "vitalSign", description = "Get VitalSign By Id")
	VitalSign findById(@GraphQLArgument(name = "id") UUID id) {
		return vitalSignRepository.findById(id).get()
	}
	
	@GraphQLQuery(name = "vitalSignsByCase", description = "Get all VitalSigns by Case Id")
	List<VitalSign> getVitalSignsByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
        List<VitalSign> vts = vitalSignRepository.getVitalSignsByCase(caseId)
        vts.each{
            it->
                it.cbc = isZeroOrEmpty(it.cbc)
                it.cbs = isZeroOrEmpty(it.cbs)
                it.cgs = isZeroOrEmpty(it.cgs)
                it.crt = isZeroOrEmpty(it.crt)
                it.diastolic = isZeroOrEmpty(it.diastolic)
                it.systolic = isZeroOrEmpty(it.systolic)
                it.fetalHr = isZeroOrEmpty(it.fetalHr)
                it.oxygenSaturation = isZeroOrEmpty(it.oxygenSaturation)
                it.pulseRate = isZeroOrEmpty(it.pulseRate)
                it.temperature = isZeroOrEmpty(it.temperature)
                it.respiratoryRate = isZeroOrEmpty(it.respiratoryRate)
        }
        return vts
	}
	
	@GraphQLQuery(name = "vitalSignsForChart", description = "Get all VitalSigns by Case Id")
	List<VitalSign> getvitalSignsForChart(@GraphQLArgument(name = "caseId") UUID caseId) {
        List<VitalSign> vts = vitalSignRepository.getVitalSignsByCase(caseId)
        vts.each{
            it->
                it.cbc = isZeroOrEmpty(it.cbc)
                it.cbs = isZeroOrEmpty(it.cbs)
                it.cgs = isZeroOrEmpty(it.cgs)
                it.crt = isZeroOrEmpty(it.crt)
                it.diastolic = isZeroOrEmpty(it.diastolic)
                it.systolic = isZeroOrEmpty(it.systolic)
                it.fetalHr = isZeroOrEmpty(it.fetalHr)
                it.oxygenSaturation = isZeroOrEmpty(it.oxygenSaturation)
                it.pulseRate = isZeroOrEmpty(it.pulseRate)
                it.temperature = isZeroOrEmpty(it.temperature)
                it.respiratoryRate = isZeroOrEmpty(it.respiratoryRate)
        }
		return vts
	}
	
	@GraphQLQuery(name = "latestVitalSign", description = "Get latest VitalSign")
	VitalSign getLatest(@GraphQLArgument(name = "caseId") UUID caseId) {
		return vitalSignRepository.getLatest(caseId)
	}

	@GraphQLMutation
	def addInitialVitals(
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		def parentCase = caseRepository.findById(UUID.fromString(fields.get("parentCase").toString())).get()

		def vs = vitalSignRepository.isInitialExists(parentCase.id)

		if(!vs) {
			vs = new VitalSign()
			vs.entryDateTime = Instant.now()
			vs.note = "initial"
		}

			String bp = fields.get("initialBp")
			def bpSplit = bp.split("/")

			if(bpSplit) {
				vs.systolic = bpSplit[0]
				vs.diastolic = bpSplit[1]
			}

			vs.temperature = fields.get("initialTemperature")
			vs.pulseRate = fields.get("initialPulse")
			vs.respiratoryRate = fields.get("initialResp")
			vs.oxygenSaturation = fields.get("initialO2sat")

			vs.employee = employeeRepository.findById(UUID.fromString(fields.get("employee").toString())).get()
			vs.parentCase = caseRepository.findById(UUID.fromString(fields.get("parentCase").toString())).get()

			vitalSignRepository.save(vs)
		return true;
	}

	@GraphQLMutation
	VitalSign addVitalSignsForFlutter(
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		return vitalSignRepository.save(
				new VitalSign().tap {
					entryDateTime = Timestamp.valueOf(fields.get("entryDateTime") as String).toInstant()
					systolic = fields.get("systolic")
					diastolic = fields.get("diastolic")
					temperature = fields.get("temperature")
					pulseRate = fields.get("pulseRate")
					respiratoryRate = fields.get("respiratoryRate")
					oxygenSaturation = fields.get("oxygenSaturation")
					painScore = fields.get("painScore")
					fetalHr = fields.get("fetalHr")
					weight = fields.get("weight")
					cbs = fields.get("cbs")
					employee = objectMapper.convertValue(fields.get("employee"), Employee)
					parentCase = objectMapper.convertValue(fields.get("parentCase"), Case)
				}
		)
	}

    String isZeroOrEmpty(String str)
    {
       if(str == null || str.isEmpty() || str.trim().equals("0") || str.trim().equals("0.0"))
           return null
        else
           return str.trim().replace(' ','')
    }
}
