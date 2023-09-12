package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.PhysicalCount
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface PhysicalCountRepository extends JpaRepository<PhysicalCount, UUID> {
	
	@Query(value = "select q from PhysicalCount q where q.dateTrans = :date AND (lower(q.item.descLong) like lower(concat('%',:filter,'%')) or lower(q.item.barcode) like lower(concat('%',:filter,'%'))) ")
	List<PhysicalCount> getMonthlyCountByDate(@Param('date') Instant date, @Param('filter') String filter)

	@Query(value = "select q from PhysicalCount q where q.item.id = :item and q.department.id = :id and q.dateTrans = :date")
	PhysicalCount getPhysicalCountObject(@Param('item') UUID item, @Param('id') UUID id, @Param('date') Instant date)
	
	@Query(value = "select q from PhysicalCount q where q.dateTrans = :date AND q.department.id = :dep AND (lower(q.item.descLong) like lower(concat('%',:filter,'%')) or lower(q.item.sku) like lower(concat('%',:filter,'%')))")
	List<PhysicalCount> getMonthlyCountByDateAndDep(@Param('date') Instant date, @Param('dep') UUID dep, @Param('filter') String filter)
	
	@Query(value = "select q from PhysicalCount q where q.dateTrans = :date AND q.department.id = :dep AND q.isPosted = false AND q.isCancel = false")
	List<PhysicalCount> getMonthlyCountByDateAndDepWhereNotPosted(@Param('date') Instant date, @Param('dep') UUID dep)
	
	@Query(value = "select q from PhysicalCount q where q.dateTrans = :date AND q.department.id = :dep AND (select COALESCE(sum(p.count), 0) from LogsCount p where p.physicalCount.id = q.id) > 0 AND q.isPosted = false AND q.isCancel = false")
	List<PhysicalCount> getMonthlyCountByDateAndDepWhereCounted(@Param('date') Instant date, @Param('dep') UUID dep)
	
	@Query(value = "select q from PhysicalCount q where q.dateTrans = :date AND q.department.id = :dep AND q.variance = 0 AND q.isPosted = false AND q.isCancel = false")
	List<PhysicalCount> getMonthlyCountByDateAndDepWhereVarianceZero(@Param('date') Instant date, @Param('dep') UUID dep)
	
	//sum
	@Query(value = "select COALESCE(sum(q.count), 0) from LogsCount q where q.physicalCount.id = :id")
	Integer getMonthlyCount(@Param('id') UUID id)
	
	// -- Pageable -- //
	@Query(value = "select q from PhysicalCount q where q.dateTrans = :date AND q.department.id = :dep AND (lower(q.item.descLong) like lower(concat('%',:filter,'%')) or lower(q.item.sku) like lower(concat('%',:filter,'%')))",
			countQuery = "Select count (q) from PhysicalCount q where q.dateTrans = :date AND q.department.id = :dep AND (lower(q.item.descLong) like lower(concat('%',:filter,'%')) or lower(q.item.sku) like lower(concat('%',:filter,'%')))")
	Page<PhysicalCount> getMonthlyCountByDateAndDepPaged(@Param('date') Instant date, @Param('dep') UUID dep, @Param('filter') String filter, Pageable page)
	
	@Query(value = "select q from PhysicalCount q where q.dateTrans = :date AND (lower(q.item.descLong) like lower(concat('%',:filter,'%')) or lower(q.item.barcode) like lower(concat('%',:filter,'%'))) ",
			countQuery = "Select count (q) from PhysicalCount q where q.dateTrans = :date AND (lower(q.item.descLong) like lower(concat('%',:filter,'%')) or lower(q.item.barcode) like lower(concat('%',:filter,'%')))")
	Page<PhysicalCount> getMonthlyCountByDatePaged(@Param('date') Instant date, @Param('filter') String, Pageable page)
	// -- end: Pageable -- //

	// wilson update
	@Query(value = "select q from PhysicalCount q where q.physicalTransaction.id = :id")
	List<PhysicalCount> getPhysicalItemsByTransaction(@Param('id') UUID id)

	@Query(value = "select q from PhysicalCount q where q.physicalTransaction.id = :id and q.isPosted = true")
	List<PhysicalCount> getPhysicalItemsPosted(@Param('id') UUID id)
}
