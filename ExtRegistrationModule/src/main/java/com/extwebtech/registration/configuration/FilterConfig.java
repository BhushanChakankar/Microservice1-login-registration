package com.extwebtech.registration.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.extwebtech.registration.util.JWTService;

@Configuration
public class FilterConfig {

	@Autowired
	JWTService jwtService;

//	@Bean
//	public FilterRegistrationBean<JwtFilter> customerJwtFilter() {
//		FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();
//		registrationBean.setFilter(new JwtFilter(jwtService, new int[] { 1, 2, 3 }));
//	//	registrationBean.addUrlPatterns("*");
//		return registrationBean;
//	}
}
