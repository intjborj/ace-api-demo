package com.hisd3.hismk2.graphqlservices.billing

import com.hisd3.hismk2.dao.price_tier.ServicePriceControlDao
import com.hisd3.hismk2.domain.billing.ServicePriceControl
import com.hisd3.hismk2.rest.dto.ServiceCheckDTO
import com.hisd3.hismk2.rest.dto.ServicePriceControlDto
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class ServicePriceControlService {
	
	@Autowired
	ServicePriceControlDao servicePriceControlDao
	
	@GraphQLQuery(name = "getAllPriceControlServices")
	Page<ServicePriceControlDto> getAllPriceControlServices(
			@GraphQLArgument(name = "tierId") UUID tierId,
			@GraphQLArgument(name = "deptId") UUID deptId,
			@GraphQLArgument(name = "costGroup") String costGroup,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	
	) {
		return servicePriceControlDao.getAllPriceControlServices(tierId, deptId, costGroup, filter, PageRequest.of(page, pageSize, Sort.Direction.ASC, 'serviceCode'))
	}
	
	@GraphQLMutation
	def addServicePriceControl(
			@GraphQLArgument(name = "serviceId") String serviceId,
			@GraphQLArgument(name = "tierDetail") String tierDetail,
			@GraphQLArgument(name = "amountValue") String amountValue,
			@GraphQLArgument(name = "percentageValue") String percentageValue
	) {
		servicePriceControlDao.updateServicePriceControl(serviceId, tierDetail, amountValue as BigDecimal, percentageValue as BigDecimal)
	}
	
	@GraphQLMutation
	def toggleLockServicePriceControl(
			@GraphQLArgument(name = "serviceId") String serviceId,
			@GraphQLArgument(name = "tierDetail") String tierDetail
	) {
		servicePriceControlDao.toggleLockServicePriceControl(serviceId, tierDetail)
	}
	
	@GraphQLMutation
	List<ServiceCheckDTO> servicePriceCheck(@GraphQLArgument(name = "fields") Map<String, Object> fields) {
		servicePriceControlDao.validateServicePriceList(fields)
	}
	
	@GraphQLMutation
	List<ServicePriceControl> massUpdateServicePrices(
			@GraphQLArgument(name = "tierDetail") String tierDetail,
			@GraphQLArgument(name = "deptId") String deptId,
			@GraphQLArgument(name = "costGroup") String costGroup,
			@GraphQLArgument(name = "percentageValue") String percentageValue
	) {
		return servicePriceControlDao.massUpdateServicePrices(tierDetail, deptId, costGroup, percentageValue as BigDecimal)
	}
	
	@GraphQLMutation
	def updateServicePrices(
			@GraphQLArgument(name = "serviceId") String serviceId
	) {
		return servicePriceControlDao.updateServicePrices(serviceId)
	}
	
}
