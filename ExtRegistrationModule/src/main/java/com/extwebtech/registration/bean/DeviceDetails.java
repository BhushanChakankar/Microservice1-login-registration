package com.extwebtech.registration.bean;

import lombok.Data;

@Data
public class DeviceDetails {
	private String deviceName;
	private String androidVersion;
	private String latitude;
	private String longitude;
	private String mobileModel;
}
