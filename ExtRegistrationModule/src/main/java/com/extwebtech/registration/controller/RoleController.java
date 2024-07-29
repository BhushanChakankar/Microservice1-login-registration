package com.extwebtech.registration.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.RoleRequest;
import com.extwebtech.registration.service.RoleService;

/**
 * Controller class for handling role-related operations.
 */
@RestController
@RequestMapping("/roles")
@CrossOrigin("*")
public class RoleController {

	/**
	 * Autowired instance of RoleService for handling role-related logic.
	 */
	@Autowired
	private RoleService roleService;

	/**
	 * Endpoint to retrieve all roles.
	 *
	 * @return ApiResponse containing the list of all roles
	 */
	@GetMapping
	public ApiResponse getAllRoles() {
		return roleService.getAllRoles();
	}

	/**
	 * Endpoint to retrieve a role by its ID.
	 *
	 * @param roleId ID of the role
	 * @return ApiResponse containing the role with the specified ID
	 */
	@GetMapping("/{roleId}")
	public ApiResponse getRoleById(@PathVariable Integer roleId) {
		return roleService.getRoleById(roleId);

	}

	/**
	 * Endpoint to save new roles.
	 *
	 * @param roleRequests List of RoleRequest objects representing the new roles
	 * @return ApiResponse containing the result of saving the new roles
	 */
	@PostMapping
	public ApiResponse saveRole(@RequestBody List<RoleRequest> roleRequests) {
		return roleService.saveRole(roleRequests);
	}

	/**
	 * Endpoint to retrieve role operations for a specific module.
	 *
	 * @param roleId     ID of the role
	 * @param moduleName Name of the module
	 * @return ApiResponse containing the role operations for the specified module
	 */
	@GetMapping("/operations")
	public ApiResponse getRoleOperation(@RequestParam("roleId") Integer roleId,
			@RequestParam("moduleName") String moduleName) {
		return roleService.getRoleOperation(roleId, moduleName);
	}

	/**
	 * Endpoint to update an existing role.
	 *
	 * @param roleId      ID of the role to be updated
	 * @param updatedRole RoleRequest object representing the updated role
	 *                    information
	 * @return ApiResponse containing the result of updating the role
	 */
	@PutMapping("/{roleId}")
	public ApiResponse updateRole(@PathVariable Integer roleId, @RequestBody RoleRequest updatedRole) {
		return roleService.updateRole(roleId, updatedRole);

	}

	/**
	 * Endpoint to delete a role by its ID.
	 *
	 * @param roleId ID of the role to be deleted
	 * @return ApiResponse containing the result of deleting the role
	 */
	@DeleteMapping("/{roleId}")
	public ApiResponse deleteRole(@PathVariable Integer roleId) {
		return roleService.deleteRole(roleId);
	}
}