package com.hisd3.hismk2.repository.ancillary

import com.hisd3.hismk2.domain.ancillary.Orderslip
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface OrderslipRepository extends JpaRepository<Orderslip, UUID> {

	@Query(value = ''' Select o from Orderslip o where lower(o.parentCase.patient.fullName) like lower(concat('%',:filter,'%'))
					
					''')
	List<Orderslip> filterByPatient(@Param("filter") String filter)

	@Query(value = "Select o from Orderslip o where lower(o.parentCase.patient.fullName) like lower(concat('%',:filter,'%')) and o.parentCase.registryType =:patientType")
	List<Orderslip> filterByPatientType(@Param("patientType") String patientType, @Param("filter") String filter)

	@Query(value = "Select orderSlip from Orderslip orderSlip where orderSlip.parentCase.id = :parentCase order by orderSlip.orderSlipNo desc")
	List<Orderslip> getOrderslipsByCase(@Param("parentCase") UUID parentCase)

	@Query(value = """ Select orderSlip from Orderslip orderSlip where orderSlip.orderSlipNo = :orderSlipNo order by orderSlip.orderSlipNo desc

	""")
	List<Orderslip> getByOrderSlipNo(@Param("orderSlipNo") String orderSlipNo)

	@Query(value = "Select orderSlip from Orderslip orderSlip where orderSlip.department.id = :departmentId order by orderSlip.orderSlipNo desc")
	List<Orderslip> getByOrderSlipDepartment(@Param("departmentId") UUID departmentId)

	@Query(value = "Select os from Orderslip os where os.department.id = :departmentId and os.parentCase.id = :caseId order by os.orderSlipNo desc")
	List<Orderslip> orderslipsByDepartment(@Param("departmentId") UUID departmentId,@Param("caseId") UUID caseId)

	@Query(value = "Select os from Orderslip os where os.parentCase.id = :caseId order by os.orderSlipNo desc")
	List<Orderslip> findByCase(@Param("caseId") UUID caseId)

	// For Ancillary Tasklist Query

	@Query(value = """
  Select o.id  from Orderslip o
 			
                 left join   o.orderSlipItemList as oi 
                 join     oi.service svc
                 join     svc.department dpt
                 left join     dpt.parentDepartment pdpt
             
                  where
                      oi.status like concat('%',:status,'%')
                      and
                      (  
                        pdpt.id =:department or dpt.id = :department  or :department =  'b87f2eaa-4f03-4700-af7a-c8cd2cf65fc0'  
                      )
			      and oi.createdDate >= :startDate and oi.createdDate <= :endDate
                  and o.parentCase.registryType like concat('%',:ptype,'%')
                  and  lower(o.parentCase.patient.fullName) like lower(concat('%',:filter,'%'))
                  group by o.id
                  order by max(o.createdDate) desc
""", countQuery = """
 Select count( distinct o.id) from Orderslip o
 			
                  left join   o.orderSlipItemList as oi
                  join   oi.service svc
                  join   svc.department dpt
                  left join   dpt.parentDepartment pdpt
                  where
                   oi.status like concat('%',:status,'%')
                  and
                       (  
                         pdpt.id =:department or dpt.id = :department  or :department =  'b87f2eaa-4f03-4700-af7a-c8cd2cf65fc0'  
                      )
			      and oi.createdDate >= :startDate and oi.createdDate <= :endDate
                  and o.parentCase.registryType like concat('%',:ptype,'%')
                  and  lower(o.parentCase.patient.fullName) like lower(concat('%',:filter,'%'))
                  
                  
""")
	Page<UUID> orderslipsByPatientType2(
			@Param("department") UUID department,
			@Param("ptype") String type,
			@Param("filter") String filter,
			@Param("status") String status,
			@Param("startDate") Instant startDate,
			@Param("endDate") Instant endDate,
			Pageable pageable)

	@Query(value = """
 Select  distinct o from Orderslip o
 			
                 left join fetch o.orderSlipItemList as oi 
                 join  fetch  oi.service svc
                 join  fetch  svc.department dpt
                 left join  fetch  dpt.parentDepartment pdpt
             
                  where
                      oi.status like concat('%',:status,'%')
                      and
                        (  
                         pdpt.id =:department or dpt.id = :department  or :department =  'b87f2eaa-4f03-4700-af7a-c8cd2cf65fc0'  
                      )
			      and oi.createdDate >= :startDate and oi.createdDate <= :endDate
                  and o.id in :ids
                  order by o.createdDate DESC
                  
""")
	List<Orderslip> orderslipsByPatientTypeActual2(
			@Param("department") UUID department,
			@Param("status") String status,
			@Param("startDate") Instant startDate,
			@Param("endDate") Instant endDate,
			@Param("ids") List<UUID> ids)





// =================== OLD RECORDS =========
// For Reference only for Study
@Query(value = """
 Select    o.id from Orderslip o
 			
                 left join o.orderSlipItemList oi
                  where
                  oi.status like concat('%',:status,'%')
                  and cast(coalesce(oi.service.department.parentDepartment.id,oi.service.department.id) as string)  like concat('%',:department,'%')
			      and oi.createdDate >= :startDate and oi.createdDate <= :endDate
                  and o.parentCase.registryType like concat('%',:ptype,'%')
                  and  lower(o.parentCase.patient.fullName) like lower(concat('%',:filter,'%'))
                  group by o.id
                  order by max(o.createdDate) desc
                  
""", countQuery = """
 Select count( distinct o.id) from Orderslip o
 			
                  left join  o.orderSlipItemList oi
                  where
                  oi.status like concat('%',:status,'%')
                  and cast(coalesce(oi.service.department.parentDepartment.id,oi.service.department.id) as string)  like concat('%',:department,'%')
			      and oi.createdDate >= :startDate and oi.createdDate <= :endDate
                  and o.parentCase.registryType like concat('%',:ptype,'%')
                  and  lower(o.parentCase.patient.fullName) like lower(concat('%',:filter,'%'))
                  
                  
""")
	Page<UUID> orderslipsByPatientType(
			@Param("department") String department,
			@Param("ptype") String type,
			@Param("filter") String filter,
			@Param("status") String status,
			@Param("startDate") Instant startDate,
			@Param("endDate") Instant endDate,
			Pageable pageable)

	@Query(value = """
 Select  distinct o from Orderslip o
 			
                 left join fetch  o.orderSlipItemList oi
                  where
                 
                  oi.status like concat('%',:status,'%')
                  and 
                  	cast(coalesce(oi.service.department.parentDepartment.id,oi.service.department.id) as string)  like concat('%',:department,'%')
                       
			      and oi.createdDate >= :startDate and oi.createdDate <= :endDate
                  and o.id in :ids
                  order by o.createdDate DESC
                  
""")
	List<Orderslip> orderslipsByPatientTypeActual(
			@Param("department") String department,
			@Param("status") String status,
			@Param("startDate") Instant startDate,
			@Param("endDate") Instant endDate,
			@Param("ids") List<UUID> ids)
}
