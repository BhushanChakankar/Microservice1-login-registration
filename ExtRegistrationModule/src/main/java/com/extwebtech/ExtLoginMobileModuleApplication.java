package com.extwebtech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication
@EnableScheduling
//@EnableDiscoveryClient
@EnableEncryptableProperties
public class ExtLoginMobileModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExtLoginMobileModuleApplication.class, args);
	}

}
