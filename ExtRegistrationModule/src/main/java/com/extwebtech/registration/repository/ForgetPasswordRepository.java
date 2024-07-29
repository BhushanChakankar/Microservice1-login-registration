package com.extwebtech.registration.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.extwebtech.registration.configuration.ApiResponseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

/**
 * Repository class for handling database operations related to forget password
 * functionality.
 */
@Repository
public class ForgetPasswordRepository {

	@Autowired
	EntityManager entityManager;

	@Autowired
	ApiResponseConfig apiResponseConfig;

	/**
	 * Updates the password for a user by email.
	 * 
	 * @param email       User's email
	 * @param newPassword New password to be set
	 * @return Number of updated rows
	 */
	@Transactional
	public int updatePasswordByEmail(String email, String newPassword) {
		String nativeQuery = "UPDATE " + apiResponseConfig.getUsersTable()
				+ " SET password = :newPassword WHERE email Ilike :email";

		Query query = entityManager.createNativeQuery(nativeQuery);
		query.setParameter("newPassword", newPassword);
		query.setParameter("email", email);
		return query.executeUpdate();
	}
}
