package com.hisd3.hismk2.dao.base

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

import javax.persistence.Query
import javax.persistence.TypedQuery

interface IGenericDao<T extends Serializable> {
	
	void setClazz(Class<T> clazzToSet)
	
	T findOne(final UUID id)
	
	List<T> findAll()
	
	T save(final T entity)
	
	void delete(final T entity)
	
	void deleteById(final UUID entityId)
	
	Query createGenericQuery(String jpql)
	
	TypedQuery<T> createQuery(String jpql, Class<T> aClass)
	
	TypedQuery<T> createQuery(String jpql)
	
	TypedQuery<Long> createCountQuery(String jpql)
	
	TypedQuery<T> createQuery(String jpql, Map<String, Object> params)
	
	Long getCount(String jpql, Map<String, Object> params)
	
	BigDecimal getSum(String jpql, Map<String, Object> params)
	
	Page<T> getPageable(String queryStr, String countQueryStr, int page, int size, Map<String, Object> params)
}
