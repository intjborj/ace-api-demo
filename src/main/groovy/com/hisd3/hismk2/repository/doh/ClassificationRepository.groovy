package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.Classification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ClassificationRepository extends JpaRepository<Classification, UUID> {
	@Query(value = "select c from Classification c")
	List<Classification> findAllClassification()
}
