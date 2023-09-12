package com.hisd3.hismk2.dao

import com.hisd3.hismk2.domain.PersistentToken
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.repository.UserRepository
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@TypeChecked
@Service
@Transactional
class UserDao {
	
	@Autowired
	private UserRepository userRepository
	
	@PersistenceContext
	EntityManager entityManager
	
	/*List<Authority> getAuthorities(User user) {
		
		def mergedUser = entityManager.merge(user)
		mergedUser.authorities.size()
		return mergedUser.authorities as List
	}*/
	
	List<PersistentToken> getPersistentTokens(User user) {
		
		def mergedUser = entityManager.merge(user)
		mergedUser.persistentTokens.size()
		return mergedUser.persistentTokens as List
	}
	
	/*List<Permission> getPermissions(User user) {
		
		def mergedUser = entityManager.merge(user)
		mergedUser.permissions.size()
		return mergedUser.permissions as List
	}*/
	
	/*List<String> getRoles(User user) {
		
		def mergedUser = entityManager.merge(user)
		mergedUser.roles.size()
		return mergedUser.roles as List
	}*/
	
	/*List<String> getAccess(User user) {
		
		def mergedUser = entityManager.merge(user)
		mergedUser.access.size()
		return mergedUser.access as List
	}*/
}
