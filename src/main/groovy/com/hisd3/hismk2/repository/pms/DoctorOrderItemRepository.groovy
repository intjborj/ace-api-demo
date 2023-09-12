package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.DoctorOrderItem
import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@JaversSpringDataAuditable
interface DoctorOrderItemRepository extends JpaRepository<DoctorOrderItem, UUID> {
	
	@Query(nativeQuery = true, value = 'Select * from pms.doctor_order_items doctorOrderItem where doctorOrderItem.doctor_order = :doctorOrder and doctorOrderItem.hidden is null order by doctorOrderItem.created_date asc')
	List<DoctorOrderItem> getDoctorOrderItemsByDoctorOrder(@Param("doctorOrder") UUID doctorOrder)
	
	@Query(value = "Select doctorOrderItem from DoctorOrderItem doctorOrderItem where doctorOrderItem.doctorOrder.id = :doctorOrder and doctorOrderItem.type = 'SERVICE' and (doctorOrderItem.hidden is null or doctorOrderItem.hidden is empty)")
	List<DoctorOrderItem> getProcedureDoctorOrderItemsByDoctorOrder(@Param("doctorOrder") UUID doctorOrder)
}
