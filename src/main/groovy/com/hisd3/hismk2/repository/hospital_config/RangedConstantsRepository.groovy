package com.hisd3.hismk2.repository.hospital_config

import com.hisd3.hismk2.domain.clinic.Clinic
import com.hisd3.hismk2.domain.hospital_config.RangedConstant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RangedConstantsRepository extends JpaRepository<RangedConstant, UUID> {
    @Query(value = "select rc from RangedConstant rc where lower(rc.fieldName) like lower(concat('%',:fieldName,'%'))")
    RangedConstant findByFieldName(@Param("fieldName") String fieldName)

    @Query(value = """select rc from RangedConstant rc where 
                        lower(rc.fieldName) like lower(concat('%',:otherName,'%')) and
                        rc.id != :id
                    """)
    RangedConstant findByOtherFieldName(@Param("otherName") String otherName, @Param("id") UUID id)
}
