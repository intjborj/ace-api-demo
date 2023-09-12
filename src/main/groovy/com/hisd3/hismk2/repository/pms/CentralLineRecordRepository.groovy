package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.CentralLineRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CentralLineRecordRepository extends JpaRepository<CentralLineRecord, UUID>{

    @Query(value = '''Select s from CentralLineRecord s where (s.aCase.id = :id)''')
    List<CentralLineRecord> searchCentralLineByCase(@Param("id") UUID id)
}