package com.hisd3.hismk2.repository.billing

import com.hisd3.hismk2.domain.billing.Investor
import com.hisd3.hismk2.graphqlservices.billing.dto.InvestorIdFullNameDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InvestorsRepository extends JpaRepository<Investor, UUID> {
	
	@Query(value = """
    Select c from Investor c  where (c.active = TRUE or c.active IS NULL) and (lower(c.fullName) like lower(concat('%',:filter,'%'))
    or
    lower(c.investorNo) like lower(concat('%',:filter,'%'))
    or 
     lower(c.arno) like lower(concat('%',:filter,'%')))
""",
			countQuery = """
     Select count(c) from Investor c  where (c.active = TRUE or c.active IS NULL) and (lower(c.fullName) like lower(concat('%',:filter,'%'))
     or
     lower(c.investorNo) like lower(concat('%',:filter,'%'))
     or 
     lower(c.arno) like lower(concat('%',:filter,'%')))
""")
	Page<Investor> getInvestors(@Param("filter") String filter,
	                            Pageable page)

	@Query(value = " Select i.id as id, i.fullName as fullName from Investor i where i.id = :id")
	InvestorIdFullNameDto findInvestorById(@Param("id")UUID id)

	@Query(value = " Select i from Investor i where i.investorNo = :investorNo and coalesce(i.active,TRUE) is TRUE")
	List<Investor> findByInvestorNo(@Param("investorNo")String investorNo)
}
