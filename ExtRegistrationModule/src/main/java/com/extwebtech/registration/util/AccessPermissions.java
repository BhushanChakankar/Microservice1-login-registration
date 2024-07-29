package com.extwebtech.registration.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.service.RoleService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@Component
public class AccessPermissions {

	@Autowired
	RoleService roleService;
	@Autowired
	EntityManager entityManager;

	public boolean hasAccess(int roleId, String moduleName, String action) {
		ApiResponse apiResponse = roleService.getRoleOperation(roleId, moduleName);
		if (apiResponse.isStatus()) {
			String permissionsString = apiResponse.getData().toString();
			return permissionsString.contains(action);
		} else {
			return false;
		}
	}
	
	public boolean getAuthorization() {
		String nativeQuery = "select is_authorized from authorize where id = 1";
		Query query = entityManager.createNativeQuery(nativeQuery);
		return (boolean) query.getSingleResult();
	}

}
