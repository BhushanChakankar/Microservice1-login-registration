package com.extwebtech.registration.bean;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpMobile {
	@NotBlank(message = "Enter your mobile phone number ")
	private String MobileNumber;

	@NotBlank(message = " OTP is required")
	private String enteredOtp;

}
