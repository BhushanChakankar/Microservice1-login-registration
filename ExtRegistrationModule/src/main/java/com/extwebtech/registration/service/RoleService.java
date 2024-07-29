package com.extwebtech.registration.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.Role;
import com.extwebtech.registration.bean.RoleRequest;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.repository.RoleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

/**
 * Service class for managing roles, including CRUD operations and retrieving
 * role-related information.
 */
@Service
public class RoleService {
	private static final Logger logger = Logger.getLogger(RoleService.class);
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	ApiResponseConfig apiResponseConfig;

	/**
	 * Retrieves all roles.
	 *
	 * @return ApiResponse containing a list of roles.
	 */
	public ApiResponse getAllRoles() {
		String methodName = "getAllRoles";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();

		try {
			List<Role> roles = roleRepository.findAll();
			apiResponse.setStatus(true);
			apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
			logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
			apiResponse.setData(roles);
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage());
			apiResponse.setData(null);
			e.printStackTrace();
		}

		return apiResponse;
	}

	/**
	 * Retrieves a role by its ID.
	 *
	 * @param roleId ID of the role to retrieve.
	 * @return ApiResponse containing the role information.
	 */
	public ApiResponse getRoleById(Integer roleId) {
		String methodName = "getRoleById";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		try {
			Role role = roleRepository.findById(roleId).orElse(null);
			if (role != null) {

				apiResponse.setStatus(true);
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
				apiResponse.setData(role);
				logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
				return apiResponse;
			} else {
				apiResponse.setStatus(false);
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getNotFoundMessage());
				apiResponse.setData(null);
				return apiResponse;
			}
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage());
			apiResponse.setData(null);
			return apiResponse;
		}
	}

	/**
	 * Saves multiple roles.
	 *
	 * @param roleRequests List of RoleRequest objects containing role information.
	 * @return ApiResponse containing the saved roles.
	 */
	public ApiResponse saveRole(List<RoleRequest> roleRequests) {
		String methodName = "saveRole";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		try {
			List<Role> roles = new ArrayList<>();
			for (RoleRequest roleRequest : roleRequests) {
				Role role = new Role();
				role.setRoleId(roleRequest.getRoleId());
				role.setRoleName(roleRequest.getRoleName());
				String permissionsJson = new ObjectMapper().writeValueAsString(roleRequest.getRolePermissions());
				role.setPermissions(permissionsJson);
				roles.add(role);
			}
			List<Role> savedRoles = roleRepository.saveAll(roles);

			apiResponse.setStatus(true);
			apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
			apiResponse.setData(savedRoles);
			logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
			return apiResponse;
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			e.printStackTrace();
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage());
			apiResponse.setData(null);
			return apiResponse;
		}
	}

	/**
	 * Updates an existing role by its ID.
	 *
	 * @param roleId      ID of the role to update.
	 * @param updatedRole RoleRequest object containing updated role information.
	 * @return ApiResponse containing the updated role.
	 */
	public ApiResponse updateRole(Integer roleId, RoleRequest updatedRole) {
		String methodName = "updateRole";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		try {
			if (roleRepository.existsById(roleId)) {
				Role existingRole = roleRepository.findById(roleId).orElse(null);
				if (existingRole != null) {
					existingRole.setRoleName(updatedRole.getRoleName());
					String updatedPermissionsJson = new ObjectMapper()
							.writeValueAsString(updatedRole.getRolePermissions());
					existingRole.setPermissions(updatedPermissionsJson);
					Role role = roleRepository.save(existingRole);
					apiResponse.setStatus(true);
					apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
					apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
					apiResponse.setData(role);
					logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName + " " + role);
				}
			} else {
				apiResponse.setStatus(false);
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getNotFoundMessage());
				apiResponse.setData(null);
			}

		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage());
			apiResponse.setData(null);
		}
		return apiResponse;
	}

	/**
	 * Deletes a role by its ID.
	 *
	 * @param roleId ID of the role to delete.
	 * @return ApiResponse indicating the success or failure of the deletion
	 *         operation.
	 */
	public ApiResponse deleteRole(Integer roleId) {
		String methodName = "deleteRole";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		try {
			if (roleRepository.existsById(roleId)) {
				roleRepository.deleteById(roleId);
				apiResponse.setStatus(true);
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
				apiResponse.setData(null);
				logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
			} else {
				apiResponse.setStatus(false);
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getNotFoundMessage());
				apiResponse.setData(null);
			}
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			e.printStackTrace();
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage());
			apiResponse.setData(null);
		}
		return apiResponse;
	}

	public ApiResponse getRoleOperation(int roleId, String moduleName) {
		String methodName = "getRoleOperation";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		try {
			String nativeQuery = "SELECT DISTINCT module->'moduleOperation' AS moduleOperation " + "FROM ("
					+ "    SELECT jsonb_array_elements(CAST(permissions AS jsonb)) AS module " + "    FROM roles "
					+ "    WHERE id = ?1" + ") subquery " + "WHERE module->>'moduleName' = ?2";

			Query query = entityManager.createNativeQuery(nativeQuery);
			query.setParameter(1, roleId);
			query.setParameter(2, moduleName);

			List<String> moduleOperations = query.getResultList();

			if (moduleOperations.isEmpty()) {
				apiResponse.setStatus(false);
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getNotFoundMessage());
				apiResponse.setData(null);
			} else {
				apiResponse.setStatus(true);
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
				apiResponse.setData(moduleOperations);
				logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
			}
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			e.printStackTrace();
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage());
			apiResponse.setData(null);
		}
		return apiResponse;
	}

}