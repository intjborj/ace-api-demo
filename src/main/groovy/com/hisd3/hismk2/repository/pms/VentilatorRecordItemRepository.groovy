package com.hisd3.hismk2.repository.pms


import com.hisd3.hismk2.domain.pms.VentilatorRecord
import com.hisd3.hismk2.domain.pms.VentilatorRecordItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface VentilatorRecordItemRepository extends JpaRepository<VentilatorRecordItem, UUID>{

    @Query(value = '''Select s from VentilatorRecordItem s where (s.ventilatorRecord.id = :id)''')
    List<VentilatorRecordItem> searchVentilatorItemByVR(@Param("id") UUID id)
}