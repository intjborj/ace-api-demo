package com.hisd3.hismk2.repository.ancillary

import com.hisd3.hismk2.domain.ancillary.AncillaryConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AncillaryConfigRepository extends JpaRepository<AncillaryConfig, UUID> {
	
	@Query(value = '''Select a from AncillaryConfig a where
            lower(a.entityName) like concat('%',:name,'%') ''')
	List<AncillaryConfig> getConfigByEntityName(@Param("name") String filter)
	
	@Query(value = '''Select a from AncillaryConfig a where
            lower(a.entityName) like concat('%',:name,'%') ''')
	List<AncillaryConfig> getConfigByDepartment(@Param("name") String filter)
	
}


