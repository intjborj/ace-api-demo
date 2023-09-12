package com.hisd3.hismk2.repository.eclaims

import com.hisd3.hismk2.domain.eclaims.EclaimsCaseRef
import com.hisd3.hismk2.domain.eclaims.EclaimsIntegrationAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EcCaseRefRepository extends JpaRepository<EclaimsCaseRef, UUID> {
	
	@Query(value = '''Select es from EclaimsCaseRef es where es.ptCase.id=:id ''')
	EclaimsCaseRef findByCase(@Param("id") UUID id)
}
