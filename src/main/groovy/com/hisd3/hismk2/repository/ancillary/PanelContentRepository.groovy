package com.hisd3.hismk2.repository.ancillary

import com.hisd3.hismk2.domain.ancillary.PanelContent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PanelContentRepository extends JpaRepository<PanelContent, UUID> {
	
	@Query(
			value = "Select p from PanelContent p where p.parent.id =:id"
	)
	List<PanelContent> findAllbyParentId(@Param("id") UUID id)
}
