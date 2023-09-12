package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.EmployeeAllowance
import com.hisd3.hismk2.domain.hrm.EmployeeBiometricConfig
import com.hisd3.hismk2.domain.hrm.EmployeeLoan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EmployeeBiometricConfigRepository extends JpaRepository<EmployeeBiometricConfig, UUID> {


    @Query(value= "Select c from EmployeeBiometricConfig c join c.employee e where e.id = :id")
    List<EmployeeBiometricConfig> findAllEmployeeBiometric(@Param("id") UUID id)

    @Query(value = "Select c from EmployeeBiometricConfig c")
    List<EmployeeBiometricConfig>findAllBiometricInEmployee()


//    @Query( value = "Select c from Employee c Where (concat(lastName ,' ', firstName, ' ' , middleName))) ",
//            countQuery = "Select count(e) from BiometricDevice e where lower(e.deviceName) like lower(concat('%',:filter,'%'))"
//    )List<EmployeeBiometricConfig>findEmployeePageable(@Param("filter") String name)




}