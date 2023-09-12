package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.VaccinationShot
import com.hisd3.hismk2.domain.pms.VentilatorRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface VentilatorRecordRepository extends JpaRepository<VentilatorRecord, UUID>{

    @Query(value = '''Select s from VentilatorRecord s where (s.aCase.id = :id)''')
    List<VentilatorRecord> searchVentilatorByCase(@Param("id") UUID id)
}