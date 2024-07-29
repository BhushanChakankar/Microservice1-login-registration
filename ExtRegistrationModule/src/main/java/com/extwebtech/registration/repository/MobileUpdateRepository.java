package com.extwebtech.registration.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.extwebtech.registration.configuration.ApiResponseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

/**
 * Repository class for handling database operations related to mobile number
 * updates.
 */
@Repository
public class MobileUpdateRepository {

	@Autowired
	EntityManager entityManager;

	@Autowired
	ApiResponseConfig apiResponseConfig;

	/**
	 * Updates the mobile number for a user based on the user ID.
	 * 
	 * @param userId          User ID
	 * @param newMobileNumber New mobile number
	 * @return Number of rows affected by the update operation
	 */
	@Transactional
	public int updateMobileNumberById(int userId, String newMobileNumber) {
		try {
			String sql = "UPDATE " + apiResponseConfig.getUsersTable() + " SET " + apiResponseConfig.getMobileColumn()
					+ " = :newMobileNumber" + " WHERE " + apiResponseConfig.getUserIdColumn() + " = :userId";
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("newMobileNumber", newMobileNumber);
			query.setParameter("userId", userId);
			return query.executeUpdate();
		} catch (Exception e) {
			return 0;
		}
	}

}
