package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.Patient
import com.hisd3.hismk2.domain.pms.PatientVaccination
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PatientVaccinationRepository extends JpaRepository<PatientVaccination, UUID>{

    @Query(value = '''Select v from PatientVaccination v where v.patient.id = :id''')
    List<PatientVaccination> getVaccinationRecordsByPatient(@Param("id") UUID id)

}