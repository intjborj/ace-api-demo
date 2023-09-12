package com.hisd3.hismk2.repository.pms


import com.hisd3.hismk2.domain.pms.Vaccination
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface VaccinationRepository extends JpaRepository<Vaccination, UUID> {

    @Query(value = '''Select s from Vaccination s ''')
    List<Vaccination> listAll()

    @Query(value = '''Select s from Vaccination s where (s.patient.id = :patientId)''')
    List<Vaccination> searchByPatient(@Param("patientId") UUID patientId)

}
