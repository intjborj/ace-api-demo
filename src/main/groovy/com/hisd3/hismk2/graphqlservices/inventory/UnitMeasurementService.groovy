package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.ItemGroup
import com.hisd3.hismk2.domain.inventory.UnitMeasurement
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.repository.inventory.UnitMeasurementRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@GraphQLApi
@TypeChecked
class UnitMeasurementService {
	
	@Autowired
	UnitMeasurementRepository unitMeasurementRepository
	
	@Autowired
	ItemRepository itemRepository

	@Autowired
	ObjectMapper objectMapper
	
	@GraphQLQuery(name = "unitOfPurchase", description = "List of Unit Measurement (Big Unit)")
	List<UnitMeasurement> unitOfPurchase() {
		return unitMeasurementRepository.unitOfPurchase()
	}
	
	@GraphQLQuery(name = "unitOfUsage", description = "List of Unit Measurement (Small Unit)")
	List<UnitMeasurement> unitOfUsage() {
		return unitMeasurementRepository.unitOfUsage()
	}
	
	@GraphQLQuery(name = "unitMeasurementList", description = "List of Unit Measurement")
	List<UnitMeasurement> getUnitMeasurements() {
		return unitMeasurementRepository.findAll().sort { it.unitDescription }
	}
	
	@GraphQLQuery(name = "unitMeasurementFilter", description = "List of Unit Measurement")
	List<UnitMeasurement> unitMeasurementFilter(@GraphQLArgument(name = "filter") String filter) {
		return unitMeasurementRepository.unitMeasurementFilter(filter).sort { it.unitDescription }
	}

	@GraphQLQuery(name = "measureFilterPage", description = "List of Items")
	Page<UnitMeasurement> measureFilterPage(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page, // zero based
			@GraphQLArgument(name = "size") Integer pageSize
	) {
		return unitMeasurementRepository.measureFilterPage(filter, new PageRequest(page, pageSize, Sort.Direction.ASC, "unitCode"))
	}
	
	@GraphQLQuery(name = "unitMeasurementActive", description = "List of Active Unit Measurement")
	List<UnitMeasurement> unitMeasurementActive() {
		return unitMeasurementRepository.unitMeasurementActive().sort { it.createdDate }
	}
	
	//validation
	@GraphQLQuery(name = "isUnitMeasurementCodeUnique", description = "Check if Unit Measurement exists")
	Boolean findOneByUnitMeasurementCode(@GraphQLArgument(name = "unitCode") String unitCode) {
		return !unitMeasurementRepository.findOneByUnitMeasurementCode(unitCode)
	}
	
	@GraphQLQuery(name = "isUnitMeasurementNameUnique", description = "Check if Unit Measurement exists")
	Boolean findOneByUnitMeasurementName(@GraphQLArgument(name = "unitDescription") String unitDescription) {
		return !unitMeasurementRepository.findOneByUnitMeasurementName(unitDescription)
	}
	
	@GraphQLQuery(name = "items_purchase", description = "List of Unit Measurement items")
	Set<Item> findByUnitOfPurchase(@GraphQLContext UnitMeasurement unitMeasurement) {
		return itemRepository.findByUnitOfPurchase(unitMeasurement.id).sort({ it.descLong }) as Set
	}
	
	@GraphQLQuery(name = "items_usage", description = "List of Unit Measurement items")
	Set<Item> findByUnitOfUsage(@GraphQLContext UnitMeasurement unitMeasurement) {
		return itemRepository.findByUnitOfUsage(unitMeasurement.id).sort({ it.descLong }) as Set
	}

	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertUnitM", description = "upsert Unit")
	GraphQLRetVal<Boolean> upsertUnitM(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		UnitMeasurement unit = new UnitMeasurement()
		def obj = objectMapper.convertValue(fields, UnitMeasurement.class)
		def result = new GraphQLRetVal<Boolean>(true,true,"Unit Measurement Added")
		def checkCode = unitMeasurementRepository.findOneByUnitMeasurementCode(obj.unitCode)
		def checkDesc = unitMeasurementRepository.findOneByUnitMeasurementCode(obj.unitDescription)
		if(id){
			unit = unitMeasurementRepository.findById(id).get()
			unit.unitCode = obj.unitCode
			unit.unitDescription = obj.unitDescription
			unit.isBig = obj.isBig
			unit.isSmall = obj.isSmall
			unit.isActive = obj.isActive
			unitMeasurementRepository.save(unit)
			result = new GraphQLRetVal<Boolean>(true,true,"Unit Measurement Updated")
		}else{
			if(checkCode || checkDesc){
				result =  new GraphQLRetVal<Boolean>(false,false,"Unit Measurement Code or Description already exist")
			}else{
				unit.unitCode = obj.unitCode
				unit.unitDescription = obj.unitDescription
				unit.isBig = obj.isBig
				unit.isSmall = obj.isSmall
				unit.isActive = obj.isActive
				unitMeasurementRepository.save(unit)
			}
		}
		return result
	}
}
