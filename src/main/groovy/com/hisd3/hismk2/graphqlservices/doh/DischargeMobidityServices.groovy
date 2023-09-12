package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.hisd3.hismk2.domain.doh.DOH_REPORT_TYPE
import com.hisd3.hismk2.domain.doh.DischargeMobidity
import com.hisd3.hismk2.domain.doh.DohLogs
import com.hisd3.hismk2.graphqlservices.doh.dto.HospOptDischargesMorbidityDOH
import com.hisd3.hismk2.graphqlservices.doh.dto.HospOptDischargesMorbidityDTO
import com.hisd3.hismk2.graphqlservices.doh.dto.HospOptMortalityDeathsDTO
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.DischargeMobidityRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesMorbidity
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesMorbidityResponse

import java.time.Instant

@Component
@GraphQLApi
class DischargeMobidityServices {

	@Autowired
	DischargeMobidityRepository dischargeMobidityRepository

	@Autowired
	DohReportService dohReportService

	@Autowired
	DohAPIService dohAPIService

	@Autowired
	DohLogsServices dohLogsServices

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	HospitalConfigService hospitalConfigService

	@Autowired
	JdbcTemplate jdbcTemplate

	@GraphQLQuery(name = "findAllDischargeMobidity", description = "Find all Discharge Mobidity")
	List<DischargeMobidity> findAllDischargeMobidity() {
		return dischargeMobidityRepository.findAllDischargeMobidity()
	}

	//==================================Mutation ============
	@GraphQLMutation
	def postDischargeMobidity(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def dischargeMobidity = dischargeMobidityRepository.findById(id).get()
			objectMapper.updateValue(dischargeMobidity, fields)
			dischargeMobidity.submittedDateTime = Instant.now()

			return dischargeMobidityRepository.save(dischargeMobidity)
		} else {

			def dischargeMobidity = objectMapper.convertValue(fields, DischargeMobidity)

			return dischargeMobidityRepository.save(dischargeMobidity)
		}
	}
	@GraphQLMutation(name = "submitDischargeMorbidity")
	GraphQLRetVal<List<HospOptDischargesMorbidityDTO>> submitDischargeMorbidity(@GraphQLArgument(name = "year") Integer year) {

		List<HospOptDischargesMorbidityDTO> hospOptDischargesMorbidityDTOArrayList = []
		List<Map<String, Object>> dischargeMorbidityList = getDischargeMobidity(year)

		dischargeMorbidityList.each {
			HospOptDischargesMorbidityDTO response = dohAPIService.hospOptDischargesMorbidity(
					hospitalConfigService.hospitalInfo.hfhudcode ?: '',
					it['icd10desc'].toString().replaceAll("'",""),
					it['munder1'].toString(),
					it['funder1'].toString(),
					it['m1to4'].toString(),
					it['f1to4'].toString(),
					it['m5to9'].toString(),
					it['f5to9'].toString(),
					it['m10to14'].toString(),
					it['f10to14'].toString(),
					it['m15to19'].toString(),
					it['f15to19'].toString(),
					it['m20to24'].toString(),
					it['f20to24'].toString(),
					it['m25to29'].toString(),
					it['f25to29'].toString(),
					it['m30to34'].toString(),
					it['f30to34'].toString(),
					it['m35to39'].toString(),
					it['f35to39'].toString(),
					it['m40to44'].toString(),
					it['f40to44'].toString(),
					it['m45to49'].toString(),
					it['f45to49'].toString(),
					it['m50to54'].toString(),
					it['f50to54'].toString(),
					it['m55to59'].toString(),
					it['f55to59'].toString(),
					it['m60to64'].toString(),
					it['f60to64'].toString(),
					it['m65to69'].toString(),
					it['f65to69'].toString(),
					it['m70over'].toString(),
					it['f70over'].toString(),
					it['msubtotal'].toString(),
					it['fsubtotal'].toString(),
					it['grandtotal'].toString(),
					it['icd10code'].toString(),
					it['icd10category'].toString(),
//					'2010'
					year.toString()
			)
//		dischargeMorbidityList.each {
//			HospOptDischargesMorbidityDTO response = dohAPIService.hospOptDischargesMorbidity(
//					hospitalConfigService.hospitalInfo.hfhudcode ?: '',
//					it['icd10desc'].toString(),
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'1',
//					'A06.6',
//					'A00-A09',
//					year.toString()
//			)


			hospOptDischargesMorbidityDTOArrayList.push(response)
			if (dischargeMorbidityList) {
				DohLogs dohLogs = new DohLogs()
				dohLogs.type = DOH_REPORT_TYPE.DISCHARGES_MORBIDITY.name()
				dohLogs.submittedReport = new JSONObject(it)
				dohLogs.reportResponse = new Gson().toJson(response)
				dohLogs.reportingYear = year
				dohLogs.status = response.response_desc
				dohLogsServices.save(dohLogs)
			}
		}

		return new GraphQLRetVal<List<HospOptDischargesMorbidityDTO>>(hospOptDischargesMorbidityDTOArrayList,true, '')


	}

	@GraphQLQuery(name="getDischargeMorbidityByAgeAndGender")
	List<Map<String,Object>> getDischargeMorbidityByAgeAndGender(@GraphQLArgument(name="year") Integer year){
		return jdbcTemplate.queryForList("""
		WITH patient_age AS (
		 	SELECT p_1.id,
			p_1.gender,
			date_part('year'::text, age(p_1.dob::timestamp with time zone))::integer AS page
		   	FROM pms.patients p_1
		)
		SELECT
		ROW_NUMBER() OVER(
				ORDER BY count(*) desc
		) as no,
		p.page as "age",
		p.gender,
		d.value ->> 'icdCode'::text AS icdcode,
		d.value ->> 'icdDesc'::text AS longname,
		date_part('year'::text, c.entry_datetime) AS reporting_year
		FROM pms.cases c
		WHERE
		c.patient = p.id
		and
		c.registry_type = 'IPD'
		and
		c.discharged_datetime is not null
		and
		date_part('year'::text, c.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION
		GROUP by p.page,p.gender, (d.value ->> 'icdCode'::text), (d.value ->> 'icdDesc'::text), (date_part('year'::text, c.entry_datetime))
		ORDER BY (count(*)) desc
		limit 10
		""",
		year
		) as List<Map<String, Object>>
	}

	@GraphQLQuery(name="getDischargeMobidity")
	List<Map<String,Object>> getDischargeMobidity(@GraphQLArgument(name="year") Integer year){
		return jdbcTemplate.queryForList("""
		WITH patient_age AS (
        SELECT p_1.id,
        p_1.gender,
        date_part('year'::text, age(p_1.dob::timestamp with time zone))::integer AS page
        FROM pms.patients p_1
        )
select 
ROW_NUMBER() OVER(
ORDER BY count(*) desc
) as no,
d.value ->> 'icdCode'::text AS icd10code,
d.value ->> 'icdDesc'::text AS icd10desc,
dic.icd10_cat as icd10category,
date_part('year'::text, c.entry_datetime) AS reporting_year,
count(*) FILTER (WHERE p.page < 1 AND p.gender::text = 'MALE'::text) AS munder1,
count(*) FILTER (WHERE p.page < 1 AND p.gender::text = 'FEMALE'::text) AS funder1,
count(*) FILTER (WHERE p.page >= 1 AND p.page <= 4 AND p.gender::text = 'MALE'::text) AS m1to4,
count(*) FILTER (WHERE p.page >= 1 AND p.page <= 4 AND p.gender::text = 'FEMALE'::text) AS f1to4,
count(*) FILTER (WHERE p.page >= 5 AND p.page <= 9 AND p.gender::text = 'MALE'::text) AS m5to9,
count(*) FILTER (WHERE p.page >= 5 AND p.page <= 9 AND p.gender::text = 'FEMALE'::text) AS f5to9,
count(*) FILTER (WHERE p.page >= 10 AND p.page <= 14 AND p.gender::text = 'MALE'::text) AS m10to14,
count(*) FILTER (WHERE p.page >= 10 AND p.page <= 14 AND p.gender::text = 'FEMALE'::text) AS f10to14,
count(*) FILTER (WHERE p.page >= 15 AND p.page <= 19 AND p.gender::text = 'MALE'::text) AS m15to19,
count(*) FILTER (WHERE p.page >= 15 AND p.page <= 19 AND p.gender::text = 'FEMALE'::text) AS f15to19,
count(*) FILTER (WHERE p.page >= 20 AND p.page <= 24 AND p.gender::text = 'MALE'::text) AS m20to24,
count(*) FILTER (WHERE p.page >= 20 AND p.page <= 24 AND p.gender::text = 'FEMALE'::text) AS f20to24,
count(*) FILTER (WHERE p.page >= 25 AND p.page <= 29 AND p.gender::text = 'MALE'::text) AS m25to29,
count(*) FILTER (WHERE p.page >= 25 AND p.page <= 29 AND p.gender::text = 'FEMALE'::text) AS f25to29,
count(*) FILTER (WHERE p.page >= 30 AND p.page <= 34 AND p.gender::text = 'MALE'::text) AS m30to34,
count(*) FILTER (WHERE p.page >= 30 AND p.page <= 34 AND p.gender::text = 'FEMALE'::text) AS f30to34,
count(*) FILTER (WHERE p.page >= 35 AND p.page <= 39 AND p.gender::text = 'MALE'::text) AS m35to39,
count(*) FILTER (WHERE p.page >= 35 AND p.page <= 39 AND p.gender::text = 'FEMALE'::text) AS f35to39,
count(*) FILTER (WHERE p.page >= 40 AND p.page <= 44 AND p.gender::text = 'MALE'::text) AS m40to44,
count(*) FILTER (WHERE p.page >= 40 AND p.page <= 44 AND p.gender::text = 'FEMALE'::text) AS f40to44,
count(*) FILTER (WHERE p.page >= 45 AND p.page <= 49 AND p.gender::text = 'MALE'::text) AS m45to49,
count(*) FILTER (WHERE p.page >= 45 AND p.page <= 49 AND p.gender::text = 'FEMALE'::text) AS f45to49,
count(*) FILTER (WHERE p.page >= 50 AND p.page <= 54 AND p.gender::text = 'MALE'::text) AS m50to54,
count(*) FILTER (WHERE p.page >= 50 AND p.page <= 54 AND p.gender::text = 'FEMALE'::text) AS f50to54,
count(*) FILTER (WHERE p.page >= 55 AND p.page <= 59 AND p.gender::text = 'MALE'::text) AS m55to59,
count(*) FILTER (WHERE p.page >= 55 AND p.page <= 59 AND p.gender::text = 'FEMALE'::text) AS f55to59,
count(*) FILTER (WHERE p.page >= 60 AND p.page <= 64 AND p.gender::text = 'MALE'::text) AS m60to64,
count(*) FILTER (WHERE p.page >= 60 AND p.page <= 64 AND p.gender::text = 'FEMALE'::text) AS f60to64,
count(*) FILTER (WHERE p.page >= 65 AND p.page <= 69 AND p.gender::text = 'MALE'::text) AS m65to69,
count(*) FILTER (WHERE p.page >= 65 AND p.page <= 69 AND p.gender::text = 'FEMALE'::text) AS f65to69,
count(*) FILTER (WHERE p.page >= 70 AND p.gender::text = 'MALE'::text) AS m70over,
count(*) FILTER (WHERE p.page >= 70 AND p.gender::text = 'FEMALE'::text) AS f70over,
count(*) FILTER (WHERE p.gender::text = 'MALE'::text) AS msubtotal,
count(*) FILTER (WHERE p.gender::text = 'FEMALE'::text) AS fsubtotal,
count(*) AS grandtotal
from pms.cases c ,
patient_age p,
LATERAL json_array_elements(
CASE 
    WHEN pms.is_json(c.doh_icd_diagnosis ::character varying) AND c.doh_icd_diagnosis IS NOT NULL THEN c.doh_icd_diagnosis::json
ELSE NULL::json
    END) d(value)
LEFT JOIN referential.doh_icd_codes dic on dic.icd10_code = d.value ->> 'icdCode'
WHERE 
c.patient = p.id
and d.value ->> 'icdCode' is not null 
and (case 
when c.discharge_condition = 'EXPIRED' then 
d.value ->> 'causeOfDeath' = 'UNDERLYING' 
else 
coalesce(cast(d.value ->> 'isFinalDiagnosis' as Boolean),false) = true 
end) = true
and 
c.discharge_disposition in ('DAMA/HAMA','TRANSFERRED','ABSCONDED','DISCHARGED')
and c.registry_type in ('IPD')
and c.may_go_home_datetime is not null
and date_part('year', discharged_datetime + INTERVAL '8h') = ${year} 
GROUP BY (d.value ->> 'icdCode'::text), (d.value ->> 'icdDesc'::text), (date_part('year'::text, c.entry_datetime), dic.icd10_cat)
ORDER BY (count(*)) desc
		"""
		) as List<Map<String, Object>>
	}
	}
