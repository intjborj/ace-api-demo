package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.CatheterRecord
import com.hisd3.hismk2.domain.pms.SurgicalSiteRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SurgicalSiteRecordRepository extends JpaRepository<SurgicalSiteRecord, UUID>{

    @Query(value = '''Select s from SurgicalSiteRecord s where (s.aCase.id = :id)''')
    List<SurgicalSiteRecord> searchSurgicalSiteByCase(@Param("id") UUID id)
}