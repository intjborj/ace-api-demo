package com.hisd3.hismk2.config

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {
	
	@Bean
	FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
		def source = new UrlBasedCorsConfigurationSource()
		def config = new CorsConfiguration()
		config.allowCredentials = true
		// TODO update allowed origins when deployed to server
		config.allowedOrigins = Arrays.asList("*")
		config.allowedMethods = Collections.singletonList("*")
		config.allowedHeaders = Collections.singletonList("*")
		config.maxAge = 3600
		source.registerCorsConfiguration("/**", config)
		def bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(source))
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE)
		return bean
	}
}
