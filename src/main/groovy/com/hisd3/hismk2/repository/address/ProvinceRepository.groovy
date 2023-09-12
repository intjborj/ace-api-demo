package com.hisd3.hismk2.repository.address

import com.hisd3.hismk2.domain.address.Province
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProvinceRepository extends JpaRepository<Province, Long> {

    @Query(value = """Select c from Province c where lower(c.name) like lower(concat('%',:filter,'%')) """)
    List<Province> searchProvinceByFilter(@Param("filter") String filter)
}
