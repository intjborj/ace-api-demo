package com.hisd3.hismk2.services.scheduler

import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.repository.pms.CaseRepository
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.BooleanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Configuration
@Service
@Slf4j
@EnableAsync
@EnableScheduling
class SchedulerNotificationTransaction {
	
	@Autowired
	CaseRepository caseRepository
	
	@Autowired
	BillingItemServices billingItemServices
	
	@Autowired
	BillingService billingService

	@Autowired
	JdbcTemplate jdbcTemplate

	@Scheduled(cron = "0 0 0,12 * * ?")
	@Transactional
	@Async
	def autoRemoveOldNotifications() {

		log.info("Scheduler removing all old notifications. ")
		String query = """ delete from notifications where date_notified < (NOW() - INTERVAL '2 DAYS')"""
		jdbcTemplate.execute(query)
	}
}
