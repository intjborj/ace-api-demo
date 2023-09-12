package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.HospOptDischargeSpecialty
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DischargeSpecialtyRepository extends JpaRepository<HospOptDischargeSpecialty, UUID> {
    @Query(value = "select c from HospOptDischargeSpecialty c")
    List<HospOptDischargeSpecialty> findAllDischargeSpecialty()
}