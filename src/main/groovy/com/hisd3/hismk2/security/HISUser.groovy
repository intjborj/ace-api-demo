package com.hisd3.hismk2.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class HISUser implements UserDetails {
	
	private String username
	private String password
	private Set<GrantedAuthority> authorities = []
	private Boolean accountNonExpired
	private Boolean accountNonLocked
	private Boolean credentialsNonExpired
	private Boolean enabled
	
	HISUser(String username, String password,
	        Set<GrantedAuthority> authorities,
	        Boolean accountNonExpired,
	        Boolean accountNonLocked,
	        Boolean credentialsNonExpired,
	        Boolean enabled) {
		this.username = username
		this.password = password
		this.authorities = authorities
		this.accountNonExpired = accountNonExpired
		this.accountNonLocked = accountNonLocked
		this.credentialsNonExpired = credentialsNonExpired
		this.enabled = enabled
	}
	
	@Override
	Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities
	}
	
	@Override
	String getPassword() {
		return password
	}
	
	@Override
	String getUsername() {
		return username
	}
	
	@Override
	boolean isAccountNonExpired() {
		return accountNonExpired
	}
	
	@Override
	boolean isAccountNonLocked() {
		return accountNonLocked
	}
	
	@Override
	boolean isCredentialsNonExpired() {
		return credentialsNonExpired
	}
	
	@Override
	boolean isEnabled() {
		return enabled
	}
}
