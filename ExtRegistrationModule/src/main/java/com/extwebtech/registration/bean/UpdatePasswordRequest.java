package com.extwebtech.registration.bean;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
	private String email;
	private String newPassword;
}