package com.hisd3.hismk2.socket

import groovy.transform.Canonical

@Canonical
class Message {
	String from = ""
	String topic = ""
	String message = ""
}