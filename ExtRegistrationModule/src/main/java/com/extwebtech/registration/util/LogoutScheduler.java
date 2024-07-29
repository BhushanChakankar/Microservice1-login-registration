package com.extwebtech.registration.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.extwebtech.registration.repository.EmailLoginRepository;

@Component
public class LogoutScheduler {

	private final EmailLoginRepository loginRepository;

	public LogoutScheduler(EmailLoginRepository loginRepository) {
		this.loginRepository = loginRepository;
	}

	@Scheduled(cron = "0 0 0 * * ?")
	public void logoutInactiveUsers() {
		LocalDateTime thirtyDaysAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
		int deletedRows = loginRepository.deleteInactiveLogins(thirtyDaysAgo);
		System.out.println("Logged out " + deletedRows + " inactive users.");
	}
}
