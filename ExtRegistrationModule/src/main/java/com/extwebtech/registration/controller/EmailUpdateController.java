package com.extwebtech.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.UpdateEmailRequest;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.exception.MissingAuthorizationHeaderException;
import com.extwebtech.registration.service.EmailUpdateService;

/**
 * Controller class for handling operations related to updating email addresses.
 */
@RestController
@RequestMapping("/update")
@CrossOrigin("*")
public class EmailUpdateController {

	/**
	 * Autowired instance of EmailUpdateService for handling business logic related
	 * to updating email addresses.
	 */
	@Autowired
	EmailUpdateService emailUpdateService;
	/**
	 * Autowired instance of ApiResponseConfig for configuring API responses.
	 */
	@Autowired
	ApiResponseConfig apiResponseConfig;

	/**
	 * Endpoint to update the email address associated with a user account.
	 *
	 * @param header             Authorization header
	 * @param updateEmailRequest UpdateEmailRequest object containing the new email
	 *                           address
	 * @return ApiResponse containing the result of updating the email address
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@PutMapping("/email")
	public ApiResponse updateEmail(@RequestHeader(value = "Authorization", required = false) String header,
			@RequestBody UpdateEmailRequest updateEmailRequest) {
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		return emailUpdateService.updateEmail(header, updateEmailRequest);
	}
}
