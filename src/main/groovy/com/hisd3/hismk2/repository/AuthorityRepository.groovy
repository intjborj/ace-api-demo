package com.hisd3.hismk2.repository

import com.hisd3.hismk2.domain.Authority
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param

interface AuthorityRepository extends JpaRepository<Authority, UUID> {
	
	Authority findOneByName(@Param("name") String name)

	List<Authority> findByNameIn(@Param("name")List<String> name)
	
}