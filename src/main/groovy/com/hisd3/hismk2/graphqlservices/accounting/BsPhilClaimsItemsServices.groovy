package com.hisd3.hismk2.graphqlservices.accounting


import com.hisd3.hismk2.domain.accounting.BillingScheduleItems
import com.hisd3.hismk2.domain.accounting.BsPhilClaims
import com.hisd3.hismk2.domain.accounting.BsPhilClaimsItems
import com.hisd3.hismk2.domain.hospital_config.ComlogikSetting
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.accounting.BillingScheduleItemsRepository
import com.hisd3.hismk2.repository.accounting.BsPhilClaimsRepository
import com.hisd3.hismk2.repository.hospital_config.ComlogikSettingRepository
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import groovy.json.JsonSlurper
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.http.HttpHost
import org.apache.http.client.fluent.Executor
import org.apache.http.client.fluent.Request
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

import javax.transaction.Transactional
import java.text.SimpleDateFormat

@Component
@GraphQLApi
class BsPhilClaimsItemsServices extends AbstractDaoService<BsPhilClaimsItems> {

	BsPhilClaimsItemsServices() {
		super(BsPhilClaimsItems.class)
	}

	@Autowired
	EntityObjectMapperService entityObjectMapperService

	@Autowired
	GeneratorService generatorService


	@GraphQLQuery(name = "checkARItemExist")
	BsPhilClaimsItems checkARItemExist(@GraphQLArgument(name = "arRecItems") UUID arRecItems
	) {
		createQuery("""
                    select b from BsPhilClaimsItems b where b.accountReceivableItems.id = :arRecItems
            """,
				[
						arRecItems: arRecItems
				] as Map<String, Object>).singleResult
	}

}

