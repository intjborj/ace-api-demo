package com.hisd3.hismk2.graphqlservices.cashiering

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.cashiering.ReceiptIssuance
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Canonical
class CashierData {
	
	boolean notFound = false
	String terminalName
	String nextOR
	String nextAR
	UUID batchReceiptId
	UUID terminalId
	String type
	String shiftId
	String terminalCode
	UUID shiftPk
	String x
}

@Component
@GraphQLApi
class ReceiptIssuanceService extends AbstractDaoService<ReceiptIssuance> {
	ReceiptIssuanceService() {
		super(ReceiptIssuance.class)
	}
	
	@Autowired
	ObjectMapper objectMapper
	
	@Autowired
	EntityObjectMapperService entityObjectMapperService
	
	@Autowired
	GeneratorService generatorService
	
	@PersistenceContext
	EntityManager entityManager
	
	@Autowired
	ShiftingServices shiftingServices
	
	@GraphQLQuery(name = "receiptIssuanceById")
	ReceiptIssuance receiptIssuanceById(
			@GraphQLArgument(name = "id") UUID id
	) {
		return findOne(id)
	}
	
	// Feb. 18, 2020
	// Transaction is now open during the whole duration of your Graphql Execution... no need to merge existing entity to the transaction
	
	/*
   @GraphQLQuery(name = "terminal")
   CashierTerminal terminal(@GraphQLContext ReceiptIssuance receiptIssuance) {


    def r = entityManager.merge(receiptIssuance)
    r.terminal?.id
    return r.terminal

    return receiptIssuance.terminal

	}
	*/
	
	@GraphQLQuery(name = "getCashierData")
	CashierData getCashierData(
			@GraphQLArgument(name = "macAddress") String macAddress,
			@GraphQLArgument(name = "type") String type  // OR, AR
	) {
		
		if (type == "OR") {
			def receiptIssuance = createQuery("""
			from ReceiptIssuance r where r.terminal.macAddress=:macAddress and r.activebatch = true
       """,
					[macAddress: macAddress]).resultList.find()
			
			if (!receiptIssuance) {
				
				return new CashierData(true, null, null, null, null, null, type)
			}
			
			def terminal = receiptIssuance.terminal
			def shifting = shiftingServices.getActiveShift(terminal)
			
			if (!receiptIssuance.receiptCurrent) {
				receiptIssuance.receiptCurrent = receiptIssuance.receiptFrom
			}
			receiptIssuance = save(receiptIssuance)
			
			new CashierData(false, receiptIssuance.terminal.remarks,
					receiptIssuance.receiptCurrent?.toString() ?: "00000",
					receiptIssuance.arCurrent?.toString() ?: "00000",
					receiptIssuance.id,
					receiptIssuance.terminal.id,
					type,
					shifting?.shiftno,
					receiptIssuance.terminal.terminalId,
					shifting?.id
			)
			
		} else {
			
			def receiptIssuance = createQuery("""
			from ReceiptIssuance r where r.terminal.macAddress=:macAddress and r.aractive = true
       """,
					[macAddress: macAddress]).resultList.find()
			
			if (!receiptIssuance) {
				
				return new CashierData(true, null, null, null, null, null, type)
			}
			
			if (!receiptIssuance.arCurrent) {
				receiptIssuance.arCurrent = receiptIssuance.arFrom
			}
			
			receiptIssuance = save(receiptIssuance)
			
			def terminal = receiptIssuance.terminal
			def shifting = shiftingServices.getActiveShift(terminal)
			
			new CashierData(false, receiptIssuance.terminal.remarks,
					receiptIssuance.receiptCurrent?.toString() ?: "00000",
					receiptIssuance.arCurrent?.toString() ?: "00000",
					receiptIssuance.id,
					receiptIssuance.terminal.id,
					type,
					shifting?.shiftno,
					receiptIssuance.terminal.terminalId,
					shifting?.id
			)
			
		}
		
	}

	@GraphQLQuery(name = "receiptIssuancesByCashier")
	Page<ReceiptIssuance> receiptIssuancesByCashier(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "terminalId") String terminalId,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		getPageable("""
		Select c from ReceiptIssuance c  where 
		    (lower(CAST(c.receiptFrom as string)) like lower(concat('%',:filter,'%'))
			or
			lower(CAST(c.arFrom as string)) like lower(concat('%',:filter,'%'))
			) and 
			  lower(cast(c.terminal.id as string)) like lower(concat('%',:terminalId,'%'))
			
			order by batchcode desc
		""",
				"""
		Select count(c) from ReceiptIssuance c  where 
		   (lower(CAST(c.receiptFrom as string)) like lower(concat('%',:filter,'%'))
			or
			lower(CAST(c.arFrom as string)) like lower(concat('%',:filter,'%'))
			)  and 
			  lower(cast(c.terminal.id as string)) like lower(concat('%',:terminalId,'%'))
			
		""",
				page,
				size,
				[
				 filter: filter,
				 terminalId:terminalId
				])

	}

	@GraphQLQuery(name = "receiptIssuances")
	Page<ReceiptIssuance> receiptIssuances(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		
		getPageable("""
		Select c from ReceiptIssuance c  where lower(CAST(c.receiptFrom as string)) like lower(concat('%',:filter,'%'))
			or
			lower(CAST(c.arFrom as string)) like lower(concat('%',:filter,'%'))
		""",
				"""
		Select count(c) from ReceiptIssuance c  where lower(CAST(c.receiptFrom as string)) like lower(concat('%',:filter,'%'))
			or
			lower(CAST(c.arFrom as string)) like lower(concat('%',:filter,'%'))
		""",
				page,
				size,
				[filter: filter])
		
	}

	@GraphQLMutation
	GraphQLRetVal<String> updateActive(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "type") String type, // OR / AR
			@GraphQLArgument(name = "newValue")  Integer newValue
	) {

		def receiptBatch = findOne(id)



		if(type=="OR"){

			if(!receiptBatch.activebatch)
				return new GraphQLRetVal<String>().tap {
					it.success = false
					it.message = "OR Not Active"
				}
			if(newValue >= receiptBatch.receiptFrom && newValue <= receiptBatch.receiptTo){
				receiptBatch.receiptCurrent = newValue
				save(receiptBatch)
			}
			else
			return new GraphQLRetVal<String>().tap {
				 it.success = false
				 it.message = "Invalid Value"
			}
		}
		else {

			if(!receiptBatch.aractive)
				return new GraphQLRetVal<String>().tap {
					it.success = false
					it.message = "AR Not Active"
				}

			if(newValue >= receiptBatch.arFrom && newValue <= receiptBatch.arTo){
				receiptBatch.arCurrent = newValue
				save(receiptBatch)
			}
			else
				return new GraphQLRetVal<String>().tap {
				it.success = false
				it.message = "Invalid Value"
			}
		}


		return new GraphQLRetVal<String>().tap {
			it.success = true
			it.message = "OK"
		}
	}
	@GraphQLMutation
	ReceiptIssuance upsertReceiptIssuance(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		
		upsertFromMap(id, fields, { ReceiptIssuance entity, boolean forInsert ->
			
			if (forInsert) {
				entity.batchcode = generatorService.getNextValue(GeneratorType.CASHIER_RECEIPT_ISSUANCE, {
					return "RCPT-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
			}
		})
		
	}
	
}
