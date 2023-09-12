package com.hisd3.hismk2.repository.philhealth

import com.hisd3.hismk2.domain.philhealth.InsuranceCompany
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InsuranceCompanyRepository extends JpaRepository<InsuranceCompany, UUID> {
	@Query(value = "Select ci from InsuranceCompany ci where lower(ci.companyName) like concat('%',:companyName,'%')")
	List<InsuranceCompany> getInsurancesByFilter(@Param("companyName") String companyName)
}
