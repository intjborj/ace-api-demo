package com.hisd3.hismk2.repository.address

import com.hisd3.hismk2.domain.address.Barangay
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BarangayRepository extends JpaRepository<Barangay, UUID> {

    @Query(value = """Select c from Barangay c where lower(c.name) like lower(concat('%',:filter,'%')) """)
    List<Barangay> searchBarangayByFilter(@Param("filter") String filter)

    @Query(value = """Select c from Barangay c where lower(CAST(c.municipality.id as string)) like lower(concat('%',:filter,'%')) """)
    List<Barangay> searchMunicipalityByProvince(@Param("filter") String filter)
}
