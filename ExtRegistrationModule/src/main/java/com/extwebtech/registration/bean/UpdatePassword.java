package com.extwebtech.registration.bean;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePassword {

	@Email(message = "please mention email")
	private String email;
	
	@NotBlank(message = "please enter password")
	private String password;
}
