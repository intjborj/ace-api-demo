package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.Patient
import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@JaversSpringDataAuditable
interface PatientRepository extends JpaRepository<Patient, UUID> {
	@Query(
			value = "Select p from Patient p where p.patientNo  =:pantientNo "
	)
	List<Patient> getPatientByPatientNo(@Param("pantientNo") String pantientNo)

	@Query(value = '''Select p from Patient p where
            lower(p.lastName) like lower(concat('%',:filter,'%')) or
            lower(p.firstName) like lower(concat('%',:filter,'%')) or
            lower(p.middleName) like lower(concat('%',:filter,'%'))
            ''')
	List<Patient> searchPatients(@Param("filter") String filter)

	@Query(value = '''Select c.patient from Case c where
 	
 				(
					lower(concat(c.patient.lastName,' ', c.patient.firstName, ' ', c.patient.middleName, case when c.room is null then '' else c.room.roomName end)) like lower(concat('%',:filter,'%'))
            	)
            
            ''')
	List<Patient> searchPatientsWithRoom(@Param("filter") String filter)

	@Query(value = '''Select p from Patient p where
			lower(p.fullName) like lower(concat('%',:filter,'%')) or
            lower(p.lastName) like lower(concat('%',:filter,'%')) or
            lower(p.firstName) like lower(concat('%',:filter,'%')) or
            lower(p.middleName) like lower(concat('%',:filter,'%'))
            ''',
			countQuery = '''Select count(p) from Patient p where
			lower(p.fullName) like lower(concat('%',:filter,'%')) or
            lower(p.lastName) like lower(concat('%',:filter,'%')) or
            lower(p.firstName) like lower(concat('%',:filter,'%')) or
            lower(p.middleName) like lower(concat('%',:filter,'%'))
            '''
	)
	Page<Patient> searchPatientsPageable(@Param("filter") String filter, Pageable pageable)
	
	
	@Query(value = 'Select c.patient from Case c where lower(c.patient.fullName) like lower(concat(\'%\',:filter,\'%\'))')
	List<Patient> filterPatients(@Param("filter") String filter)

	@Query(value = """Select c.patient from Case c where lower(c.registryType) like lower('IPD') and lower(c.status) like lower('ACTIVE') and lower(c.patient.fullName) like lower(concat('%',:filter,'%'))""")

	List<Patient> patientsListByFilter(@Param("filter") String filter)

	@Query(value = '''Select c.patient from Case c where lower(c.registryType) like lower(:type) and lower(c.status) like lower('ACTIVE') and lower(c.patient.fullName) like lower(concat('%',:filter,'%'))
            ''')
	List<Patient> filterActivePatients(@Param("type") String type, @Param("filter") String filter)

	@Query(value = '''Select c.patient from Case c where lower(c.registryType) like lower('IPD') and lower(c.status) like lower('ACTIVE')
            ''',
			countQuery = '''Select count(c.patient) from Case c where lower(c.registryType) like lower('IPD') and lower(c.status) like lower('ACTIVE')
            '''
	)
	Page<Patient> filterPatientsByAdmittedPageable(@Param("filter") String filter, Pageable pageable)

	@Query(value = '''Select c.patient from Case c where lower(c.registryType) like lower('IPD') and c.mayGoHomeDatetime is not null and lower(c.status) like lower('ACTIVE') and lower(c.patient.fullName) like lower(concat('%',:filter,'%'))
            ''')
	List<Patient> filterPatientsByForDischarge(@Param("filter") String filter)

	@Query(value = '''Select c.patient from Case c where lower(c.registryType) like lower('IPD') and c.mayGoHomeDatetime is not null and lower(c.status) like lower('ACTIVE')
            ''',
			countQuery = '''Select count(c.patient) from Case c where lower(c.registryType) like lower('IPD') and c.mayGoHomeDatetime is not null and lower(c.status) like lower('ACTIVE')
            '''
	)
	Page<Patient> filterPatientsByForDischargePageable(@Param("filter") String filter, Pageable pageable)

	@Query(value = '''Select c.patient from Case c where (lower(c.status) like lower('CLOSED') or lower(c.status) like lower('DISCHARGED')) and lower(c.patient.fullName) like lower(concat('%',:filter,'%'))
            ''')
	List<Patient> filterPatientsByDischarge(@Param("filter") String filter)

	@Query(value = '''Select m.parentCase.patient from ManagingPhysician m where m.employee.id = :id and lower(m.parentCase.patient.fullName) like lower(concat('%',:filter,'%'))
            ''')
	List<Patient> filterPatientsByEmployee(@Param("id") UUID id, @Param("filter") String filter)

	@Query(value = '''Select m.parentCase.patient from ManagingPhysician m where m.employee.id = :id
            ''',
			countQuery = '''Select count(m.parentCase.patient) from ManagingPhysician m where m.employee.id = :id
            '''
	)
	Page<Patient> filterPatientsByEmployeePageable(@Param("id") UUID id, Pageable pageable)

	@Query(value = "Select c.patient from Case c where c.room.id = :id")
	Page<Patient> filterPatientsByRoomPageable(@Param("id") UUID id, Pageable pageable)

	@Query(value = '''Select count(m.parentCase.patient) from ManagingPhysician m where m.employee.id = :id''')
	Integer countPatientsByEmployee(@Param("id") UUID id)

	@Query(value = '''Select count(c.patient) from Case c where lower(c.registryType) like lower(:type) and lower(c.status) like lower('ACTIVE')''')
	Integer countActivePatients(@Param("type") String type)

	@Query(value = '''Select count(c.patient) from Case c where lower(c.registryType) like lower('IPD') and c.mayGoHomeDatetime is not null and lower(c.status) like lower('ACTIVE')''')
	Integer countPatientsByForDischarge()

	@Query(value = '''Select c.patient from Case c where lower(c.registryType) like lower('IPD') and lower(c.status) like lower('ACTIVE')''')
	List<Patient> listAllInpatient()
}
