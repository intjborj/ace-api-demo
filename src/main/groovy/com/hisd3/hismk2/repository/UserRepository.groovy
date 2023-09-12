package com.hisd3.hismk2.repository

import com.hisd3.hismk2.domain.Authority
import com.hisd3.hismk2.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository extends JpaRepository<User, Long> {
	
	User findOneByLogin(@Param("login") String login)
	
	@Query(value = 'select u from User u where :role in u.authorities')
	List<User> findUserByRole(@Param('role') Authority role)
	
	@Query(value = 'SELECT DISTINCT u  from User u LEFT JOIN u.authorities authorities where authorities in  :roles')
	List<User> findUserByRoles(@Param('roles') List<Authority> roles)
	
}
