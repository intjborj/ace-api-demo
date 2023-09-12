package com.hisd3.hismk2.repository.address

import com.hisd3.hismk2.domain.address.Municipality
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MunicipalityRepository extends JpaRepository<Municipality, UUID> {

    @Query(value = """Select c from Municipality c where lower(c.name) like lower(concat('%',:filter,'%')) """)
    List<Municipality> searchMunicipalityByFilter(@Param("filter") String filter)


    @Query(value = """Select c from Municipality c where lower(CAST(c.province.id as string)) like lower(concat('%',:filter,'%')) """)
    List<Municipality> searchMunicipalityByProvince(@Param("filter") String filter)
}
