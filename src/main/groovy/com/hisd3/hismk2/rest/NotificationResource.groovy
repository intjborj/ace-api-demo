package com.hisd3.hismk2.rest

import com.hisd3.hismk2.services.AblyNotificationService
import com.hisd3.hismk2.services.NotificationService
import io.ably.lib.rest.Auth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notif")
class NotificationResource {

	@RequestMapping("/generate-token")
	static Auth.TokenRequest generateToken() {
		AblyNotificationService ablyService = new AblyNotificationService()
		return ablyService.generateTokenRequest();
	}
}