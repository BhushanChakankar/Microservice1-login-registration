package com.extwebtech.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.UpdatePasswordRequest;
import com.extwebtech.registration.service.ForgetPasswordService;

/**
 * Controller class for handling operations related to updating forgotten
 * passwords.
 */
@RestController
@RequestMapping("/update")
@CrossOrigin("*")
public class ForgetPasswordController {
	/**
	 * Autowired instance of ForgetPasswordService for handling business logic
	 * related to forgotten passwords.
	 */
	@Autowired
	ForgetPasswordService forgetPasswordService;

	/**
	 * Endpoint to update the password for a user who has forgotten their password.
	 *
	 * @param updatePasswordRequest UpdatePasswordRequest object containing the new
	 *                              password
	 * @return ApiResponse containing the result of updating the password
	 */
	@PutMapping("/password")
	public ApiResponse updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
		return forgetPasswordService.updatePassword(updatePasswordRequest);
	}
}
