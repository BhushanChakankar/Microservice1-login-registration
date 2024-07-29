package com.extwebtech.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.UpdateMobileRequest;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.exception.MissingAuthorizationHeaderException;
import com.extwebtech.registration.repository.MobileLoginRepository;
import com.extwebtech.registration.service.MobileUpdateService;

/**
 * Controller class for handling operations related to updating mobile numbers.
 */
@RestController
@RequestMapping("/update")
@CrossOrigin("*")
public class MobileUpdateController {
	/**
	 * Autowired instance of MobileUpdateService for handling business logic related
	 * to updating mobile numbers.
	 */
	@Autowired
	MobileUpdateService mobileUpdateService;
	/**
	 * Autowired instance of ApiResponseConfig for configuring API responses.
	 */
	@Autowired
	ApiResponseConfig apiResponseConfig;

	/**
	 * Autowired instance of MobileLoginRepository for accessing mobile login data.
	 */
	@Autowired
	MobileLoginRepository mobileLoginRepository;

	/**
	 * Endpoint to update the mobile number associated with a user account.
	 *
	 * @param header              Authorization header
	 * @param updateMobileRequest UpdateMobileRequest object containing the new
	 *                            mobile number
	 * @return ApiResponse containing the result of updating the mobile number
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@PutMapping("/mobile")
	public ApiResponse updateMobileNumber(@RequestHeader(value = "Authorization", required = false) String header,
			@RequestBody UpdateMobileRequest updateMobileRequest) {
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		return mobileUpdateService.updateMobileNumber(header, updateMobileRequest);
	}
}
