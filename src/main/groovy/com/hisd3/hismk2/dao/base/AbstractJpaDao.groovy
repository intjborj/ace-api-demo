package com.hisd3.hismk2.dao.base

import org.hibernate.annotations.Sort
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.Query
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root
import org.springframework.data.domain.Sort

@Transactional
abstract class AbstractJpaDao<T extends Serializable> {
	
	private Class<T> clazz
	
	private CriteriaBuilder _cb
	
	@PersistenceContext
	EntityManager entityManager
	
	void setClazz(Class<T> clazzToSet) {
		this.clazz = clazzToSet
	}
	
	CriteriaBuilder getCb() {
		if (_cb)
			_cb
		else
			_cb = entityManager.getCriteriaBuilder()
	}
	
	// ============Criteria Builders============
	
	CriteriaQuery<T> getCq() {
		cb.createQuery(clazz)
	}
	
	CriteriaQuery<Long> getCntq() {
		cb.createQuery(Long.class)
	}
	
	Root<T> getRoot(CriteriaQuery<T> ct) {
		ct.from(clazz)
	}
	
	TypedQuery<T> createTypeQuery(CriteriaQuery<T> criteriaQuery) {
		entityManager.createQuery(criteriaQuery)
	}
	
	TypedQuery<Long> createCountTypeQuery(CriteriaQuery<Long> criteriaQuery) {
		entityManager.createQuery(criteriaQuery)
	}
	// ============Criteria Builders============
	
	// ==============JPQl =====================
	
	Query createGenericQuery(String jpql) {
		entityManager.createQuery(jpql)
	}
	
	TypedQuery<T> createQuery(String jpql, Class<T> aClass) {
		entityManager.createQuery(jpql, aClass)
	}
	
	TypedQuery<T> createQuery(String jpql) {
		createQuery(jpql, clazz)
	}
	
	TypedQuery<Long> createCountQuery(String jpql) {
		entityManager.createQuery(jpql, Long.class)
	}
	
	//code ni Wilson
	TypedQuery<BigDecimal> createSumQuery(String jpql) {
		entityManager.createQuery(jpql, BigDecimal.class)
	}
	
	TypedQuery<T> createQuery(String jpql, Map<String, Object> params) {
		createQuery(jpql)
				.tap {
			cq ->
				params.each {
					key, val ->
						cq.setParameter(key, val)
				}
		}
	}
	
	Long getCount(String jpql, Map<String, Object> params) {
		createCountQuery(jpql).tap {
			cq ->
				params.each {
					key, val ->
						cq.setParameter(key, val)
				}
		}.singleResult
	}
	
	//code ni wilson
	BigDecimal getSum(String jpql, Map<String, Object> params) {
		createSumQuery(jpql).tap {
			cq ->
				params.each {
					key, val ->
						cq.setParameter(key, val)
				}
		}.singleResult
	}
	
	Page<T> getPageable(String queryStr, String countQueryStr, int page, int size, Map<String, Object> params) {
		
		def query = createQuery(queryStr, params)
		def count = getCount(countQueryStr, params)
		
		new PageImpl<T>(query.setFirstResult(page * size)
				.setMaxResults(size).resultList, PageRequest.of(page, size),
				count)
		
	}


	//====================Common Method ======================
	
	T findOne(UUID id) {
		entityManager.find(clazz, id)
	}
	
	List<T> findAll() {
		entityManager.createQuery("from " + clazz.getName())
				.getResultList()
	}
	
	T save(T entity) {
		
		if (entityManager.contains(entity))
			return entityManager.merge(entity)
		else {
			entityManager.persist(entity)
			return entity
		}
	}
	
	void delete(T entity) {
		entityManager.remove(entity)
	}
	
	void deleteById(UUID entityId) {
		T entity = findOne(entityId)
		delete(entity)
	}
}
