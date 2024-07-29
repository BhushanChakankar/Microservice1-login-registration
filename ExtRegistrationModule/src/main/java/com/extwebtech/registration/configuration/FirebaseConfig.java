package com.extwebtech.registration.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.IOException;
//
//@Configuration
//public class FirebaseConfig {
//
//	@Value("${firebase.config.path}")
//	private String firebaseConfigPath;
//
//	@Bean
//	public FirebaseApp initializeFirebaseApp() throws IOException {
//		FileInputStream serviceAccount = null;
//		try {
//			if (firebaseConfigPath != null && !firebaseConfigPath.isEmpty()) {
//				serviceAccount = new FileInputStream(firebaseConfigPath);
//			} else {
//				serviceAccount = new FileInputStream(ResourceUtils.getFile("classpath:firebase.json"));
//			}
//
//			FirebaseOptions options = FirebaseOptions.builder()
//					.setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
//
//			return FirebaseApp.initializeApp(options);
//		} finally {
//			if (serviceAccount != null) {
//				serviceAccount.close();
//			}
//		}
//	}
//}
