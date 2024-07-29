package com.extwebtech.registration.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.extwebtech.registration.bean.User;

/**
 * Repository interface for handling database operations related to User
 * entities.
 */
public interface RegistrationRepository extends JpaRepository<User, Integer> {
	/**
	 * Retrieves a User entity by its user ID.
	 *
	 * @param userId The user ID
	 * @return User entity associated with the user ID
	 */

	User getById(Integer userId);

	/**
	 * Retrieves a list of User entities where the subscription is not null.
	 *
	 * @return List of User entities with non-null subscriptions
	 */
	List<User> findAllBySubscriptionIsNotNull();

	// User findBySubscriptionNotNullAndUserId(int userId);
	/**
	 * Checks if a user with the specified subscription ID exists.
	 *
	 * @param subscriptionId The subscription ID
	 * @return True if a user with the subscription ID exists, otherwise false
	 */
	boolean existsBySubscriptionId(int subscriptionId);

	/**
	 * Retrieves a User entity with a non-null subscription by its user ID.
	 *
	 * @param userId The user ID
	 * @return User entity with a non-null subscription associated with the user ID
	 */
	User findBySubscriptionNotNullAndId(int userId);

	/**
	 * Finds the referral code associated with a user by user ID.
	 *
	 * @param userId The user ID
	 * @return Referral code associated with the user ID
	 */
	@Query("SELECT u.referralCode FROM User u WHERE u.id = :userId")
	String findReferralCodeById(int userId);

	/**
	 * Retrieves a Page of User entities with their average ratings.
	 *
	 * @param pageable The pageable object for pagination
	 * @return Page of User entities with average ratings
	 */
	@Query(value = "SELECT u.*, AVG(rr.ratings) AS average_rating " + "FROM users u LEFT JOIN ratingsreviews rr "
			+ "ON u.id = rr.seller_id " + "GROUP BY u.id", nativeQuery = true)
	Page<Object[]> getAllUsersWithAverageRatings(Pageable pageable);

}