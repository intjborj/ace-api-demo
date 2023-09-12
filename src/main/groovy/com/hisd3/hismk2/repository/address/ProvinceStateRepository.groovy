package com.hisd3.hismk2.repository.address

import com.hisd3.hismk2.domain.address.ProvinceState
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProvinceStateRepository extends JpaRepository<ProvinceState, UUID> {

    @Query(value = """Select c from ProvinceState c where lower(c.name) like lower(concat('%',:filter,'%')) """)
    List<ProvinceState> searchStateByFilter(@Param("filter") String filter)

    @Query(value = """Select c from ProvinceState c where c.region.id = :filter """)
    List<ProvinceState> searchMunicipalityByProvince(@Param("filter") UUID filter)

}
