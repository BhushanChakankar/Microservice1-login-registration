package com.extwebtech.registration.bean;

import java.util.List;

import lombok.Data;

@Data
public class RoleRequest {
	private Integer roleId;
	private String roleName;
	private List<RolePermissions> rolePermissions;
}