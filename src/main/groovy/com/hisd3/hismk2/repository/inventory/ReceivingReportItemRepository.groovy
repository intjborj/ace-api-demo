package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.ReceivingReport
import com.hisd3.hismk2.domain.inventory.ReceivingReportItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface ReceivingReportItemRepository extends JpaRepository<ReceivingReportItem, UUID> {
	
	@Query(value = "Select r from ReceivingReportItem r where r.receivingReport.id = :id")
	List<ReceivingReportItem> findItemsByReceivingReportId(@Param("id") UUID id)


	@Query(value = "Select coalesce(sum(r.totalAmount - r.inputTax),0) from ReceivingReportItem r where r.receivingReport.id = :id and r.item.isMedicine = :meds")
	BigDecimal getNetVatIn(@Param("id") UUID id, @Param("meds") Boolean meds)

	@Query(value = "Select coalesce(sum(r.totalAmount),0) from ReceivingReportItem r where r.receivingReport.id = :id and r.item.isMedicine = :meds")
	BigDecimal getNetVatEx(@Param("id") UUID id, @Param("meds") Boolean meds)


	@Query( value = """ Select rr from ReceivingReportItem rr where 
						(lower(rr.item.descLong) like lower(concat('%',:filter,'%'))) and
						rr.receivingReport.id =:id

					""",
			countQuery = """ Select count(rr) from ReceivingReportItem rr where
						(lower(rr.item.descLong) like lower(concat('%',:filter,'%')))  and
						rr.receivingReport.id =:id 			 
 				""")
	Page<ReceivingReportItem> getReceivingReportItem(@Param("id") UUID id ,@Param('filter') String filter, Pageable pageable)


	//code ni dons
	@Query(value = '''	Select r from ReceivingReportItem r
						where
						(r.receivingReport.isVoid = false OR r.receivingReport.isVoid is null) AND
						r.receivingReport.receiveDate >= :startDate AND
						r.receivingReport.receiveDate <= :endDate AND
						(lower(r.receivingReport.rrNo) like lower(concat('%',:filter,'%')) OR
						lower(r.item.descLong) like lower(concat('%',:filter,'%')) OR
						lower(r.receivingReport.supplier.supplierFullname) like lower(concat('%',:filter,'%')))
					 	ORDER BY r.receivingReport.receiveDate asc ''')
	List<ReceivingReportItem> getSrrItemByDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate, @Param("filter") String filter)

	@Query(value = '''	Select r from ReceivingReportItem r
						where
						(r.receivingReport.isVoid = false OR r.receivingReport.isVoid is null) AND
						r.receivingReport.receiveDate >= :startDate AND
						r.receivingReport.receiveDate <= :endDate AND
						r.receivingReport.supplier.id = :supplier AND
						(lower(r.receivingReport.rrNo) like lower(concat('%',:filter,'%')) OR
						lower(r.item.descLong) like lower(concat('%',:filter,'%')) OR
						lower(r.receivingReport.supplier.supplierFullname) like lower(concat('%',:filter,'%')))
					 	ORDER BY r.receivingReport.receiveDate asc ''')
	List<ReceivingReportItem> getSrrItemByDateRangeSupplier(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate, @Param("filter") String filter, @Param("supplier") UUID supplier)


	@Query(value = '''Select r from ReceivingReportItem r where r.receivingReport.receiveDepartment.id = :depId
					and (lower(r.receivingReport.rrNo) like lower(concat('%',:filter,'%')) or 
					lower(r.item.descLong) like lower(concat('%',:filter,'%')) or 
					lower(r.lotNo) like lower(concat('%',:filter,'%')))
					and r.expirationDate is not null and (r.item.consignment is false or r.item.consignment is null) 
					and r.item.active = true 
					and to_date(to_char(r.expirationDate, 'YYYY-MM-DD'),'YYYY-MM-DD') > to_date(:dateNow,'YYYY-MM-DD')''',
			countQuery = '''Select count(r) from ReceivingReportItem r where r.receivingReport.receiveDepartment.id = :depId
					and (lower(r.receivingReport.rrNo) like lower(concat('%',:filter,'%')) or 
					lower(r.item.descLong) like lower(concat('%',:filter,'%')) or 
					lower(r.lotNo) like lower(concat('%',:filter,'%')))
					and r.expirationDate is not null and (r.item.consignment is false or r.item.consignment is null) 
					and r.item.active = true 
					and to_date(to_char(r.expirationDate, 'YYYY-MM-DD'),'YYYY-MM-DD') > to_date(:dateNow,'YYYY-MM-DD')''')
	Page<ReceivingReportItem> getItemsWithExpiry(@Param("depId") UUID depId,
												 @Param("dateNow") String dateNow,
												 @Param("filter") String filter,
												 Pageable pageable)

	@Query(value = '''Select r from ReceivingReportItem r where r.receivingReport.receiveDepartment.id = :depId
					and (lower(r.receivingReport.rrNo) like lower(concat('%',:filter,'%')) or 
					lower(r.item.descLong) like lower(concat('%',:filter,'%')) or 
					lower(r.lotNo) like lower(concat('%',:filter,'%')))
					and r.expirationDate is not null and (r.item.consignment is false or r.item.consignment is null) 
					and r.item.active = true 
					and to_date(to_char(r.expirationDate, 'YYYY-MM-DD'),'YYYY-MM-DD') <= to_date(:dateNow,'YYYY-MM-DD')''',
			countQuery = '''Select count(r) from ReceivingReportItem r where r.receivingReport.receiveDepartment.id = :depId
					and (lower(r.receivingReport.rrNo) like lower(concat('%',:filter,'%')) or 
					lower(r.item.descLong) like lower(concat('%',:filter,'%')) or 
					lower(r.lotNo) like lower(concat('%',:filter,'%')))
					and r.expirationDate is not null and (r.item.consignment is false or r.item.consignment is null) 
					and r.item.active = true 
					and to_date(to_char(r.expirationDate, 'YYYY-MM-DD'),'YYYY-MM-DD') <= to_date(:dateNow,'YYYY-MM-DD')''')
	Page<ReceivingReportItem> getItemsWithExpired(@Param("depId") UUID depId,
												 @Param("dateNow") String dateNow,
												  @Param("filter") String filter,
												 Pageable pageable)


}
