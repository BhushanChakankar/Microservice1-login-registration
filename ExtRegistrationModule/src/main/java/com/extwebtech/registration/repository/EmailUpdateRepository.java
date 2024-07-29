package com.extwebtech.registration.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

/**
 * Repository class for handling database operations related to email updates.
 */
@Repository
public class EmailUpdateRepository {

	@Autowired
	EntityManager entityManager;

	/**
	 * Updates the email of a user by user ID.
	 * 
	 * @param userId User ID
	 * @param email  New email to be set
	 * @return Number of updated rows
	 */
	@Transactional
	public int updateEmailById(int userId, String email) {
		String sql = "UPDATE users SET email = :email WHERE id = :userId";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("email", email);
		query.setParameter("userId", userId);
		return query.executeUpdate();

	}
}
