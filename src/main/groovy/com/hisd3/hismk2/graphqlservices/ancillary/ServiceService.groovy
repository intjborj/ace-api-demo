package com.hisd3.hismk2.graphqlservices.ancillary

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.dao.ancillary.PanelContentDao
import com.hisd3.hismk2.dao.ancillary.ServiceDao
import com.hisd3.hismk2.dao.price_tier.PriceTierDetailDao
import com.hisd3.hismk2.domain.ancillary.PackageContent
import com.hisd3.hismk2.domain.ancillary.PanelContent
import com.hisd3.hismk2.domain.ancillary.Service
import com.hisd3.hismk2.domain.ancillary.Service as HisService
import com.hisd3.hismk2.repository.ancillary.PackageContentRepository
import com.hisd3.hismk2.repository.ancillary.ServiceRepository
import com.hisd3.hismk2.rest.HisServiceResource
import com.hisd3.hismk2.services.GenerateRfService
import com.hisd3.hismk2.services.GeneratorService
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

import java.math.RoundingMode

class TieredService {
	HisService service
	String calculatedPrice
}

@TypeChecked
@Component
@GraphQLApi
class ServiceService {

	@Autowired
	PanelContentDao panelContentDao

	@Autowired
	ServiceDao servicesDao

	@Autowired
	GeneratorService generatorService

	@Autowired
	private ServiceRepository servicesRepository

	@Autowired
	PackageContentRepository packageContentRepository

	@Autowired
	PriceTierDetailDao priceTierDetailDao

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	GenerateRfService generateRfService

	@Autowired
	HisServiceResource hisServiceResource

	//============== All Queries ====================

	@GraphQLQuery(name = "services", description = "Get All Service")
	List<HisService> findAll() {
		servicesDao.findAll()
	}

	@GraphQLQuery(name = "searchServicesByPage", description = "Search Services by Page")
	Page<HisService> searchServicesByPage(@GraphQLArgument(name = "filter") String filter,
	                                      @GraphQLArgument(name = "page") Integer page,
	                                      @GraphQLArgument(name = "size") Integer size) {

		servicesRepository.getServicesFiltered(filter,
				new PageRequest(page, size, Sort.Direction.ASC, "serviceName"))
	}

	@GraphQLQuery(name = "searchServicesByDefaultPage", description = "Search Services by Page")
	Page<HisService> searchServicesByDefaultPage(@GraphQLArgument(name = "filter") String filter) {

		servicesRepository.getServicesFiltered(filter,
				new PageRequest(0, 25, Sort.Direction.ASC, "serviceName"))
	}

	@GraphQLQuery(name = "searchServices", description = "Search Services")
	List<HisService> searchServices(@GraphQLArgument(name = "filter") String filter) {
		servicesDao.searchHisServices(filter)
	}

	@GraphQLQuery(name = "searchServicesForDoctorsOrders", description = "Search Services For Doctors Orders")
	List<HisService> searchServicesForDoctorsOrders(@GraphQLArgument(name = "filter") String filter) {
		servicesRepository.searchlistForDoctorsOrders(filter)
	}

	@GraphQLQuery(name = "searchServicesByDepartment", description = "Search Services by Department")
	List<HisService> searchServicesByDepartment(@GraphQLArgument(name = "filter") String filter, @GraphQLArgument(name = "department") String department = "") {
		def list
		if (department == "" || department == null) {
			list = servicesDao.searchHisServices(filter)
		} else {
			list = servicesDao.searchHisServicesByDepartment(UUID.fromString(department), filter)
		}
		return list.sort { it.serviceCode }
	}

	@GraphQLQuery(name = "searchServicesByDepartmentPageable", description = "Search Services by Department")
	Page<HisService> searchServicesByDepartmentPageable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "department") UUID department,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		Page<HisService> list
		Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.ASC, 'serviceName')
		if (department) {
			list = servicesRepository.searchlist(filter, pageable)
		} else {
			list = servicesRepository.searchlistByDepartment(department, filter, pageable)
		}
		return list
	}

	@GraphQLQuery(name = "searchTieredServicesByDepartment", description = "Search Services by Department")
	List<TieredService> searchTieredServicesByDepartment(@GraphQLArgument(name = "priceTierDetailId") String priceTierDetailId, @GraphQLArgument(name = "department") String departmentId = "", @GraphQLArgument(name = "filter") String filter = "") {

		def list
		if (departmentId == "" || departmentId == null) {
			list = servicesDao.searchHisServices(filter)
		} else {
			list = servicesDao.searchHisServicesByDepartment(UUID.fromString(departmentId), filter)
		}
		//return list
		List<TieredService> res = []
		list.forEach {
			def hService = objectMapper.convertValue(it, HisService)
			def serviceItem = new TieredService()
			serviceItem.service = it
			if (priceTierDetailId != "") {
				serviceItem.calculatedPrice = priceTierDetailDao.getServicePrice(UUID.fromString(priceTierDetailId), hService.id)
			} else {
				serviceItem.calculatedPrice = "Please select Tier"
			}

			res.add(serviceItem)
		}
		return res.sort { it.service.serviceCode }
	}

	@GraphQLQuery(name = "getPanelComponent", description = "Search Child Services")
	List<PanelContent> getPanelComponent(@GraphQLArgument(name = "id") String id) {
		panelContentDao.searchHisServices(id)
	}

	@GraphQLQuery(name = "getPackageContent", description = "Search Item Contents")
	List<PackageContent> getPackageContent(@GraphQLArgument(name = "parentId") UUID parentId) {
		packageContentRepository.findByContentParentService(parentId).sort{ it.itemId.descLong}
	}

	@GraphQLQuery(name = "Service", description = "Get Service By Id")
	HisService findById(@GraphQLArgument(name = "id") String id) {

		return servicesDao.findById(UUID.fromString(id))
	}

	//============== All Mutations ====================

	@GraphQLMutation
	HisService upsertServices(
			@GraphQLArgument(name = "id") String id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def serviceitem = servicesDao.findById(UUID.fromString(id))
			objectMapper.updateValue(serviceitem, fields)

			serviceitem.available = true

			return servicesDao.save(serviceitem)
		} else {
			def serviceitem = objectMapper.convertValue(fields, HisService)

			serviceitem.available = true

			return servicesDao.save(serviceitem)
		}
	}

	@GraphQLMutation
	List<PanelContent> addPanelComponents(
			@GraphQLArgument(name = "id") String id,
			@GraphQLArgument(name = "fields") ArrayList<Map<String, Object>> fields
	) {

		HisService parentService = servicesDao.findById(UUID.fromString(id))

		List<PanelContent> serviceComponents = []
		def child
		child = fields as ArrayList<HisService>
		child.each {
			it ->
				def sibling = objectMapper.convertValue(it, HisService)
				def order = new PanelContent()
				order.parent = parentService
				order.service = sibling
				order.id = null
				serviceComponents.add(order)
		}
		return panelContentDao.addPanelComponents(serviceComponents)
	}

	@GraphQLMutation
	List<Service> massUpdateServicesCost(
			@GraphQLArgument(name = "department") String department,
			@GraphQLArgument(name = "operator") String operator,
			@GraphQLArgument(name = "percentageValue") String percentageValue) {
		List<Service> allServices = servicesRepository.searchlistByDepartment(UUID.fromString(department), "")
		List<Service> toModify = []

		Double percVal = percentageValue.toDouble()

		allServices.each {
			it ->
				if (it.basePrice > 0) {
					if (operator == "add") {
						it.basePrice = it.basePrice * ((percVal + 100.0) / 100.0)
					} else if (operator == "subtract") {
						BigDecimal price = it.basePrice / ((percVal + 100.0) / 100.0)
						it.basePrice = price.setScale(2, RoundingMode.HALF_EVEN)
					}
				}

				toModify.push(it)
		}

		return servicesRepository.saveAll(toModify)
	}

	@GraphQLMutation
	Boolean generateDoctorsRf(
			@GraphQLArgument(name = "empId") String empId,
			@GraphQLArgument(name = "department") String department,
			@GraphQLArgument(name = "percentage") String percentage) {

		BigDecimal percVal = new BigDecimal(percentage)
		generateRfService.generateRfService(empId,department,"",percVal)

	}

	@GraphQLMutation
	Boolean updateRf(
			@GraphQLArgument(name = "rfId") String rfId,
			@GraphQLArgument(name = "percentage") String percentage) {

		hisServiceResource.updaterfResource(rfId,percentage)
	}

	@GraphQLMutation
	Boolean updateRfFixedPrice(
			@GraphQLArgument(name = "rfId") String rfId,
			@GraphQLArgument(name = "fixedPrice") String percentage) {

		hisServiceResource.updateRfFixedPrice(rfId,percentage)
	}
}

