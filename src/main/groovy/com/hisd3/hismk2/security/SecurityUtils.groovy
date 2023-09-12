package com.hisd3.hismk2.security

import com.hisd3.hismk2.domain.hrm.Employee
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

class SecurityUtils {

	static List<String> getRoles() {
		def securityContext = SecurityContextHolder.getContext()
		def authentication = securityContext.authentication

		if (!authentication)
			return []

		def roles = []
		authentication.authorities.each {
			roles << (it as SimpleGrantedAuthority).authority
		}

		roles
	}

	static String currentLogin() {
		def securityContext = SecurityContextHolder.getContext()
		def authentication = securityContext.authentication

		if (!authentication)
			return "system"

		HISUser springSecurityUser
		String userName = null

		if (authentication != null) {
			if (authentication.principal.getClass() == HISUser) {
				springSecurityUser = authentication.principal as HISUser
				userName = springSecurityUser.username
			} else if (authentication.principal.getClass() == String) {
				userName = authentication.principal as String
			}
		}

		return userName
	}

}
