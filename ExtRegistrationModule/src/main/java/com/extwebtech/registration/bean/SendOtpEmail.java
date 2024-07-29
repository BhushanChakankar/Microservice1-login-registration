package com.extwebtech.registration.bean;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendOtpEmail {

	@NotBlank(message = "Enter your mobile phone number ")
	private String email;

}
