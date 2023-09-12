package com.hisd3.hismk2.socket

class HISD3WebsocketMessage {
	HISD3WebsocketMessage() {
	}
	
	HISD3WebsocketMessage(String from, String message, String title, HISD3MessageType type) {
		this.from = from
		this.message = message
		this.title = title
		this.type = type
	}
	String from = ""
	String message = ""
	String title = ""
	HISD3MessageType type = null
}
