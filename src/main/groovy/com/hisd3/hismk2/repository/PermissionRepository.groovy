package com.hisd3.hismk2.repository

import com.hisd3.hismk2.domain.Permission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param

interface PermissionRepository extends JpaRepository<Permission, UUID> {
	
	Permission findOneByName(@Param("name") String name)

	List<Permission> findByNameIn(@Param("name")List<String> name)
}