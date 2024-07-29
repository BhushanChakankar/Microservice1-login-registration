package com.extwebtech.registration.bean;

import lombok.Data;

@Data
public class LoginEmail {

	private String email;
	private String password;
	private String deviceToken;
	private String deviceType;
	private DeviceDetails deviceDetails;
}
