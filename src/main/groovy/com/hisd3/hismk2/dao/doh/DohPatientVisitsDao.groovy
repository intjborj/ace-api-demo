package com.hisd3.hismk2.dao.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.rest.dto.LeadingErConsult
import com.hisd3.hismk2.rest.dto.VisitsDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate

@Service
@Transactional
class DohPatientVisitsDao {
	
	@Autowired
	private CaseRepository caseRepository
	
	@Autowired
	private JdbcTemplate jdbcTemplate
	
	@Autowired
	ObjectMapper objectMapper
	
	VisitsDTO getVisits(String start, String end) {
		
		Timestamp startTimeS = Timestamp.valueOf(start)
		Timestamp endTimeS = Timestamp.valueOf(end)
		
		Instant startDate = startTimeS.toInstant()
		Instant endDate = endTimeS.toInstant()
		def newPatient = caseRepository.getAllNewCaseCount(startDate, endDate)
		def adultCase = caseRepository.getAdultCase(startDate, endDate)
		def reVisits = caseRepository.getRevisits(startDate)
		def pediatrics = caseRepository.getPediatric(startDate, endDate)
		def adultMedicine = caseRepository.getAdultMedicine(startDate, endDate)
		def surgical = caseRepository.getSurgical(startDate, endDate)
		def antenatal = caseRepository.getAntenatal(startDate, endDate)
		def postnatal = caseRepository.getPostnatal(startDate, endDate)
		def specialtyNonsurgical = caseRepository.getSpecialtyNonSurgical(startDate, endDate)
		
		VisitsDTO res = new VisitsDTO()
		res.newPatient = newPatient
		res.adult = adultCase
		res.revisit = reVisits
		res.pediatric = pediatrics
		res.adultGeneralMedicine = adultMedicine
		res.surgical = surgical
		res.antenatal = antenatal
		res.postnatal = postnatal
		res.specialtyNonSurgical = specialtyNonsurgical
		res.reportingYear = startTimeS.toLocalDateTime().getYear()
		return res
	}
	
	List<LeadingErConsult> LeadingErConsult(String filter, String start, String end) {
		
		LocalDate startD = LocalDate.parse(start)
		LocalDate endD = LocalDate.parse(end)
		
		List<LeadingErConsult> results = []
		
		def query = jdbcTemplate.queryForList("""
						select     count(c) as count,
						   each_cases ->> 'icdCode' icdCode,
						   each_cases ->> 'icdDesc' icdDesc ,
						   each_cases ->> 'icdCategory' icdCategory
						from       pms.cases c
              			 cross join json_array_elements(c.doh_icd_diagnosis::json) each_cases
						where
						c.doh_icd_diagnosis is not null and c.registry_type = ? AND c.entry_datetime >= ? AND c.entry_datetime <= ?
						group by (each_cases->>'icdCode',each_cases ->> 'icdDesc',each_cases ->> 'icdCategory') order by  count desc limit 10 """,
				filter,
				startD,
				endD
		)
		
		query.forEach {
			it ->
				LeadingErConsult item = new LeadingErConsult()
				
				def icd = it.get("icdCode") as String
				item.erconsultaions = icd
				item.number = it.get("count") as String
				item.icdCode = it.get("icdDesc") as String
				item.icdCategory = it.get("icdCategory") as String
				item.reportingyear = startD.getYear()
				results.add(item)
		}
		
		return results
	}
}
