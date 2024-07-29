package com.extwebtech.registration.bean;

import lombok.Data;

@Data
public class LoginResponse {

	public boolean status;
	public String statusCode;
	public String message;
	public String token;
	public Integer loginId;
	public Object data;
}