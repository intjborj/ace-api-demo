package com.hisd3.hismk2.security

// (1000 * 60) * 30  // 30 mins

class SecurityConstants {
	static final def SECRET = "5A84158E00CAA579E90E1A114A0CE9B96FE702C9"
	static final def EXPIRATION_TIME = 86_400_000L // 1 day
	static final def TOKEN_PREFIX = "Bearer "
	static final def HEADER_STRING = "Authorization"
	static final def SIGN_UP_URL = "/users/sign-up"
}
