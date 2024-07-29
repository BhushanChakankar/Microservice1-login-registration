package com.extwebtech.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.extwebtech.registration.bean.Subscriptions;

/**
 * Repository interface for handling database operations related to
 * Subscriptions entities.
 */
@Repository
public interface SubscriptionsRepository extends JpaRepository<Subscriptions, Integer> {
	/**
	 * Retrieves a subscription by its plan name.
	 *
	 * @param planName The name of the subscription plan
	 * @return The subscription with the specified plan name, or null if not found
	 */
	Subscriptions findByPlanName(String planeName);

	/**
	 * Checks if a subscription with the specified plan name (case-insensitive)
	 * exists.
	 *
	 * @param planName The name of the subscription plan to check
	 * @return True if a subscription with the specified plan name exists, otherwise
	 *         false
	 */
	boolean existsByPlanNameIgnoreCase(String planName);
}
