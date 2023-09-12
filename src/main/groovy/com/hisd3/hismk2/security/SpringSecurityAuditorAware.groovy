package com.hisd3.hismk2.security

import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component

@Component
class SpringSecurityAuditorAware implements AuditorAware<String> {
	@Override
	Optional<String> getCurrentAuditor() {
		return new Optional<String>(SecurityUtils.currentLogin())
	}
}
