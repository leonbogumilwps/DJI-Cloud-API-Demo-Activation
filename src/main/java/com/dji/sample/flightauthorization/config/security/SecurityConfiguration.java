package com.dji.sample.flightauthorization.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Allow access to the "/manage/api/v1/**" endpoint without authentication
		http.authorizeRequests()
			.antMatchers("/manage/api/v1/**").permitAll()
			.antMatchers("/wayline/api/v1/**").permitAll()
			.antMatchers("/api/v1/flight-authorization/**").permitAll()
			.anyRequest().authenticated()
			.and()
			.csrf().disable(); // Disable CSRF protection for all endpoints;
	}
}