package com.extwebtech.registration.bean;

import lombok.Data;

@Data
public class ApiResponse {
	public boolean status;
	public String statusCode;
	public String message;
	public Object data;

}
