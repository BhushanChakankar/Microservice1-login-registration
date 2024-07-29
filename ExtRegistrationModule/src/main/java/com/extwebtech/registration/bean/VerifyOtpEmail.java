package com.extwebtech.registration.bean;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpEmail {
	@NotBlank(message = "Enter your mobile phone number ")
	private String email;

	@NotBlank(message = " OTP is required")
	private String enteredOtp;

}
