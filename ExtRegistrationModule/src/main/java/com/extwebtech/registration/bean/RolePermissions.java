package com.extwebtech.registration.bean;

import java.util.List;

import lombok.Data;

@Data
public class RolePermissions {
	private String moduleName;
	private List<String> moduleOperation;

}