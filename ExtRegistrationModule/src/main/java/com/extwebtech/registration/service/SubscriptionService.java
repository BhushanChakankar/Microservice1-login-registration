package com.extwebtech.registration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.extwebtech.registration.bean.Subscriptions;
import com.extwebtech.registration.bean.User;
import com.extwebtech.registration.repository.RegistrationRepository;

@Service
public class SubscriptionService {

	@Autowired
	RegistrationRepository registrationRepository;

	/**
	 * Scheduled task to decrement subscription days for users with active
	 * subscriptions. This task runs every day at 12:01 AM.
	 */
	@Scheduled(cron = "0 1 0 * * ?")
	public void decrementSubscriptionDays() {
		List<User> usersWithSubscription = registrationRepository.findAllBySubscriptionIsNotNull();
		for (User user : usersWithSubscription) {
			Subscriptions subscription = user.getSubscription();
			int daysRemaining = user.getSubscriptionDaysRemaining();
			if (subscription != null && daysRemaining > 0) {
				if (subscription.getDurations().equalsIgnoreCase("weekly") && daysRemaining <= 7) {
					daysRemaining = Math.max(0, daysRemaining - 1);
				} else if (subscription.getDurations().equalsIgnoreCase("monthly") && daysRemaining <= 30) {
					daysRemaining = Math.max(0, daysRemaining - 1);
				} else if (subscription.getDurations().equalsIgnoreCase("yearly") && daysRemaining <= 365) {
					daysRemaining = Math.max(0, daysRemaining - 1);
				}
				user.setSubscriptionDaysRemaining(daysRemaining);
				registrationRepository.save(user);
			}
		}
	}
}
