package com.hisd3.hismk2.graphqlservices.inventory

import com.hisd3.hismk2.domain.inventory.LogsCount
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.inventory.PhysicalCountRepository
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@GraphQLApi
class LogsCountService extends AbstractDaoService<LogsCount> {
	
	LogsCountService() {
		super(LogsCount.class)
	}
	
	@Autowired
	GeneratorService generatorService
	
	@Autowired
	PhysicalCountRepository physicalCountRepository
	
	@GraphQLQuery(name = "physicalLogs", description = "List of Physical Logs")
	List<LogsCount> physicalLogs(@GraphQLArgument(name = "id") UUID id) {
		
		createQuery("Select logs from LogsCount logs where logs.physicalCount.id = :id order by logs.user",
				[id: id])
				.resultList
		
	}
	
	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertLogCount", description = "insert Log Count")
	LogsCount upsertLogCount(
			@GraphQLArgument(name = "count") Integer count,
			@GraphQLArgument(name = "parentId") UUID parentId
	
	) {
		LogsCount upsert = new LogsCount()
		
		upsert.count = count
		upsert.physicalCount = physicalCountRepository.findById(parentId).get()
		upsert.user = SecurityUtils.currentLogin()
		save(upsert)
		
		//update parent
		def sum = physicalCountRepository.getMonthlyCount(parentId)
		
		def parent = physicalCountRepository.findById(parentId).get()
		parent.variance = sum - parent.onHand
		
		physicalCountRepository.save(parent)
		
		return upsert
	}


	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "deleteLogCount", description = "delete Log Count")
	LogsCount upsertLogCount(
			@GraphQLArgument(name = "id") UUID countId,
			@GraphQLArgument(name = "parentId") UUID parentId

	) {
		LogsCount upsert = findOne(countId)
		delete(upsert)

		//update parent
		def sum = physicalCountRepository.getMonthlyCount(parentId)

		def parent = physicalCountRepository.findById(parentId).get()
		parent.variance = sum - parent.onHand

		physicalCountRepository.save(parent)

		return upsert
	}
	
}

