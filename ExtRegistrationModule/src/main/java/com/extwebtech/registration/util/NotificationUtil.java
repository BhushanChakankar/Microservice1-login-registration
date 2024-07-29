package com.extwebtech.registration.util;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.extwebtech.registration.bean.Notifications;
import com.extwebtech.registration.repository.NotificationRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class NotificationUtil {

	Logger logger = Logger.getLogger(NotificationUtil.class);
	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	JWTService jwtService;

	public String sendPushNotification(int userId, String topic) {
		Notifications notifications = new Notifications();
		try {
			List<String> deviceToken = notificationRepository.getDeviceToken(userId);
			Object[] details = notificationRepository.getDetails(topic);
			if (!deviceToken.isEmpty()) {
				for (int i = 0; i < deviceToken.size(); i++) {
					Message fcmMessage = Message.builder().setNotification(Notification.builder()
							.setTitle(details[0].toString()).setBody(details[1].toString()).build())
							.setToken(deviceToken.get(i)).build();

					FirebaseMessaging.getInstance().send(fcmMessage);
				}
				notifications.setUserId(userId);
				notifications.setCreatedBy((Integer) details[3]);
				notifications.setNotificationId((Integer) details[2]);

				Notifications notification = notificationRepository.saveNotification(notifications);
			}
			return "notification send";

		} catch (FirebaseMessagingException e) {
			return e.getMessage();

		}
	}

}