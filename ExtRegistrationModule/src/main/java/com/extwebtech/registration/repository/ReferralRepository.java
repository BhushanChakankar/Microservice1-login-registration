package com.extwebtech.registration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.extwebtech.registration.bean.Referral;

/**
 * Repository interface for handling database operations related to Referral
 * entities.
 */
public interface ReferralRepository extends JpaRepository<Referral, Long> {
	/**
	 * Retrieves a list of Referral entities associated with a referrer user ID.
	 *
	 * @param userId The referrer user ID
	 * @return List of Referral entities associated with the referrer user ID
	 */
	List<Referral> findByReferrerId(int userId);

	/**
	 * Checks if a Referral entry exists for a given referrer ID and friend email.
	 *
	 * @param referrerId  The referrer user ID
	 * @param friendEmail The email of the referred friend
	 * @return True if a Referral entry exists, otherwise false
	 */
	boolean existsByReferrerIdAndFriendEmail(int referrerId, String friendEmail);

}