package com.extwebtech.registration.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.extwebtech.registration.bean.LoginRequest;
import com.extwebtech.registration.bean.Subscriptions;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.exception.UserNotFoundException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

/**
 * Repository class for handling database operations related to mobile login
 * functionality.
 */
@Repository
public class MobileLoginRepository {

	@Autowired
	EntityManager entityManager;

	@Autowired
	ApiResponseConfig apiResponseConfig;

	/**
	 * Retrieves user details based on the mobile number.
	 * 
	 * @param loginRequest Login request containing mobile number
	 * @return Array containing user details
	 * @throws UserNotFoundException if the user is not found
	 */
	@Transactional
	public Object[] findByMobileNumberWithId(LoginRequest loginRequest) {
		String nativeQuery = "SELECT * FROM " + apiResponseConfig.getUsersTable() + " WHERE "
				+ apiResponseConfig.getMobileColumn() + " = :mobileNumber";

		Query query = entityManager.createNativeQuery(nativeQuery);
		query.setParameter("mobileNumber", loginRequest.getMobileNumber());

		try {
			Object[] result = (Object[]) query.getSingleResult();
			return result;
		} catch (NoResultException e) {
			throw new UserNotFoundException("User not found with mobile number: " + loginRequest.getMobileNumber());
		}
	}

	/**
	 * Fetches subscription details by subscription ID.
	 * 
	 * @param subscriptionId Subscription ID
	 * @return Subscription details
	 */
	public Subscriptions fetchSubscriptionDetails(Integer subscriptionId) {
		String subscriptionQuery = "SELECT s FROM Subscriptions s WHERE s."
				+ apiResponseConfig.getSubscriptionsIdColumn() + " = :subscriptionId";
		TypedQuery<Subscriptions> query = entityManager.createQuery(subscriptionQuery, Subscriptions.class);
		query.setParameter("subscriptionId", subscriptionId);

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Inserts or updates user data for mobile login.
	 * 
	 * @param mobileNumber      User's mobile number
	 * @param deviceDetailsJson Device details in JSON format
	 * @param deviceToken       Device token
	 * @param deviceType        Device type
	 * @param userId            User ID
	 * @return Login ID
	 */
	@Transactional
	public int insertOrUpdateUserData(String mobileNumber, String deviceDetailsJson, String deviceToken,
			String deviceType, int userId) {
		String sqlCheckIfExists = "SELECT id, device_token FROM logindetails WHERE user_mobile = :mobileNumber";
		Query checkQuery = entityManager.createNativeQuery(sqlCheckIfExists);
		checkQuery.setParameter("mobileNumber", mobileNumber);

		List<Object[]> loginDetailsList = checkQuery.getResultList();

		if (!loginDetailsList.isEmpty()) {
			Object[] loginDetails = loginDetailsList.get(0);

			int loginId = (int) loginDetails[0];
			String existingDeviceToken = (String) loginDetails[1];
			if (existingDeviceToken != null && existingDeviceToken.equals(deviceToken)) {
				String sqlUpdate = "UPDATE logindetails SET device_details = CAST(:deviceDetailsJson AS json), device_type = :deviceType, user_id = :userId WHERE id = :loginId";
				Query updateQuery = entityManager.createNativeQuery(sqlUpdate);
				updateQuery.setParameter("deviceDetailsJson", deviceDetailsJson);
				// updateQuery.setParameter("deviceToken", deviceToken);
				updateQuery.setParameter("deviceType", deviceType);
				updateQuery.setParameter("userId", userId);
				updateQuery.setParameter("loginId", loginId);
				updateQuery.executeUpdate();

				return loginId;
			} else {
				String sqlInsert = "INSERT INTO logindetails (user_mobile, device_details, device_token, device_type, user_id) VALUES (:mobileNumber, CAST(:deviceDetailsJson AS json), :deviceToken, :deviceType, :userId) RETURNING id";
				Query insertQuery = entityManager.createNativeQuery(sqlInsert);
				insertQuery.setParameter("mobileNumber", mobileNumber);
				insertQuery.setParameter("deviceDetailsJson", deviceDetailsJson);
				insertQuery.setParameter("deviceToken", deviceToken);
				insertQuery.setParameter("deviceType", deviceType);
				insertQuery.setParameter("userId", userId);

				return (int) insertQuery.getSingleResult();
			}
		} else {
			String sqlInsert = "INSERT INTO logindetails (user_mobile, device_details, device_token, device_type, user_id) VALUES (:mobileNumber, CAST(:deviceDetailsJson AS json), :deviceToken, :deviceType, :userId) RETURNING id";
			Query insertQuery = entityManager.createNativeQuery(sqlInsert);
			insertQuery.setParameter("mobileNumber", mobileNumber);
			insertQuery.setParameter("deviceDetailsJson", deviceDetailsJson);
			insertQuery.setParameter("deviceToken", deviceToken);
			insertQuery.setParameter("deviceType", deviceType);
			insertQuery.setParameter("userId", userId);

			return (int) insertQuery.getSingleResult();
		}
	}

	/**
	 * Checks if a user exists based on the mobile number.
	 * 
	 * @param mobile User's mobile number
	 * @return True if the user exists, false otherwise
	 */
	public boolean checkIfUserExists(String mobile) {
		String sql = "SELECT COUNT(*) FROM " + apiResponseConfig.getUsersTable() + " WHERE "
				+ apiResponseConfig.getMobileColumn() + " = :mobile";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("mobile", mobile);

		int count = ((Number) query.getSingleResult()).intValue();
		return count > 0;
	}

	/**
	 * Checks if a user exists with a given user ID and mobile number.
	 * 
	 * @param mobile User's mobile number
	 * @param userId User ID
	 * @return True if the user exists, false otherwise
	 */
	public boolean checkIfUserExistsWithUserId(String mobile, int userId) {
		String sql = "SELECT COUNT(*) FROM " + apiResponseConfig.getUsersTable() + " WHERE "
				+ apiResponseConfig.getMobileColumn() + " = :mobile and id != :userId";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("mobile", mobile);
		query.setParameter("userId", userId);

		int count = ((Number) query.getSingleResult()).intValue();
		return count > 0;
	}

}
