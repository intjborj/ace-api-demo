package com.hisd3.hismk2.graphqlservices.cashiering

import com.hisd3.hismk2.domain.cashiering.CashierTerminal
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
@GraphQLApi
class CashieringService extends AbstractDaoService<CashierTerminal> {




	CashieringService() {
		super(CashierTerminal.class)
	}
	
	@Autowired
	GeneratorService generatorService
	
	@GraphQLQuery(name = "cashierTerminalsById")
	CashierTerminal cashierTerminalsById(
			@GraphQLArgument(name = "id") UUID id
	) {
		return findOne(id)
	}
	
	CashierTerminal findByMacAddess(String macAddress) {
		
		createQuery("from CashierTerminal c where c.macAddress=:macAddress",
				[macAddress: macAddress])
				.resultList.find()
		
	}
	
	@GraphQLQuery(name = "cashierTerminals")
	Page<CashierTerminal> getCashierTerminals(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		
		getPageable(
				"""
			 Select c from CashierTerminal c  where lower(c.remarks) like lower(concat('%',:filter,'%')) order by c.terminalId
			""",
				"""
			Select count(c) from CashierTerminal c  where lower(c.remarks) like lower(concat('%',:filter,'%'))
			""",
				page,
				size,
				[filter: filter]
		)
		
	}

	@GraphQLQuery(name = "terminals")
	List<CashierTerminal> terminals() {
		findAll().sort { it.description }
	}

	@GraphQLQuery(name = "cashierTerminal")
	CashierTerminal findOneById(@GraphQLArgument(name = "id") UUID id) {
		createQuery("from CashierTerminal c where c.id=:id",
				[id: id])
				.resultList.find()
	}




	@GraphQLMutation
	CashierTerminal upsertCashierTerminals(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		
		upsertFromMap(id, fields, { CashierTerminal entity, boolean forInsert ->
			
			if (forInsert) {
				entity.terminalId = generatorService.getNextValue(GeneratorType.CASHIER_TERMINAL_ID, {
					return "TM-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
				
			}
		})
		
	}



}
