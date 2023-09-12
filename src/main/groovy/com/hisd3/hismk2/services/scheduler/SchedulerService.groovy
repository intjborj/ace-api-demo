package com.hisd3.hismk2.services.scheduler

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class SchedulerService {
	
	@Autowired
	SchedulerTransaction schedulerTransaction
	
	@Async
	//@Transactional  https://declara.com/content/eaL8p2aO
	/*
		 Before and other versions it will not work...
	 */
	def autochargeRooms() {
		schedulerTransaction.autochargeRooms()
	}
	
	@Async
	def autoCloseOPD() {
		schedulerTransaction.autoCloseOPD()
	}

	@Async
	def cleanNotifications() {
		schedulerTransaction.cleanNotifications()
	}

	@Async
	void recompClosedFolio() {
		schedulerTransaction.recompClosedFolio()
	}

	@Async
	void correctAutoEntries(){
		schedulerTransaction.correctAutoEntries()
	}
}
