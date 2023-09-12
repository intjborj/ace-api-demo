package com.hisd3.hismk2.socket

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@TypeChecked
@Controller
class SocketController {
	
	//socket receiver from the frontend '/app/ws'
	
	@Autowired
	ObjectMapper objectMapper
	
	@MessageMapping("/ws")
	@SendTo("/channel/hello")
	protected String hello(String world) {
		return "Hello Message From Backend, ${world}!"
	}
	
	@MessageMapping("/ws/{topic}")
	@SendTo("/channel/messages")
	Message send(@DestinationVariable("topic") String topic,
	             Map<String, Object> message) throws Exception {
		def msg
		msg = objectMapper.convertValue(message, Message.class)
		msg.topic = topic
		//do something with the payload
		return msg
	}
}