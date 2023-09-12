package com.hisd3.hismk2.repository.referential

import com.hisd3.hismk2.domain.referential.DohPosition
import groovy.transform.TypeChecked
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@TypeChecked
interface DohPositionRepository extends JpaRepository<DohPosition, UUID> {
	
	@Query(value = "SELECT c FROM DohPosition c")
	List<DohPosition> getDOHPositions()
	
	@Query(value = "SELECT c FROM DohPosition c WHERE c.postdesc = :postDesc")
	List<DohPosition> getDOHPositionByPostDesc(@Param("postDesc") String postDesc)

    @Query(value = "SELECT c FROM DohPosition c WHERE c.isOthers = true")
    List<DohPosition> getOtherPositions()
	
}
