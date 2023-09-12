package com.hisd3.hismk2.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter

import javax.servlet.Filter

// Taken from https://stackoverflow.com/questions/48037601/lazyinitializationexception-with-graphql-spring
// https://github.com/leangen/graphql-spqr/issues/263

@Configuration
class GraphQLConfig {
	
	@Bean
	Filter OpenFilter() {
		return new OpenEntityManagerInViewFilter()
	}
	
}
