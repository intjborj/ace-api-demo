package com.hisd3.hismk2.repository.pms


import com.hisd3.hismk2.domain.pms.VaccinationShot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface VaccinationShotRepository extends JpaRepository<VaccinationShot,UUID>{

    @Query(value = '''Select s from VaccinationShot s ''')
    List<VaccinationShot> listAll()

    @Query(value = '''Select s from VaccinationShot s where (s.patient.id = :patientId)''')
    List<VaccinationShot> searchByPatient(@Param("patientId") UUID patientId)
}
