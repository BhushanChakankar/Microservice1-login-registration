package com.extwebtech.registration.repository;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.extwebtech.registration.bean.Notifications;
import com.extwebtech.registration.configuration.ApiResponseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

/**
 * Repository class for handling database operations related to notifications.
 */
@Repository
public class NotificationRepository {

	private static final Logger logger = Logger.getLogger(NotificationRepository.class);

	@Autowired
	private EntityManager entityManager;

	@Autowired
	ApiResponseConfig apiResponseConfig;

	/**
	 * Saves a notification in the database.
	 * 
	 * @param notification The notification to be saved
	 * @return The saved notification
	 */
	@Transactional
	public Notifications saveNotification(Notifications notification) {
		try {
			String nativeQuery = "INSERT INTO " + apiResponseConfig.getNotificationsTable() + " ("
					+ apiResponseConfig.getUserIdColumnNotify() + ", " + apiResponseConfig.getNotificationIdColumn()
					+ ", " + apiResponseConfig.getCreatedByColumn() + ") VALUES (?, ?, ?)";
			Query query = entityManager.createNativeQuery(nativeQuery);
			query.setParameter(1, notification.getUserId());
			query.setParameter(2, notification.getNotificationId());
			query.setParameter(3, notification.getCreatedBy());
			query.executeUpdate();
			logger.info("Saved Notification with user ID: " + notification.getUserId());

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return notification;
	}

	/**
	 * Retrieves device tokens associated with a user ID.
	 * 
	 * @param userId The user ID
	 * @return List of device tokens
	 */
	public List<String> getDeviceToken(int userId) {
		try {
			Query query = entityManager.createNativeQuery("SELECT " + apiResponseConfig.getDeviceTokenColumn()
					+ " FROM " + apiResponseConfig.getLoginDetailsTable() + " WHERE "
					+ apiResponseConfig.getUserIdColumnNotify() + " = ?");
			query.setParameter(1, userId);
			return (List<String>) query.getResultList();

		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves details associated with a notification topic.
	 * 
	 * @param topic The notification topic
	 * @return Array of details [title, description, id, createdBy]
	 */
	public Object[] getDetails(String topic) {
		try {
			Query query = entityManager.createNativeQuery("SELECT " + apiResponseConfig.getTitleColumn() + ", "
					+ apiResponseConfig.getDescriptionColumn() + ", " + apiResponseConfig.getIdColumn() + ", "
					+ apiResponseConfig.getAdminNotificationCreatedByColumn() + " FROM "
					+ apiResponseConfig.getAdminNotificationTable() + " WHERE topic = ?");
			query.setParameter(1, topic);
			return (Object[]) query.getSingleResult();

		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

}