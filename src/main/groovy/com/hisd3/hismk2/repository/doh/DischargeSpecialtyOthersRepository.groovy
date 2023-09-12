package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.HospOptDischargeSpecialtyOthers
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DischargeSpecialtyOthersRepository extends JpaRepository<HospOptDischargeSpecialtyOthers, UUID> {
    @Query(value = "select c from HospOptDischargeSpecialtyOthers c")
    List<HospOptDischargeSpecialtyOthers> findAllDischargeSpecialtyOthers()
}