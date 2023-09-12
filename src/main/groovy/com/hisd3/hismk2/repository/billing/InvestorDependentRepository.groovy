package com.hisd3.hismk2.repository.billing

import com.hisd3.hismk2.domain.billing.InvestorDependent
import com.hisd3.hismk2.graphqlservices.billing.dto.InvestorIdFullNameDto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InvestorDependentRepository extends JpaRepository<InvestorDependent, UUID> {

    @Query(value = " Select id.id as dependentId, id.id as id, id.fullName as fullName from InvestorDependent id left join id.investor i where i.id = :id ")
    List<InvestorIdFullNameDto> findInvestorById(@Param("id")UUID id)
}
