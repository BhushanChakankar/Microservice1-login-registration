package com.extwebtech.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.extwebtech.registration.util.NotificationUtil;

@RestController
public class NotificationController {

	@Autowired
	NotificationUtil notificationUtil;

//	@PostMapping("/send")
//	public String sendNotification(@RequestParam String fcmToken, @RequestParam String topic) {
//		return notificationUtil.sendPushNotification(fcmToken , topic);
//	}
}
