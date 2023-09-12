package com.hisd3.hismk2.repository.address

import com.hisd3.hismk2.domain.address.Municipality
import com.hisd3.hismk2.domain.address.Region
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface RegionRepository extends JpaRepository<Region, UUID> {

    @Query(value = """Select c from Region c where lower(c.name) like lower(concat('%',:filter,'%')) """)
    List<Region> searchRegionByFilter(@Param("filter") String filter)

    @Query(value = """Select c from Region c where c.country.id = :filter """)
    List<Region> searchRegionByCountry(@Param("filter") Long filter)

}
