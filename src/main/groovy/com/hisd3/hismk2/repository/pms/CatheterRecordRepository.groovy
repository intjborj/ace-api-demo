package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.CatheterRecord
import com.hisd3.hismk2.domain.pms.CentralLineRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CatheterRecordRepository extends JpaRepository<CatheterRecord, UUID>{

    @Query(value = '''Select s from CatheterRecord s where (s.aCase.id = :id)''')
    List<CatheterRecord> searchCatheterByCase(@Param("id") UUID id)
}