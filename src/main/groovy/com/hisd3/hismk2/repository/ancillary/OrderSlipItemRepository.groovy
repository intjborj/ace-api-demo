package com.hisd3.hismk2.repository.ancillary

import com.hisd3.hismk2.domain.ancillary.OrderSlipItem
import com.hisd3.hismk2.domain.billing.Billing
import com.hisd3.hismk2.domain.billing.BillingItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface OrderSlipItemRepository extends JpaRepository<OrderSlipItem, UUID> {


    @Query(value = """
      Select orderSlipItem from OrderSlipItem orderSlipItem
      where  orderSlipItem.posted = true and orderSlipItem.transaction_type = 'CASH' and  orderSlipItem.billing_item.billing =:billing
      and orderSlipItem.billing_item.taggedOrNumber is null order by orderSlipItem.createdDate
""")
    List<OrderSlipItem> getCashBasisItems(@Param("billing") Billing billing)

    @Query(value = "Select osi from OrderSlipItem osi where osi.doctors_order_item.id =:doItemId")
    OrderSlipItem getOrderSlipItemByDOItem(@Param("doItemId") UUID doItemId)

    @Query(value = "Select orderSlipItem from OrderSlipItem orderSlipItem where orderSlipItem.orderslip.id =:orderSlipId and orderSlipItem.service.hideInPatientDiagnostics = false")
    List<OrderSlipItem> getByOrderSlip(@Param("orderSlipId") UUID orderSlipId)

    @Query(value = "Select orderSlipItem from OrderSlipItem orderSlipItem where orderSlipItem.orderslip.orderSlipNo =:orderSlipNo")
    List<OrderSlipItem> orderSlipItemsByOrderSlipNo(@Param("orderSlipNo") String orderSlipNo)

    @Query(value = """Select r from  OrderSlipItem r where r.posted = FALSE 
                      and (lower(r.service.serviceName || r.service.description ||  r.service.serviceCode) like lower(concat('%',:filter,'%')))
                      and r.orderslip.orderSlipNo =:orderSlipNo  
                      """)
    List<OrderSlipItem> getAllOrderSlipNoFalse(@Param("orderSlipNo") String orderSlipNo, @Param("filter") String filter)

    @Query(value = "Select orderSlipItem from OrderSlipItem orderSlipItem where orderSlipItem.orderslip.orderSlipNo =:orderSlipNo")
    List<OrderSlipItem> getByOrderSlipNo(@Param("orderSlipNo") String orderSlipNo)

    @Query(value = "Select orderSlipItem from OrderSlipItem orderSlipItem where orderSlipItem.itemNo =:itemNo")
    List<OrderSlipItem> getByOrderSlipItem(@Param("itemNo") String itemNo)

    @Query(value = """ Select  oi from OrderSlipItem oi  where oi.status like concat('%',:status,'%') and oi.orderslip.parentCase.registryType like concat('%',:ptype,'%')
					   and lower(oi.orderslip.parentCase.patient.fullName) like lower(concat('%',:filter,'%'))
					   and oi.createdDate >= :startDate and oi.createdDate <= :endDate """)

    List<OrderSlipItem> orderslipsByPatientType(
            @Param("ptype") String type,
            @Param("filter") String filter,
            @Param("status") String status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate)

    @Query(value = """ Select  oi from OrderSlipItem oi where (oi.service.department.parentDepartment.id=:department or oi.service.department.id=:department)  and oi.status like concat('%',:status,'%') and lower(oi.orderslip.parentCase.registryType) like lower(concat('%',:ptype,'%'))
					   and lower(oi.orderslip.parentCase.patient.fullName) like lower(concat('%',:filter,'%'))
					   and oi.createdDate >= :startDate and oi.createdDate <= :endDate  """)

    List<OrderSlipItem> orderslipsByPatientTypeWithDep(
            @Param("department") UUID department,
            @Param("ptype") String type,
            @Param("filter") String filter,
            @Param("status") String status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate)

    @Query(nativeQuery = true, value = """ Select  oi from OrderSlipItem oi
 					   left join service s on (s.id = oi.id)
 					   left join orderslip os on (os.id = oi.id)
 					   left join case cs on (cs.id = oi.id)
 					   where (oi.service.department.parentDepartment.id=:department or oi.service.department.id=:department)  and oi.status like concat('%',:status,'%') and lower(oi.orderslip.parentCase.registryType) like lower(concat('%',:ptype,'%'))
					   and lower(oi.orderslip.parentCase.patient.fullName) like lower(concat('%',:filter,'%'))
					   and oi.createdDate >= :startDate and oi.createdDate <= :endDate  """)

    List<OrderSlipItem> orderslipsByPatientTypeWithDepNative(
            @Param("department") UUID department,
            @Param("ptype") String type,
            @Param("filter") String filter,
            @Param("status") String status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate)

    @Query(value = ''' Select oi from OrderSlipItem oi where (oi.orderslip.parentCase.id=:caseId and coalesce(oi.service.department.parentDepartment.id,oi.service.department.id) =:department)''')
    List<OrderSlipItem> orderslipsByDepartment(@Param("caseId") UUID caseId, @Param("department") UUID department)

    @Query(value = ''' Select oi from OrderSlipItem oi where cast(oi.orderslip.parentCase.id as string) like concat('%',:caseId,'%') and cast(coalesce(oi.service.department.parentDepartment.id,oi.service.department.id) as string) like concat('%',:departmentId,'%')''')
    List<OrderSlipItem> orderslipsByDepartmentV2(@Param("caseId") String caseId,@Param("departmentId") String departmentId)

//    cast(rf.doctor.id as string) like concat('%',:empId,'%')



    @Query(value = '''Select oi from OrderSlipItem oi where oi.orderslip.parentCase.id=:caseId and oi.service.hideInPatientDiagnostics = false''')
    List<OrderSlipItem> findByCase(@Param("caseId") UUID caseId)

    @Query(value = "Select o from OrderSlipItem o where o.billing_item =:billing_item")
    List<OrderSlipItem> getByBillingItem(@Param("billing_item") BillingItem billing_item)

    @Query(value = ''' Select oi from OrderSlipItem oi where oi.service.department.parentDepartment.id=:department
 						and oi.orderslip.id =:orderSlip
	''')
    List<OrderSlipItem> getByOrderSlipItemsWithDepartment(@Param("department") UUID department, @Param("orderSlip") UUID orderSlip)

    @Query(value = ''' Select oi from OrderSlipItem oi where oi.accession=:accession ''')
    List<OrderSlipItem> findByAccession(@Param("accession") String accession)

    @Query(value = """ Select oi from OrderSlipItem oi where oi.status like concat('%',:status,'%')
						and (cast(oi.service.department.parentDepartment.id as string) like concat('%',:category,'%') or cast(oi.service.department.id as string) like concat('%',:category,'%') )
						and cast(oi.reader.id as string)  like concat('%',:reader,'%')
						and oi.createdDate >= :startDate and oi.createdDate <= :endDate """,
            countQuery = """
						Select count(oi) from OrderSlipItem oi where oi.status like concat('%',:status,'%')
						and (cast(oi.service.department.parentDepartment.id as string) like concat('%',:category,'%') or cast(oi.service.department.id as string) like concat('%',:category,'%') )
						and cast(oi.reader.id as string)  like concat('%',:reader,'%')
						and oi.createdDate >= :startDate and oi.createdDate <= :endDate
"""
    )
    Page<OrderSlipItem> getOrderSlipItemsByCategoryPage(
            @Param("category") String category,
            @Param("status") String status,
            @Param("reader") String reader,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable)

    @Query(value = """ Select oi from OrderSlipItem oi where oi.status like concat('%',:status,'%')
 						and (cast(oi.service.department.parentDepartment.id as string) like concat('%',:category,'%') or cast(oi.service.department.id as string) like concat('%',:category,'%'))
						and oi.createdDate >= :startDate and oi.createdDate <= :endDate""")
    List<OrderSlipItem> getAllSelectedResults(
            @Param("status") String status,
            @Param("category") String category,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate

    )

    @Query(value = """ Select oi from OrderSlipItem oi where oi.status like concat('%',:status,'%')
						and oi.billing_item is not null
 						and oi.createdDate >= :startDate and oi.createdDate <= :endDate""")
    List<OrderSlipItem> getAllCompletedByDate(
            @Param("status") String status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate

    )

    @Query(value = """ Select oi from OrderSlipItem oi where oi.billing_item is null
						and oi.posted <> true
						and oi.status <> 'CANCELLED'
						and oi.orderslip.parentCase.patient.fullName like concat('%',:filter,'%')
						and cast(coalesce(oi.service.department.parentDepartment.id,oi.service.department.id) as string) like concat('%',:departmentId,'%')""",

            countQuery = """ 
			Select count(oi) from OrderSlipItem oi where oi.billing_item is null
			and oi.posted <> true
			and oi.status <> 'CANCELLED'
			and oi.orderslip.parentCase.patient.fullName like concat('%',:filter,'%')
			and cast(coalesce(oi.service.department.parentDepartment.id,oi.service.department.id) as string) like concat('%',:departmentId,'%')
	"""
    )


    Page<OrderSlipItem> getAllUnbilledItems(
            @Param("filter") String filter,
            @Param("departmentId") String departmentId,
            Pageable pageable

    )

    @Query(value = '''
        Select oi from OrderSlipItem oi where 
        oi.orderslip.parentCase.id=:caseId and 
        oi.service.hideInPatientDiagnostics = false and
        oi.service.department.parentDepartment.departmentName = 'CLINICAL LABORATORY'
    ''')
    List<OrderSlipItem> findLabExamResultsByCase(@Param("caseId") UUID caseId)
}
