package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.Case
import groovy.transform.TypeChecked
import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.sql.Timestamp
import java.time.Instant

@JaversSpringDataAuditable
@TypeChecked
interface CaseRepository extends JpaRepository<Case, UUID> {

    @Query(
            value = "Select c from Case c where c.patient.id = :patientId and c.status = 'ACTIVE' order by c.entryDateTime desc"
    )
    Case getPatientActiveCase(@Param("patientId") UUID patientId)

    @Query(
            value = "Select c from Case c where   c.status = 'ACTIVE' and c.registryType = 'IPD'  order by c.entryDateTime desc"
    )
    List<Case> getActiveCasesForRoomCharge()

    @Query(
            value = "Select c from Case c where c.caseNo = :caseNo"
    )
    Case findByCaseNo(@Param("caseNo") String caseNo)

    @Query(value = '''Select c from Case c where c.patient.id = :patientId order by c.entryDateTime''')
    List<Case> getPatientCases(@Param("patientId") UUID patientId)

    @Query(value = '''Select c from Case c where c.patient.id = :patientId''',
            countQuery = '''Select count(c) from Case c where c.patient.id = :patientId'''
    )
    Page<Case> getPatientCasesPageable(@Param("patientId") UUID patientId, Pageable pageable)

    @Query(value = '''Select c from Case c where c.patient.id in :patientIds''',
            countQuery = '''Select count(c) from Case c where c.patient.id in :patientIds'''
    )
    Page<Case> getByPatientListPageable(@Param("patientIds") List<UUID> patientIds, Pageable pageable)

    @Query(value = '''Select c from Case c order by c.entryDateTime desc''')
    List<Case> getAllPatientCase()

    @Query(value = '''Select c from Case c Where c.entryDateTime = :date ''')
    List<Case> getAllCaseByDate(@Param("date") Instant date)

    @Query(value = '''Select c from Case c Where c.registryType = :allcasesbyregistrytype ''')
    List<Case> getAllCasesByRegistryType(@Param("allcasesbyregistrytype") String anythingtosay)

    @Query(value = '''Select c From Case c Where c.accommodationType = :accommodation''')
    List<Case> getAllAccommodationType(@Param("accommodation") String accommodation)

    @Query(value = '''SELECT c FROM Case c WHERE c.registryType = :registryType AND (c.entryDateTime BETWEEN :from AND :to)''')
    List<Case> getAllCaseByEntryDatetime(
            @Param("registryType") String registryType,
            @Param("from") Instant from,
            @Param("to") Instant to
    )

    // Doh: do not add function under ---------------------------------------------
    //getAllInpatients
    @Query(value = '''Select c From Case c Where c.admissionDatetime is not null AND c.registryType ='IPD'  ''')
    List<Case> getAllInPatients()

    //getAllNewBorn
    @Query(value = '''Select c From Case c Where c.serviceType = 'NEW BORN'  ''')
    List<Case> getAllNewborn()

    //getAllDischarged
    @Query(value = '''Select c From Case c Where c.admissionDatetime is not null AND c.registryType ='IPD'  ''')
    List<Case> getAllDischarged()

    // Doh: do not add function above ---------------------------------------------

    @Query(nativeQuery = true, value = """
	Select count(c) from pms.cases c
		where c.admission_datetime is not null and c.registry_type = 'IPD'
			and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getTotalInPatients(@Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
	Select count(c) from pms.cases c
		where c.is_infacility_delivery = true and c.service_code = 9
			and date(c.entry_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getTotalInNewborns(@Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
	Select count(c) from pms.cases c
		where c.admission_datetime is not null and c.discharged_datetime is not null and c.registry_type ='IPD' and c.discharge_condition <> 'EXPIRED'
			and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getTotalAliveDischarges(@Param("reportingYear") Timestamp reportingYear)

    //OPD Visits
    @Query(value = '''Select count (c) From Case c Where c.registryType ='OPD'  and c.serviceType <> 'PEDIATRIC' and c.serviceType <> 'NEWBORN' and  c.createdDate >= :startDate and c.createdDate <= :endDate ''')
    Integer getAdultCase(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate)

    @Query(value = '''Select c From Case c Where c.registryType ='OPD'  and  c.patient.createdDate >= :startDate and c.patient.createdDate <= :endDate ''')
    List<Case> getAllNewCase(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate)

    @Query(value = '''Select count(c) From Case c Where c.registryType ='OPD'  and  c.patient.createdDate >= :startDate and c.patient.createdDate <= :endDate ''')
    Integer getAllNewCaseCount(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate)

    @Query(value = '''Select count (c) From Case c Where c.registryType ='OPD' and c.patient.createdDate <= :startDate  ''')
    Integer getRevisits(@Param("startDate") Instant startDate)

    @Query(value = '''Select count(c) From Case c Where c.registryType ='OPD'  and c.serviceType = 'PEDIATRIC'  and  c.createdDate >= :startDate and c.createdDate <= :endDate ''')
    Integer getPediatric(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate)

    @Query(value = '''Select count (c) From Case c Where c.registryType ='OPD'  and c.serviceType = 'MEDICINE'  and  c.createdDate >= :startDate and c.createdDate <= :endDate ''')
    Integer getAdultMedicine(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate)

    @Query(value = '''Select count (c) From Case c Where c.registryType ='OPD'  and c.serviceType = 'SURGICAL'  and  c.createdDate >= :startDate and c.createdDate <= :endDate ''')
    Integer getSurgical(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate)

    @Query(value = '''Select count (c) From Case c Where c.registryType ='OPD' and c.isAntenatal = true  and  c.createdDate >= :startDate and c.createdDate <= :endDate ''')
    Integer getAntenatal(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate)

    @Query(value = '''Select count (c) From Case c Where c.registryType ='OPD' and c.isPostnatal = true  and  c.createdDate >= :startDate and c.createdDate <= :endDate ''')
    Integer getPostnatal(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate)

    @Query(value = '''Select count(c) From Case c Where c.registryType ='OPD' and c.serviceType <> 'MEDICINE' and c.serviceType <> 'SURGICAL'  and  c.createdDate >= :startDate and c.createdDate <= :endDate ''')
    Integer getSpecialtyNonSurgical(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate)

    @Query(nativeQuery = true, value = """
		Select count(c) from pms.cases c
			where c.admission_datetime is not null and date(c.admission_datetime + interval '8h') = date(c.discharged_datetime + interval '8h') and c.registry_type = 'IPD'
				and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
		""")
    Integer getTotalAdmittedAndDischargedInSameDay(@Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
		Select coalesce(sum(date_part('day', discharged_datetime) - date_part('day', admission_datetime)), 0) + coalesce((
		  Select sum(date_part('day', now()) - date_part('day', admission_datetime)) from pms.cases
		    where admission_datetime is not null and discharged_datetime is null and registry_type = 'IPD'
		  		and date(admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
		), 0) from pms.cases c
			where c.admission_datetime is not null and registry_type = 'IPD'
				and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getTotalAdmittedBedDays(@Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
		Select count(distinct c.patient) from pms.cases c
			where c.admission_datetime is not null and c.transferred_in = true and c.registry_type = 'IPD'
				and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
		""")
    Integer getTotalAdmittedToThisFacility(@Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
		Select count(distinct c.patient) from pms.cases c
			where c.admission_datetime is not null and c.discharged_datetime is not null and c.transferred_out = true and c.registry_type = 'IPD'
				and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
		""")
    Integer getTotalAdmittedFromThisFacility(@Param("reportingYear") Timestamp reportingYear)

    //TODO not yet finished query
    @Query(nativeQuery = true, value = """
		Select count(distinct c.patient)
			from pms.cases c
			where c.admission_datetime is not null and c.registry_type = 'IPD' and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getTotalPatientsRemainingAsOfMidnightLastDayOfPreviousYear(@Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
	Select count(c) from pms.cases c
		where c.admission_datetime is not null and c.service_code = :serviceCode and c.registry_type = 'IPD'
			and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getNoOfPatientsByServiceType(@Param("serviceCode") Integer serviceCode, @Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
		Select coalesce(sum(date_part('day', discharged_datetime) - date_part('day', admission_datetime)), 0) + coalesce((
		  Select sum(date_part('day', now()) - date_part('day', admission_datetime)) from pms.cases
		    where admission_datetime is not null and discharged_datetime is null and service_code = :serviceCode and registry_type = 'IPD'
		  		and date(admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
		), 0) from pms.cases c
			where c.admission_datetime is not null and c.service_code = :serviceCode and c.registry_type = 'IPD'
				and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getTotalLengthOfStayByServiceType(@Param("serviceCode") Integer serviceCode, @Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
	Select count(c) from pms.cases c
		where c.admission_datetime is not null and c.service_code = :serviceCode and c.accommodation_type = :accommodationType and c.registry_type = 'IPD'
			and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getInpatientCountByAccommodationType(@Param("accommodationType") String accommodationType, @Param("serviceCode") Integer serviceCode, @Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
	Select count(c) from pms.cases c
		where c.admission_datetime is not null and c.service_code = :serviceCode and (c.accommodation_type = 'SELF' or c.accommodation_type = 'SERVICE CHARITY') and c.registry_type = 'IPD'
			and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getTotalNonPhilHealth(@Param("serviceCode") Integer serviceCode, @Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
	Select count(c) from pms.cases c
		where c.admission_datetime is not null and c.service_code = :serviceCode and (c.accommodation_type = 'NHIP/DEPENDENT' or c.accommodation_type = 'INDIGENT') and c.registry_type = 'IPD'
			and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getTotalPhilHealthService(@Param("serviceCode") Integer serviceCode, @Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
	Select count(c) from pms.cases c
		where c.admission_datetime is not null and c.service_code = :serviceCode and (c.accommodation_type = 'NHIP/MEMBER' or c.accommodation_type = 'NHIP/DEPENDENT' or c.accommodation_type = 'INDIGENT') and c.registry_type = 'IPD'
			and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getTotalPhilHealth(@Param("serviceCode") Integer serviceCode, @Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
	Select count(c) from pms.cases c
		where c.admission_datetime is not null and c.discharged_datetime is not null and c.service_code = :serviceCode and (c.discharge_condition = 'IMPROVED' or c.discharge_condition = 'RECOVERED') and c.registry_type = 'IPD'
			and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getRecoveredImprovedCount(@Param("serviceCode") Integer serviceCode, @Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
	Select count(c) from pms.cases c
		where c.admission_datetime is not null and c.discharged_datetime is not null and c.service_code = :serviceCode and c.discharge_condition = :dischargeCondition and c.registry_type = 'IPD'
			and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getInpatientCountByDischargeCondition(@Param("dischargeCondition") String dischargeCondition, @Param("serviceCode") Integer serviceCode, @Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
	Select count(c) from pms.cases c
		where c.admission_datetime is not null and c.discharged_datetime is not null and c.service_code = :serviceCode and c.discharge_disposition = :dischargeDisposition and c.registry_type = 'IPD'
			and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getInpatientCountByDischargeDisposition(@Param("dischargeDisposition") String dischargeDisposition, @Param("serviceCode") Integer serviceCode, @Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
	Select count(c) from pms.cases c
		where c.admission_datetime is not null and c.discharged_datetime is not null and c.service_code = :serviceCode and c.registry_type ='IPD' and c.discharge_condition = 'EXPIRED'
		  and c.time_of_death + interval '8h' between c.admission_datetime + interval '8h' and c.admission_datetime + interval '2d 8h -1seconds'
			and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getDeathCountBelow48hrs(@Param("serviceCode") Integer serviceCode, @Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
	Select count(c) from pms.cases c
		where c.admission_datetime is not null and c.discharged_datetime is not null and c.service_code = :serviceCode and c.registry_type ='IPD' and c.discharge_condition = 'EXPIRED'
		  and c.time_of_death + interval '8h' >= c.admission_datetime + interval '2d 8h'
			and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getDeathCountOverOrEqualTo48hrs(@Param("serviceCode") Integer serviceCode, @Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
	Select count(c) from pms.cases c
		where c.admission_datetime is not null and c.discharged_datetime is not null and c.service_code = :serviceCode and c.registry_type ='IPD' and c.discharge_condition = 'EXPIRED'
			and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getTotalDeathCount(@Param("serviceCode") Integer serviceCode, @Param("reportingYear") Timestamp reportingYear)

    @Query(nativeQuery = true, value = """
	Select count(c) from pms.cases c
		where c.admission_datetime is not null and c.discharged_datetime is not null and c.service_code = :serviceCode and c.registry_type ='IPD'
			and date(c.admission_datetime + interval '8h') between date(:reportingYear) - interval '1seconds' and date(:reportingYear) + interval '1yr -1seconds'
	""")
    Integer getTotalDischargesByServiceCode(@Param("serviceCode") Integer serviceCode, @Param("reportingYear") Timestamp reportingYear)

    @Query(value = """
     from Case c where c.mayGoHomeDatetime is not null and
        (
        lower( c.patient.fullName) like concat('%',:filter,'%')
          or
          lower( c.caseNo) like concat('%',:filter,'%')
        )
     and c.id in (Select b.patientCase.id from Billing b where b.patientCase = c and b.status='ACTIVE')
   """, countQuery = """
    Select count(c) from Case c where c.mayGoHomeDatetime is not null  and
        (
        lower( c.patient.fullName) like concat('%',:filter,'%')
          or
          lower( c.caseNo) like concat('%',:filter,'%')
        )
    and c.id in (Select b.patientCase.id from Billing b where b.patientCase = c and b.status='ACTIVE')
  """)
    Page<Case> getPatientsForDischarge(
            @Param("filter") String filter,
            Pageable page)

    @Query(value = '''Select c From Case c Where c.status = 'ACTIVE' and c.registryType = 'OPD' ''')
    List<Case> getAllActiveCasesByRegistry()

    @Query(value = "select count(c) from Case c where c.deliveryType = :deliveryType and c.dischargedDatetime between :start and :end")
    Integer countDischargeDeliviries(@Param('deliveryType') String deliveryType, @Param('start') Instant start, @Param('end') Instant end)

    @Query(value = "Select c from Case c where EXTRACT(YEAR FROM c.entryDateTime) = :year")
    List<Case> getPatientCensus(@Param("year") Integer year)

    @Query(value = '''
		select c from Case c
            where c.registryType IN ('IPD', 'ERD') AND (c.mayGoHomeDatetime IS NOT NULL AND status = 'ACTIVE') OR (c.dischargedDatetime IS NOT NULL AND status = 'DISCHARGED') 
            AND (c.dischargedDatetime between :start and :end) AND (lower(c.patient.lastName) like lower(concat('%',:search,'%')) or
            lower(c.patient.firstName) like lower(concat('%',:search,'%')) or
            lower(c.patient.middleName) like lower(concat('%',:search,'%')))
	''')
    List<Case> getDischargeReport(@Param("start") Instant start, @Param("end") Instant end, @Param("search") String search)

    @Query(value = '''
		select c from Case c
             where (lower(c.patient.lastName) like lower(concat('%',:search,'%')) or
            lower(c.patient.firstName) like lower(concat('%',:search,'%')) or
            lower(c.patient.middleName) like lower(concat('%',:search,'%'))) AND (c.mayGoHomeDatetime IS NOT NULL AND status = 'ACTIVE' AND c.registryType IN ('IPD', 'ERD')) OR (c.dischargedDatetime IS NOT NULL AND status = 'DISCHARGED' AND c.registryType IN ('IPD', 'ERD')) 
            AND (c.dischargedDatetime between :start and :end)
	''', countQuery = '''
   select count(c) from Case c
              where (lower(c.patient.lastName) like lower(concat('%',:search,'%')) or
            lower(c.patient.firstName) like lower(concat('%',:search,'%')) or
            lower(c.patient.middleName) like lower(concat('%',:search,'%'))) AND (c.mayGoHomeDatetime IS NOT NULL AND status = 'ACTIVE' AND c.registryType IN ('IPD', 'ERD')) OR (c.dischargedDatetime IS NOT NULL AND status = 'DISCHARGED' AND c.registryType IN ('IPD', 'ERD')) 
             AND (c.dischargedDatetime between :start and :end)
             ''')
    Page<Case> getDischargeReport(@Param("start") Instant start, @Param("end") Instant end, @Param("search") String search, Pageable pageable)


    @Query(value = '''SELECT c From Case c WHERE c.status = 'ACTIVE' 
				AND lower(c.patient.firstName) like lower(concat('%',:firstName,'%')) 
				AND lower(c.patient.lastName) like lower(concat('%',:lastName,'%')) 
				AND lower(c.registryType) in (:registryType)
			''',
            countQuery = '''
            SELECT count(c) From Case c WHERE c.status = 'ACTIVE' 
				AND lower(c.patient.firstName) like lower(concat('%',:firstName,'%')) 
				AND lower(c.patient.lastName) like lower(concat('%',:lastName,'%')) 
				AND lower(c.registryType) in (:registryType)
			''')
    Page<Case> fetchActivePatients(
            @Param("lastName") String lastName,
            @Param("firstName") String firstName,
            @Param("registryType") List<String> registryType,
            Pageable page
    )
}
