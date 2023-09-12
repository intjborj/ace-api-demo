package com.hisd3.hismk2.graphqlservices.ancillary

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.dao.ancillary.OrderslipDao
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.ancillary.DicNumber
import com.hisd3.hismk2.domain.ancillary.OrderSlipItem
import com.hisd3.hismk2.domain.ancillary.Orderslip
import com.hisd3.hismk2.domain.ancillary.Service
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.DoctorOrderItem
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.ancillary.DicNumberRepository
import com.hisd3.hismk2.repository.ancillary.OrderSlipItemRepository
import com.hisd3.hismk2.repository.ancillary.OrderslipRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.pms.DoctorOrderItemRepository
import com.hisd3.hismk2.services.GeneratorService
import groovy.transform.Canonical
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.hibernate.query.NativeQuery
import org.hibernate.query.Query
import org.hibernate.transform.Transformers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.Instant

@Canonical
class OrderSlipDto {
	String description
	String cnt
	Instant created_date
	String billing_item
	String item_no
	String requestingPhysicianName
}



@TypeChecked
@Component
@GraphQLApi
class OrderslipService {

	@Autowired
	private OrderslipRepository orderslipRepository

	@Autowired
	private OrderSlipItemRepository orderSlipItemRepository

	@Autowired
	private EmployeeRepository employeeRepository

	@Autowired
	OrderslipDao orderslipDao

	@Autowired
	DicNumberRepository dicNumberRepository

	@Autowired
	GeneratorService generatorService

	@Autowired
	private DoctorOrderItemRepository doctorOrderItemRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	EntityManager entityManager

	//============== All Queries ====================
	@GraphQLQuery(name = "TestString", description = "Get All Orderslips")
	String testString() {
		return "Success"
	}

	@GraphQLQuery(name = "orderslips", description = "Get All Orderslips")
	List<Orderslip> findAll() {
		orderslipRepository.findAll()
	}

	@GraphQLQuery(name = "orderSlip", description = "Get OrderSlip By Id")
	Orderslip findById(@GraphQLArgument(name = "id") UUID id) {
		return orderslipRepository.findById(id).get()
	}

	@GraphQLQuery(name = "orderslipsByPatientTypeByPage", description = "Get All Orderslips by Filters By Page")
	Page<Orderslip> orderslipsByPatientTypeByPage(@GraphQLArgument(name = "department") String department = "",
												  @GraphQLArgument(name = "ptype") String ptype = "",
												  @GraphQLArgument(name = "filter") String filter = "",
												  @GraphQLArgument(name = "status") String status = "",
												  @GraphQLArgument(name = "start") String start,
												  @GraphQLArgument(name = "end") String end,
												  @GraphQLArgument(name = "page") Integer page,
												  @GraphQLArgument(name = "pageSize") Integer pageSize)

	{

		Pageable pageable = PageRequest.of(page?:0, pageSize) //,Sort.Direction.DESC, 'createdDate'

		Timestamp startTimeS = Timestamp.valueOf(start)
		Timestamp endTimeS = Timestamp.valueOf(end)

		Instant startDate = startTimeS.toInstant()
		Instant endDate = endTimeS.toInstant()




		// to prevent HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!

		def pageIds = orderslipRepository.orderslipsByPatientType2(StringUtils.isNotBlank(department) ? UUID.fromString(department):
				UUID.fromString("b87f2eaa-4f03-4700-af7a-c8cd2cf65fc0"),
				ptype, filter, status, startDate, endDate, pageable)

		def ids = pageIds.content //.collect {  it [0] as UUID  }

		if (ids.size() == 0) {
			// in hql : "in clause does not accept empty list"
			return new PageImpl<Orderslip>([], pageIds.pageable, pageIds.totalElements)
		}

		def actualRecords = orderslipRepository.orderslipsByPatientTypeActual2(
				StringUtils.isNotBlank(department) ? UUID.fromString(department):
						UUID.fromString("b87f2eaa-4f03-4700-af7a-c8cd2cf65fc0"), status, startDate, endDate,
				ids
		)

		return new PageImpl<Orderslip>(actualRecords, pageIds.pageable, pageIds.totalElements)

	}

	@GraphQLQuery(name = "orderSlipsByCase", description = "Get All Order Slips by Case")
	List<Orderslip> findByCase(@GraphQLArgument(name = "id") UUID id) {
		orderslipRepository.getOrderslipsByCase(id)
	}

	@GraphQLQuery(name = "getDiagnosticExamsByCase", description = "Get All Order Slips by Case")
	List<Service> getDiagnosticExamsByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
		def results = orderSlipItemRepository.findLabExamResultsByCase(caseId)

		List<Service> listOfExams = [];
		results.each {
			if(it.service != null) {
				listOfExams.add(it.service)
			}
		}

		return listOfExams.unique().sort { it -> it.serviceName };
	}

	@GraphQLQuery(name = "orderSlipItemsByOrderSlipNo", description = "Get All Orderslips by OrderSlip No")
	List<OrderSlipItem> orderSlipItemsByOrderSlipNo(@GraphQLArgument(name = "orderSlipNo") String orderSlipNo) {
		def res = orderSlipItemRepository.orderSlipItemsByOrderSlipNo(orderSlipNo).sort { it.createdDate }.reverse(true)

		return res
	}

	@GraphQLQuery(name = "getAllOrderSlipNoFalse", description = "get all unbilled orderSlip ")
	List<OrderSlipItem> getAllOrderSlipNoFalse(
				@GraphQLArgument(name ="orderSlipNo") String orderSlipNo,
				@GraphQLArgument(name = "filter") String filter
		){
		def  res = orderSlipItemRepository.getAllOrderSlipNoFalse(orderSlipNo, filter?:"").sort{it.createdDate}
		return  res
	}

	@GraphQLQuery(name = "orderSlipsByNo", description = "Get All Orderslips by OrderSlip No")
	List<Orderslip> getByOrderSlipNo(@GraphQLArgument(name = "orderSlipNo") String orderSlipNo) {
		def res = orderslipRepository.getByOrderSlipNo(orderSlipNo)

		return res
	}

	@GraphQLQuery(name = "orderSlipsByNoAndDepartment", description = "Get All Orderslips by OrderSlip No and User Department")
	List<OrderSlipItem> getByOrderSlipItemsWithDepartment(@GraphQLArgument(name = "department") UUID department, @GraphQLArgument(name = "orderSlip") UUID orderSlip) {
		orderSlipItemRepository.getByOrderSlipItemsWithDepartment(department, orderSlip)
	}

	@GraphQLQuery(name = "getDicNumber", description = "Get Dic")
	List<DicNumber> getDicNumber(@GraphQLArgument(name = "department") String department, @GraphQLArgument(name = "patientId") String patientId) {

		if (department && patientId)
			return dicNumberRepository.getAltenaneNumber(UUID.fromString(department), UUID.fromString(patientId))
		else
			return []
	}

	@GraphQLQuery(name = "orderSlipsByDepartment", description = "Get All OrderslipItem Items by Department")
	List<OrderSlipItem> getByOrderSlipDepartment(@GraphQLArgument(name = "caseId") String caseId, @GraphQLArgument(name = "departmentId") String departmentId) {

		orderslipDao.orderSlipsByDepartment(caseId, departmentId)
	}

	@GraphQLQuery(name = "orderSlipsByDepartment_old", description = "Get All Orderslip Items by Department")
	List<Orderslip> orderSlipsByDepartment_old(@GraphQLArgument(name = "caseId") String caseId, @GraphQLArgument(name = "departmentId") String departmentId) {
		orderslipDao.orderSlipsByDepartment_old(caseId, departmentId)
	}

	@GraphQLQuery(name = "getOrderSlipItems", description = "Get  OrderSlip Items")
	List<OrderSlipItem> getOrderSlipItems(@GraphQLArgument(name = "id") UUID id) {
		if (id) {
			return orderSlipItemRepository.getByOrderSlip(id).sort { it.createdDate }
		}
	}

	@GraphQLQuery(name = "orderSlipItems", description = "Get all OrderSlip Items")
	List<OrderSlipItem> getOrderSlipItems(@GraphQLContext Orderslip orderslip) {
		orderSlipItemRepository.getByOrderSlip(orderslip.id).sort { it.createdDate }
	}

	@GraphQLQuery(name = "orderSlipItemsByCategory", description = "Get all OrderSlip Items")
	Page<OrderSlipItem> getOrderSlipItemsByCategory(
			@GraphQLArgument(name = "category") String category = "",
			@GraphQLArgument(name = "status") String status = "",
			@GraphQLArgument(name = "readerId") String reader = "",
			@GraphQLArgument(name = "start") String start,
			@GraphQLArgument(name = "end") String end,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize) {
		Pageable pageable = PageRequest.of(page, pageSize) //,Sort.Direction.DESC, 'createdDate'

		Timestamp startTimeS = Timestamp.valueOf(start)
		Timestamp endTimeS = Timestamp.valueOf(end)

		Instant startDate = startTimeS.toInstant()
		Instant endDate = endTimeS.toInstant()

		orderSlipItemRepository.getOrderSlipItemsByCategoryPage(category, status, reader, startDate, endDate, pageable)
	}

	@GraphQLQuery(name = "unbilledOrderslipItems", description = "Get all Unbilled OrderSlip Items")
	Page<OrderSlipItem> unbilledOrderslipItems(
			@GraphQLArgument(name = "filter") String filter = "",
			@GraphQLArgument(name = "departmentId") String departmentId = "",
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		Pageable pageable = PageRequest.of(page, pageSize,Sort.Direction.DESC, 'createdDate')

		orderSlipItemRepository.getAllUnbilledItems(filter,departmentId,pageable)
	}
	//============== All Mutations ====================

	@GraphQLMutation
	@Transactional(rollbackFor = Exception.class)
	List<OrderSlipItem> addOrderslip(
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {

		List<OrderSlipItem> orderslipItems = []
		def oSlip = objectMapper.convertValue(fields.get("orderSlip"), Orderslip) as Orderslip
		def doctorsOrderItemId = objectMapper.convertValue(fields.get("doctorsOrderItemId"), DoctorOrderItem) as DoctorOrderItem

		def requested = fields.get("requested") as List<Service>
		requested.each {
			it ->
				def serviceItems = objectMapper.convertValue(it, Service)
				OrderSlipItem order = new OrderSlipItem()
				order.service = serviceItems
				order.stat = serviceItems.stat
				order.posted = false
				order.status = "NEW"
				order.deleted = false
				order.doctors_order_item = doctorsOrderItemId
				if(serviceItems.packageItems){
					order.packageItems = serviceItems.packageItems
				}
				orderslipItems.add(order)


		}

		return orderslipDao.insertOrderTransaction(oSlip, orderslipItems)
	}

	@GraphQLMutation
	List<OrderSlipItem> addOrderslipFromOrder(
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {

		List<OrderSlipItem> orderslipItems = []
		def oSlip = objectMapper.convertValue(fields.get("orderSlip"), Orderslip) as Orderslip

		def requested = fields.get("requested") as List<Object>
		requested.each {
			it ->
				OrderSlipItem order = new OrderSlipItem()
				order.doctors_order_item = doctorOrderItemRepository.findById(UUID.fromString(it['doctors_order_item'] as String)).get()

				def serviceItems = objectMapper.convertValue(it, Service)
				order.service = serviceItems
				order.stat = serviceItems.stat
				order.posted = false
				order.status = "NEW"
				order.deleted = false
				orderslipItems.add(order)
		}

		return orderslipDao.insertOrderTransaction(oSlip, orderslipItems)
	}

	@GraphQLMutation(name = "updateOrderSlipItemStatus")
	GraphQLRetVal<OrderSlipItem> updateOrderSlipItemStatus(
			@GraphQLArgument(name = 'id') UUID id,
			@GraphQLArgument(name = 'status') String status
	){
		try {
			OrderSlipItem item = orderSlipItemRepository.getOne(id)

			if(StringUtils.equalsIgnoreCase(item.status, 'NEW')){
				if(item.billing_item != null){
					return new GraphQLRetVal<OrderSlipItem>(item, false, 'This item is already billed. Cancelling is now restricted.')

				}
				item.status = status
				orderSlipItemRepository.save(item)
			}else{
				return new GraphQLRetVal<OrderSlipItem>(item, false, 'This item is already been set to ' + item.status +'. Please click `OK` to reload latest changes. Thank you!')
			}

			return new GraphQLRetVal<OrderSlipItem>(item, true, "Successfully Saved!")
		} catch (Exception e) {
			return new GraphQLRetVal<OrderSlipItem>(new OrderSlipItem(), false, e.message)
		}
	}

	List<OrderSlipItem> addOrderslipFromPackage(
			Case aCase,
			Department department,
			List<Service> service,
			Employee requestingPhysician
	) {

		List<OrderSlipItem> orderslipItem = []
		def oSlip
		oSlip = new Orderslip()
		oSlip.parentCase = aCase
		oSlip.department = department
		oSlip.requestingPhysician = requestingPhysician.id
		oSlip.requestingPhysicianName = requestingPhysician.fullName

		def ordersItems = []

		service.each {
			it ->
				def order = new OrderSlipItem()
				order.posted = false
				order.status = "NEW"
				order.deleted = false
				order.service = it
				orderslipItem.add(order)
		}

		return orderslipDao.insertOrderTransaction(oSlip, orderslipItem)
	}

	@GraphQLQuery(name = "getOslipItem")
	OrderSlipItem getOslipItem(
			@GraphQLArgument(name = 'itemNo') String itemNo
	){
		def oSlipItem = orderSlipItemRepository.getByOrderSlipItem(itemNo)
		if(oSlipItem) {
			return oSlipItem[0]
		}
		else{
			 new OrderSlipItem()
		}

	}

	@GraphQLQuery(name="getOrderSlipItemByGroup")
	List<OrderSlipDto> getOrderSlipItemByGroup(
			@GraphQLArgument(name="orderSlipId") UUID orderSlipId
	){
		def a =entityManager.createQuery("""
			Select 
			oi.itemNo as item_no,
			oi.service.description as description,
			cast(coalesce(count(oi),0) as text) as cnt,
			oi.createdDate as created_date, 
			coalesce(oi.orderslip.requestingPhysicianName,'') as requestingPhysicianName
		 	from OrderSlipItem oi where oi.service.description is not null and oi.posted IS FALSE  and oi.orderslip.id = :id 
		 	group by oi.service.description,
		 	oi.createdDate,
		 	coalesce(oi.orderslip.requestingPhysicianName,''),
		 	oi.itemNo
		""")
				.setParameter("id",orderSlipId)
				.unwrap(Query.class)
				.setResultTransformer(Transformers.aliasToBean(OrderSlipDto.class)).resultList
		return a
	}


 //	@GraphQLMutation(name = "overRideOrderSlipItem")
//	GraphQLRetVal<OrderSlipItem> overRideOrderSlipItem(
//			@GraphQLArgument(name = "fields") Map<String, Object> fields
//	){
//		def oSlipItem = objectMapper.convertValue(fields.get("item"), OrderSlipItem) as OrderSlipItem
//	}
}

