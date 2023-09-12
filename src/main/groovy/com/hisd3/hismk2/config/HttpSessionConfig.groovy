package com.hisd3.hismk2.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession

@EnableRedisHttpSession
class HttpSessionConfig {
	
	@Value('${redis.host}')
	String host
	
	@Value('${redis.port}')
	Integer port = 6379
	
	@ConditionalOnProperty(name = ["redis.deployment"], havingValue = "openshift")
	@Bean
	LettuceConnectionFactory connectionFactoryOpenshift() {
		
		def factory = new LettuceConnectionFactory(host, port)
		//factory.password = "password"
		return factory
	}
	
	@ConditionalOnProperty(name = ["redis.deployment"], havingValue = "dev")
	@Bean
	LettuceConnectionFactory connectionFactory() {
		return new LettuceConnectionFactory()
	}
}
