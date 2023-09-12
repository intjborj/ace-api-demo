package com.hisd3.hismk2.graphqlservices.accounting

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.accounting.ReleaseCheck
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.accounting.BankRepository
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.Instant
import java.time.Duration

@Service
@GraphQLApi
class ReleaseCheckServices extends AbstractDaoService<ReleaseCheck> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	ApTransactionServices apTransactionServices

	@Autowired
	DisbursementCheckServices disbursementCheckServices

	@Autowired
	DisbursementServices disbursementServices

	@Autowired
	BankRepository bankRepository


    ReleaseCheckServices() {
		super(ReleaseCheck.class)
	}
	
	@GraphQLQuery(name = "releaseCheckById")
	ReleaseCheck releaseCheckById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}

	@GraphQLQuery(name = "releaseChecksFilter", description = "Filter Checks")
	Page<ReleaseCheck> releaseChecksFilter(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "bank") UUID bank,
			@GraphQLArgument(name = "start") String start,
			@GraphQLArgument(name = "end") String end,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		String query = '''Select d from ReleaseCheck d where
						(lower(d.check.checkNo) like lower(concat('%',:filter,'%')) or 
						lower(d.disbursement.payeeName) like lower(concat('%',:filter,'%')))
						and to_date(to_char(d.check.checkDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD')'''

		String countQuery = '''Select count(d) from ReleaseCheck d where
						(lower(d.check.checkNo) like lower(concat('%',:filter,'%')) or 
						lower(d.disbursement.payeeName) like lower(concat('%',:filter,'%')))
						and to_date(to_char(d.check.checkDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD')'''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)
		params.put('start', start)
		params.put('end', end)

		if (bank) {
			query += ''' and (d.bank.id = :bank) '''
			countQuery += ''' and (d.bank.id = :bank) '''
			params.put("bank", bank)
		}

		query += ''' ORDER BY d.check.checkDate DESC'''

		getPageable(query, countQuery, page, size, params)
	}

	//mutations
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertReleaseCheck")
	ReleaseCheck upsertReleaseCheck(
			@GraphQLArgument(name = "fields") ArrayList<Map<String, Object>> items,
			@GraphQLArgument(name = "date") Instant date,
			@GraphQLArgument(name = "id") UUID id
	) {
		def result = new ReleaseCheck()
		items.each {fields ->
			def obj = objectMapper.convertValue(fields, ReleaseCheck.class)
			result = upsertFromMap(id, fields, { ReleaseCheck entity, boolean forInsert ->
				if (forInsert) {
					entity.releaseDate = date.plus(Duration.ofHours(8))
					entity.release_by = SecurityUtils.currentLogin()
					entity.isPosted = true
				}
			})
			//update disbursement
			disbursementServices.updateForRelease(obj.disbursement?.id)
			disbursementCheckServices.updateCheck(obj.check?.id, result.id)
		}
		return result
	}

}
