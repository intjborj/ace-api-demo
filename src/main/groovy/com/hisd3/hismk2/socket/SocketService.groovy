package com.hisd3.hismk2.socket


import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@TypeChecked
@Service
class SocketService {
	@Autowired
	SimpMessagingTemplate simpMessagingTemplate

	void notificationToUser(HISD3WebsocketMessage payload, String username) {
		simpMessagingTemplate.convertAndSendToUser(username, "/channel/notifications", payload)
	}
	
}
