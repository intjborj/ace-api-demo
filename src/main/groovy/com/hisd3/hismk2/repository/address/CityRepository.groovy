package com.hisd3.hismk2.repository.address

import com.hisd3.hismk2.domain.address.City
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CityRepository extends JpaRepository<City, Long> {

    @Query(value = """Select c from City c where lower(c.province.name) like lower(concat('%',:province,'%'))""")
    List<City> searchCities(@Param("province") String province)
}
