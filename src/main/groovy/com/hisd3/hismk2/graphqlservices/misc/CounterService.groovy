package com.hisd3.hismk2.graphqlservices.misc

import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.TupleConstructor
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@TupleConstructor
class Counter {
	String name
	Long value
}

@Component
@GraphQLApi
class CounterService {
	
	@Autowired
	JdbcTemplate jdbcTemplate
	
	@GraphQLMutation
	Counter setCounter(
			@GraphQLArgument(name = "seqName") String seqName,
			@GraphQLArgument(name = "value") Long value
	) {
		
		def seqNameT = seqName.toLowerCase() + "_gen"
		jdbcTemplate.queryForObject("SELECT setval('${seqNameT}', ${value}, true); ", Long)
		
		new Counter(seqName, value)
	}
	
	@GraphQLQuery(name = "counters")
	List<Counter> counters() {
		
		def result = []
		def names = GeneratorType.values()*.name()
		
		names.each {
			def seqName = it.toLowerCase() + "_gen"
			
			try {
				def currentVal = jdbcTemplate.queryForObject("SELECT last_value FROM  ${seqName}", Long) as Long
				
				result << new Counter(it, currentVal)
			}
			catch (Exception e) {
				// sequence not exist yet can safely ignore
			}
			
		}
		
		result
	}
}
