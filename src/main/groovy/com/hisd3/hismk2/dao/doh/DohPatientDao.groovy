package com.hisd3.hismk2.dao.doh

import com.hisd3.hismk2.domain.pms.CaseServiceType
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.rest.dto.HospOptDischargesSpecialty
import com.hisd3.hismk2.rest.dto.HospOptDischargesSpecialtyOthers
import com.hisd3.hismk2.rest.dto.HospOptSummaryOfPatients
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant

@TypeChecked
@Service
@Transactional
class DohPatientDao {
	@Autowired
	private CaseRepository caseRepository
	
	HospOptSummaryOfPatients hospOptSummaryOfPatients(Instant year) {
		Timestamp reportingYear = Timestamp.from(year)
		
		return new HospOptSummaryOfPatients().tap {
			hfhudcode = ''
			totalinpatients = caseRepository.getTotalInPatients(reportingYear)
			totalnewborn = caseRepository.getTotalInNewborns(reportingYear)
			totaldischarges = caseRepository.getTotalAliveDischarges(reportingYear)
			totalpad = caseRepository.getTotalAdmittedAndDischargedInSameDay(reportingYear)
			totalibd = caseRepository.getTotalAdmittedBedDays(reportingYear)
			totalinpatienttransto = caseRepository.getTotalAdmittedToThisFacility(reportingYear)
			totalinpatienttransfrom = caseRepository.getTotalAdmittedFromThisFacility(reportingYear)
			totalpatientsremaining = caseRepository.getTotalPatientsRemainingAsOfMidnightLastDayOfPreviousYear(reportingYear)
			reportingyear = new SimpleDateFormat("yyyy").format(reportingYear).toInteger()
		}
	}
	
	List<HospOptDischargesSpecialty> hospOptDischargesSpecialty(Instant year) {
		Timestamp reportingYear = Timestamp.from(year)
		List<HospOptDischargesSpecialty> responseData = new ArrayList<HospOptDischargesSpecialty>()
		
		for (CaseServiceType serviceType : CaseServiceType.values()) {
			responseData.add(
					new HospOptDischargesSpecialty().tap {
						hfhudcode = 'SAMPLE-' + serviceType.toString()
						typeofservice = serviceType.getValue()
						nopatients = caseRepository.getNoOfPatientsByServiceType(serviceType.getValue(), reportingYear)
						totallengthstay = caseRepository.getTotalLengthOfStayByServiceType(serviceType.getValue(), reportingYear)
						nppay = caseRepository.getInpatientCountByAccommodationType('SELF', serviceType.getValue(), reportingYear)
						nphservicecharity = caseRepository.getInpatientCountByAccommodationType('SERVICE CHARITY', serviceType.getValue(), reportingYear)
						nphtotal = caseRepository.getTotalNonPhilHealth(serviceType.getValue(), reportingYear)
						phpay = caseRepository.getInpatientCountByAccommodationType('NHIP/MEMBER', serviceType.getValue(), reportingYear)
						phservice = caseRepository.getTotalPhilHealthService(serviceType.getValue(), reportingYear)
						phtotal = caseRepository.getTotalPhilHealth(serviceType.getValue(), reportingYear)
						hmo = caseRepository.getInpatientCountByAccommodationType('HMO', serviceType.getValue(), reportingYear)
						owwa = caseRepository.getInpatientCountByAccommodationType('OWWA', serviceType.getValue(), reportingYear)
						recoveredimproved = caseRepository.getRecoveredImprovedCount(serviceType.getValue(), reportingYear)
						transferred = caseRepository.getInpatientCountByDischargeDisposition('TRANSFERRED', serviceType.getValue(), reportingYear)
						hama = caseRepository.getInpatientCountByDischargeDisposition('DAMA/HAMA', serviceType.getValue(), reportingYear)
						absconded = caseRepository.getInpatientCountByDischargeDisposition('ABSCONDED', serviceType.getValue(), reportingYear)
						unimproved = caseRepository.getInpatientCountByDischargeCondition('UNIMPROVED', serviceType.getValue(), reportingYear)
						deathsbelow48 = caseRepository.getDeathCountBelow48hrs(serviceType.getValue(), reportingYear)
						deathsover48 = caseRepository.getDeathCountOverOrEqualTo48hrs(serviceType.getValue(), reportingYear)
						totaldeaths = caseRepository.getTotalDeathCount(serviceType.getValue(), reportingYear)
						totaldischarges = caseRepository.getTotalDischargesByServiceCode(serviceType.getValue(), reportingYear)
						remarks = null
						reportingyear = new SimpleDateFormat("yyyy").format(reportingYear).toInteger()
					}
			)
		}
		
		return responseData
	}
	
	List<HospOptDischargesSpecialtyOthers> hospOptDischargesSpecialtyOthers(Instant year) {
		Timestamp reportingYear = Timestamp.from(year)
		List<HospOptDischargesSpecialtyOthers> responseData = new ArrayList<HospOptDischargesSpecialtyOthers>()
		
		for (CaseServiceType serviceType : CaseServiceType.values()) {
			responseData.add(
					new HospOptDischargesSpecialtyOthers().tap {
						hfhudcode = 'SAMPLE-' + serviceType.toString()
						othertypeofservicespecify = serviceType.getValue()
						nopatients = caseRepository.getNoOfPatientsByServiceType(serviceType.getValue(), reportingYear)
						totallengthstay = caseRepository.getTotalLengthOfStayByServiceType(serviceType.getValue(), reportingYear)
						nppay = caseRepository.getInpatientCountByAccommodationType('SELF', serviceType.getValue(), reportingYear)
						nphservicecharity = caseRepository.getInpatientCountByAccommodationType('SERVICE CHARITY', serviceType.getValue(), reportingYear)
						nphtotal = caseRepository.getTotalNonPhilHealth(serviceType.getValue(), reportingYear)
						phpay = caseRepository.getInpatientCountByAccommodationType('NHIP/MEMBER', serviceType.getValue(), reportingYear)
						phservice = caseRepository.getTotalPhilHealthService(serviceType.getValue(), reportingYear)
						phtotal = caseRepository.getTotalPhilHealth(serviceType.getValue(), reportingYear)
						hmo = caseRepository.getInpatientCountByAccommodationType('HMO', serviceType.getValue(), reportingYear)
						owwa = caseRepository.getInpatientCountByAccommodationType('OWWA', serviceType.getValue(), reportingYear)
						recoveredimproved = caseRepository.getRecoveredImprovedCount(serviceType.getValue(), reportingYear)
						transferred = caseRepository.getInpatientCountByDischargeDisposition('TRANSFERRED', serviceType.getValue(), reportingYear)
						hama = caseRepository.getInpatientCountByDischargeDisposition('DAMA/HAMA', serviceType.getValue(), reportingYear)
						absconded = caseRepository.getInpatientCountByDischargeDisposition('ABSCONDED', serviceType.getValue(), reportingYear)
						unimproved = caseRepository.getInpatientCountByDischargeCondition('UNIMPROVED', serviceType.getValue(), reportingYear)
						deathsbelow48 = caseRepository.getDeathCountBelow48hrs(serviceType.getValue(), reportingYear)
						deathsover48 = caseRepository.getDeathCountOverOrEqualTo48hrs(serviceType.getValue(), reportingYear)
						totaldeaths = caseRepository.getTotalDeathCount(serviceType.getValue(), reportingYear)
						totaldischarges = caseRepository.getTotalDischargesByServiceCode(serviceType.getValue(), reportingYear)
						remarks = null
						reportingyear = new SimpleDateFormat("yyyy").format(reportingYear).toInteger()
					}
			)
		}
		
		return responseData
	}
}
