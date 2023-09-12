package com.hisd3.hismk2.graphqlservices.cashiering

import com.hisd3.hismk2.domain.cashiering.Cdctr
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
import com.hisd3.hismk2.domain.cashiering.Shifting
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.security.SecurityUtils
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

import java.time.Instant

@Service
@GraphQLApi
class CdctrServices extends AbstractDaoService<Cdctr> {
	CdctrServices() {
		super(Cdctr.class)
	}
	
	@Autowired
	ShiftingServices shiftingServices
	
	@Autowired
	GeneratorService generatorService
	
	@GraphQLQuery(name = "getAllCdctr")
	Page<Cdctr> getAllCdctr(
			@GraphQLArgument(name = "recno") String recno,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		
		getPageable("from Cdctr s where lower(s.recno) like lower(concat('%',:recno,'%'))   order by s.recno desc ",
				"Select count(s)  from Cdctr s where lower(s.recno) like lower(concat('%',:recno,'%'))  ",
				page,
				size,
				[recno: recno]
		)
	}
	
	@GraphQLMutation
	Boolean addToCdctr(
			@GraphQLArgument(name = "shifts") Map<String, Object> shifts
	) {
		def username = SecurityUtils.currentLogin()
		List<Shifting> selected = []
		
		shifts.each {
			k, v ->
				selected << shiftingServices.findOne(UUID.fromString(k))
		}
		
		def cdctr = new Cdctr()
		cdctr.recno = generatorService.getNextValue(GeneratorType.CDCTR , { Long it->
			return "CDCTR-" + StringUtils.leftPad((it + 1000).toString(), 6, "0")
		})
		
		cdctr.receivedby = username
		cdctr.received_datetime = Instant.now()
		save(cdctr)
		def totalCollection = 0.0
		
		selected.each {
			
			it.cdctr = cdctr
			cdctr.shiftings.add(it)
			shiftingServices.save(it)
			
			def validPayments = it.payments.findAll {
				def p = (PaymentTracker) it
				!p.voided
			}
			
			validPayments.each {
				def pt = (PaymentTracker) it
				totalCollection += pt.totalpayments
			}
			
		}
		
		cdctr.totalcollection = totalCollection
		cdctr.status
		save(cdctr)
		true
	}


	@GraphQLMutation
	Boolean voidCdctr(
			@GraphQLArgument(name = "id") UUID id
	) {
		def cdctr = findOne(id)

		if(cdctr.collection)
			return false

		List<Shifting> shifting = []
		cdctr.shiftings.each {
			it.cdctr = null
			shifting.add(it)
			shiftingServices.save(it)
		}

		cdctr.shiftings.removeAll(shifting)

		def username = SecurityUtils.currentLogin()
		cdctr.status = 'voided'
		cdctr.voidedBy = username
		cdctr.voidedDatetime = Instant.now()
		save(cdctr)

		return true
	}
}
