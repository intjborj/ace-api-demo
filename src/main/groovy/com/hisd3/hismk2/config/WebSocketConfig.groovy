package com.hisd3.hismk2.config

import groovy.transform.TypeChecked
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@TypeChecked
@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	@Value('${mq.host}')
	String mqHost = ""
	
	@Override
	void configureMessageBroker(MessageBrokerRegistry config) {
		
		if (StringUtils.isEmpty(mqHost)) {
			config.enableSimpleBroker("/channel")
			config.setApplicationDestinationPrefixes("/app")
		} else {
			
			config.enableStompBrokerRelay("/channel")
					.setRelayHost(mqHost) // broker host
					.setRelayPort(61613)
					.setClientLogin("admin")
					.setClientPasscode("admin")
			
			config.setApplicationDestinationPrefixes("/app")
			
		}
		
	}
	
	@Override
	void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS()
	}
}
