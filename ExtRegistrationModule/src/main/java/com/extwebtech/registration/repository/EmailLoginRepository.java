package com.extwebtech.registration.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.extwebtech.registration.bean.LoginEmail;
import com.extwebtech.registration.bean.Subscriptions;
import com.extwebtech.registration.exception.CommonException;
import com.extwebtech.registration.exception.UserNotFoundException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

/**
 * Repository class for handling database operations related to email-based
 * login.
 */
@Repository
public class EmailLoginRepository {

	@Autowired
	EntityManager entityManager;

	/**
	 * Inserts or updates user data for email-based login.
	 * 
	 * @param email        User's email
	 * @param userDataJson User data in JSON format
	 * @param deviceToken  User's device token
	 * @param deviceType   Type of the device
	 * @param userId       User ID
	 * @return Login ID
	 */
	@Transactional
	public int insertOrUpdateUserDataEmail(String email, String userDataJson, String deviceToken, String deviceType,
			int userId) {
		String sqlCheckIfExists = "SELECT id, device_token FROM logindetails WHERE user_email ILIKE :email";
		Query checkQuery = entityManager.createNativeQuery(sqlCheckIfExists);
		checkQuery.setParameter("email", email);

		List<Object[]> loginDetailsList = checkQuery.getResultList();

		if (!loginDetailsList.isEmpty()) {
			Object[] loginDetails = loginDetailsList.get(0);

			int loginId = (int) loginDetails[0];
			String existingDeviceToken = (String) loginDetails[1];
			if (existingDeviceToken != null && existingDeviceToken.equals(deviceToken)) {
				String sqlUpdate = "UPDATE logindetails SET device_details = CAST(:userDataJson AS json), device_type = :deviceType, user_id = :userId WHERE id = :loginId";
				Query updateQuery = entityManager.createNativeQuery(sqlUpdate);
				updateQuery.setParameter("userDataJson", userDataJson);
				updateQuery.setParameter("deviceType", deviceType);
				updateQuery.setParameter("userId", userId);
				updateQuery.setParameter("loginId", loginId);
				updateQuery.executeUpdate();

				return loginId;
			} else {
				String sqlInsert = "INSERT INTO logindetails (user_email, device_details, device_token, device_type, user_id) VALUES (:email, CAST(:userDataJson AS json), :deviceToken, :deviceType, :userId) RETURNING id";
				Query insertQuery = entityManager.createNativeQuery(sqlInsert);
				insertQuery.setParameter("email", email);
				insertQuery.setParameter("userDataJson", userDataJson);
				insertQuery.setParameter("deviceToken", deviceToken);
				insertQuery.setParameter("deviceType", deviceType);
				insertQuery.setParameter("userId", userId);

				return (int) insertQuery.getSingleResult();
			}
		} else {
			String sqlInsert = "INSERT INTO logindetails (user_email, device_details, device_token, device_type, user_id) VALUES (:email, CAST(:userDataJson AS json), :deviceToken, :deviceType, :userId) RETURNING id";
			Query insertQuery = entityManager.createNativeQuery(sqlInsert);
			insertQuery.setParameter("email", email);
			insertQuery.setParameter("userDataJson", userDataJson);
			insertQuery.setParameter("deviceToken", deviceToken);
			insertQuery.setParameter("deviceType", deviceType);
			insertQuery.setParameter("userId", userId);

			return (int) insertQuery.getSingleResult();
		}
	}

	/**
	 * Finds user data by email and password for email-based login.
	 * 
	 * @param loginEmail User's login credentials (email and password)
	 * @return User data as an array of objects
	 * @throws UserNotFoundException If the user is not found
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public Object[] findByEmail(LoginEmail loginEmail) {
		String nativeQuery = "SELECT * FROM users WHERE email Ilike :email AND password = :password";
		Query query = entityManager.createNativeQuery(nativeQuery);
		query.setParameter("email", loginEmail.getEmail());
		query.setParameter("password", loginEmail.getPassword());
		try {
			Object[] result = (Object[]) query.getResultStream().findFirst().orElse(null);
			return result;
		} catch (NoResultException e) {
			throw new UserNotFoundException("User not found wi: " + loginEmail.getEmail());
		}
	}

	/**
	 * Fetches subscription details by subscription ID.
	 * 
	 * @param subscriptionId ID of the subscription
	 * @return Subscriptions entity
	 */
	public Subscriptions fetchSubscriptionDetails(Integer subscriptionId) {
		String subscriptionQuery = "SELECT s FROM Subscriptions s WHERE s.id = :subscriptionId";
		TypedQuery<Subscriptions> query = entityManager.createQuery(subscriptionQuery, Subscriptions.class);
		query.setParameter("subscriptionId", subscriptionId);

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Checks if a user with the given email exists.
	 * 
	 * @param email User's email
	 * @return True if the user exists, false otherwise
	 */
	public boolean checkIfUserExists(String email) {
		String sql = "SELECT COUNT(*) FROM public.users WHERE email Ilike :email";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("email", email);

		int count = ((Number) query.getSingleResult()).intValue();
		return count > 0;
	}

	/**
	 * Checks if a user with the given email and user ID exists.
	 * 
	 * @param email  User's email
	 * @param userId User ID to exclude from the check
	 * @return True if the user exists, false otherwise
	 */
	public boolean checkIfUserExistsWIthuserId(String email, int userId) {
		String sql = "SELECT COUNT(*) FROM public.users WHERE email Ilike :email and id !=:userId ";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("email", email);
		query.setParameter("userId", userId);

		int count = ((Number) query.getSingleResult()).intValue();
		return count > 0;
	}

	/**
	 * Updates the device token for a given login ID, user details, and device
	 * token.
	 * 
	 * @param loginId     Login ID
	 * @param details     User details (mobile or email)
	 * @param deviceToken User's device token
	 * @return Number of updated rows
	 * @throws CommonException If the update fails
	 */
	@Transactional
	public int updateDeviceToken(int loginId, String details, String deviceToken) {
		try {
			String query = "UPDATE logindetails SET device_token = :deviceToken WHERE id = :loginId AND (user_mobile = :details OR user_email = :details)";
			Query entityManagerQuery = entityManager.createNativeQuery(query);
			entityManagerQuery.setParameter("deviceToken", deviceToken);
			entityManagerQuery.setParameter("loginId", loginId);
			entityManagerQuery.setParameter("details", details);

			return entityManagerQuery.executeUpdate();
		} catch (Exception e) {
			throw new CommonException("Failed to update device token: " + e.getMessage());
		}
	}

	/**
	 * Deletes login details associated with a given user's mobile or email.
	 * 
	 * @param details User details (mobile or email)
	 * @return Number of deleted rows
	 * @throws CommonException If the deletion fails
	 */
	@Transactional
	public int deleteLoginDetails(String details) {
		try {
			String sql = "DELETE FROM logindetails WHERE user_mobile = :details OR user_email = :details";
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("details", details);
			int deletedRows = query.executeUpdate();
			return deletedRows;
		} catch (Exception e) {
			throw new CommonException("Failed to delete login details: " + e.getMessage());
		}
	}

	/**
	 * Deletes inactive logins based on a specified timestamp.
	 * 
	 * @param thirtyDaysAgo Timestamp for determining inactivity
	 * @return Number of deleted rows
	 * @throws CommonException If the deletion fails
	 */
	@Transactional
	public int deleteInactiveLogins(LocalDateTime thirtyDaysAgo) {
		try {
			String sql = "DELETE FROM logindetails WHERE login_time < :thirtyDaysAgo";
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("thirtyDaysAgo", thirtyDaysAgo);
			int deletedRows = query.executeUpdate();
			return deletedRows;
		} catch (Exception e) {
			throw new CommonException("Failed to delete inactive logins: " + e.getMessage());
		}
	}

	/**
	 * Retrieves the password for a user with the given email.
	 * 
	 * @param email User's email
	 * @return User's password
	 * @throws UserNotFoundException If the user is not found
	 */
	public String getPassword(String email) {
		String nativeQuery = "SELECT password FROM users where email Ilike :email";
		Query query = entityManager.createNativeQuery(nativeQuery);
		query.setParameter("email", email);
		try {
			String result = (String) query.getResultStream().findFirst().orElse(null);
			return result;
		} catch (NoResultException e) {
			throw new UserNotFoundException("User not found with: " + email);
		}
	}
}
