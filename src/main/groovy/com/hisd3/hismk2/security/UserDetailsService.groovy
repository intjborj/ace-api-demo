package com.hisd3.hismk2.security

import com.hisd3.hismk2.repository.UserRepository
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

@Service
@Transactional
class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
	
	private def log = LoggerFactory.getLogger(UserDetailsService)
	
	private HttpServletRequest request
	
	@Autowired
	UserRepository userRepository
	
	@Autowired
	void setRequest(HttpServletRequest request) {
		this.request = request
	}
	
	String getClientIp() {
		
		String remoteAddr = ""
		
		if (request != null) {
			remoteAddr = request?.getHeader("X-FORWARDED-FOR")
			if (remoteAddr == null || "" == remoteAddr) {
				remoteAddr = request?.getRemoteAddr()
			}
		}
		
		return remoteAddr
	}
	
	String getClientUserAgent() {
		
		return request.getHeader("User-Agent")
		
	}
	
	@Override
	UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		log.info("Authenticating {} from IP {} using {}", login, getClientIp(), getClientUserAgent())
		
		def lowercaseLogin = login.toLowerCase()
		def userFromDatabase = userRepository.findOneByLogin(lowercaseLogin)
		
		if (userFromDatabase == null)
			throw new UsernameNotFoundException("User $lowercaseLogin was not found in the database")
		
		def grantedAuthorities = [] as Set<GrantedAuthority>
		
		def hasROLE_USER = false
		// activated is not used ... this is
		//TODO: Use Activated column of user
		//	if (userFromDatabase.activated) {
		
		userFromDatabase.authorities?.forEach {
			authority ->
				if (StringUtils.equals(authority?.name, "ROLE_USER"))
					hasROLE_USER = true
				
				def grantedAuthority = new SimpleGrantedAuthority(authority?.name)
				grantedAuthorities.add(grantedAuthority)
		}
		
		//}
		
		// all user should be a ROLE_USER
		if (!hasROLE_USER) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"))
			if (userFromDatabase.id == null) {
				grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_PATIENT"))
			}
		}
		
		return new HISUser(
				login,
				userFromDatabase.password ?: "",
				grantedAuthorities,
				true,
				true,
				true,
				true
		)
		
	}
}
