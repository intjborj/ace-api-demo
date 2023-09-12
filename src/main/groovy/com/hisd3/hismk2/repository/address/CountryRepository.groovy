package com.hisd3.hismk2.repository.address

import com.hisd3.hismk2.domain.address.Country
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CountryRepository extends JpaRepository<Country, Long>{

    @Query(value = """Select c from Country c where lower(c.country) like lower(concat('%',:filter,'%')) """)
    List<Country> searchCountryByFilter(@Param("filter") String filter)
}
