package com.hisd3.hismk2.graphqlservices.types

import groovy.transform.Canonical

@Canonical
class GraphQLRetVal<T> {
	T payload
	boolean success = false
	String message = ""
	UUID returnId = null
}

@Canonical
class GraphQLRetValAppointment<T> {
	T payload
	boolean success = false
	String message = ""
	String header = ""
}

@Canonical
class GraphQLResVal<T> {
	T response
	boolean success = false
	String message = ""
	UUID returnId = null
}
