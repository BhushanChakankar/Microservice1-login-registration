package com.extwebtech.registration.bean;

import lombok.Data;

@Data
public class LoginRequest {
	private String mobileNumber;
	private int role;
	private String deviceToken;
	private String deviceType;
	private DeviceDetails deviceDetails;
}