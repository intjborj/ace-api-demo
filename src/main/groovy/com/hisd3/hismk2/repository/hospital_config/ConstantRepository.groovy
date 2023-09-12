package com.hisd3.hismk2.repository.hospital_config

import com.hisd3.hismk2.domain.hospital_config.Constant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ConstantRepository extends JpaRepository<Constant, UUID> {
	
	@Query("select c from Constant c where c.type.id = :filter")
	List<Constant> findByFilter(@Param("filter") UUID filter)
	
	@Query("select c from Constant c where c.type.name = :name")
	List<Constant> findByName(@Param("name") String name)

	@Query("select c from Constant c where c.name in :name")
	List<Constant> findByNames(@Param("name") List<String> name)

	@Query("select c from Constant c where c.name = :name")
	Constant findByConstantName(@Param("name") String name)

	@Query("select c from Constant c where c.type.id = :type and c.status is true")
	List<Constant>  findActiveByType(@Param("type") UUID type)


}