package com.extwebtech.registration.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class OtpService {

	private Map<String, String> otpStorage = new HashMap<>();

	@Value("${twilio.account-sid}")
	private String accountSid;

	@Value("${twilio.auth-token}")
	private String authToken;

	@Value("${twilio.from-number}")
	private String fromNumber;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Autowired
	private JavaMailSender javaMailSender;

	public String generateOtp() {
		return String.format("%04d", (int) (Math.random() * 10000));
	}

	public String sendOtpSms(String mobileNumber, String otp) {
		Twilio.init(accountSid, authToken);

		Message message = Message
				.creator(new PhoneNumber(mobileNumber), new PhoneNumber(fromNumber), "Your OTP is: " + otp).create();
		return otp;
	}

	public String sendOtpByEmail(String email, String otp) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(email);
			message.setSubject("Your OTP");
			message.setText("Your OTP is: " + otp);
			message.setFrom(fromEmail);

			javaMailSender.send(message);

			return otp;
		} catch (Exception e) {
			throw new RuntimeException("Failed to send OTP via email", e);
		}
	}

	public void storeGeneratedOtp(String target, String otp) {
		otpStorage.put(target, otp);
	}

	public boolean validateOtp(String target, String enteredOtp) {
		if (otpStorage.containsKey(target)) {
			String storedOtp = otpStorage.get(target);
			return storedOtp.equals(enteredOtp);
		}
		return false;
	}
}
