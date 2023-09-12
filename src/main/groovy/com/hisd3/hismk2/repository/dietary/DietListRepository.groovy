package com.hisd3.hismk2.repository.dietary


import com.hisd3.hismk2.domain.dietary.DietList
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface DietListRepository extends JpaRepository<DietList, UUID> {



    @Query(value = """ Select d from DietList d where d.patientCase.id =:pcase 
                        and lower(d.mealSched) like lower(concat('%',:meal,'%') )
                        and d.createdDate >= :startDate and d.createdDate <= :endDate
    """)
    List<DietList> findDietByPatientAndMealtime(
    @Param("pcase") UUID pcase,
    @Param("meal") String meal,
    @Param("startDate") Instant startDate,
    @Param("endDate") Instant endDate)

    @Query(value = """ Select d from DietList d where  lower(d.mealSched) like lower(concat('%',:meal,'%') )
                        and lower(d.patientCase.patient.fullName) like lower(concat('%',:filter,'%'))
                        and d.employee is null
                        and COALESCE(d.mealToCompanion, FALSE) = FALSE
                        and d.createdDate >= :recent and d.createdDate <= :now
    """)
    List<DietList> findDietListByDate(
            @Param("filter") String filter,
            @Param("meal") String meal,
            @Param("recent") Instant recent,
            @Param("now") Instant now
    )

    @Query(value = """ Select d from DietList d where  lower(d.mealSched) like lower(concat('%',:meal,'%') )
                        and lower(d.employee.fullName) like lower(concat('%',:filter,'%'))
                        and d.patientCase is null
                        and COALESCE(d.mealToCompanion, FALSE) = FALSE
                        and d.createdDate >= :recent and d.createdDate <= :now
    """)
    List<DietList> findEmployeeDietByDate(
            @Param("filter") String filter,
            @Param("meal") String meal,
            @Param("recent") Instant recent,
            @Param("now") Instant now
    )

    @Query(value = """ Select d from DietList d where  lower(d.mealSched) like lower(concat('%',:meal,'%') )
                        and lower(alias) like lower(concat('%',:filter,'%'))
                        and d.mealToCompanion is true
                        and d.createdDate >= :recent and d.createdDate <= :now
    """)
    List<DietList> findCompanionDietByDate(
            @Param("filter") String filter,
            @Param("meal") String meal,
            @Param("recent") Instant recent,
            @Param("now") Instant now
    )

}
