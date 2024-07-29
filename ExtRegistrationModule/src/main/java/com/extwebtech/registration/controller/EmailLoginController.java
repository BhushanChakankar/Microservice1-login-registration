package com.extwebtech.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.LoginEmail;
import com.extwebtech.registration.bean.LoginResponse;
import com.extwebtech.registration.bean.SendOtpEmail;
import com.extwebtech.registration.bean.VerifyOtpEmail;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.exception.MissingAuthorizationHeaderException;
import com.extwebtech.registration.service.EmailLoginService;

import jakarta.validation.Valid;

/**
 * Controller class for handling operations related to Email-based login and
 * authentication.
 */
@RestController
@RequestMapping("/email")
@CrossOrigin("*")
public class EmailLoginController {

	/**
	 * Autowired instance of EmailLoginService for handling business logic related
	 * to Email-based login.
	 */
	@Autowired
	EmailLoginService emailLoginService;

	/**
	 * Autowired instance of ApiResponseConfig for configuring API responses.
	 */
	@Autowired
	ApiResponseConfig apiResponseConfig;

	/**
	 * Endpoint to send an OTP (One-Time Password) via email for authentication.
	 *
	 * @param sendOtpRequest SendOtpEmail object containing email information
	 * @return ApiResponse containing the result of the OTP sending process
	 */
	@PostMapping("/sendotp")
	public ApiResponse sendOtp(@Valid @RequestBody SendOtpEmail sendOtpRequest) {
		return emailLoginService.sendOtp(sendOtpRequest);
	}

	/**
	 * Endpoint to verify an OTP received via email for authentication.
	 *
	 * @param verifyOtpRequest VerifyOtpEmail object containing email and OTP
	 *                         information
	 * @return ApiResponse containing the result of the OTP verification process
	 */
	@PostMapping("/verifyotp")
	public ApiResponse verifyOtp(@Valid @RequestBody VerifyOtpEmail verifyOtpRequest) {
		return emailLoginService.verifyOtp(verifyOtpRequest);
	}

	/**
	 * Endpoint for user login using email credentials.
	 *
	 * @param loginEmail LoginEmail object containing email and password information
	 * @return LoginResponse containing the result of the login process
	 */
	@PostMapping("/login")
	public LoginResponse loginEmail(@RequestBody LoginEmail loginEmail) {
		return emailLoginService.loginEmail(loginEmail);
	}

	/**
	 * Endpoint to update the device token for push notifications.
	 *
	 * @param header      Authorization header
	 * @param deviceToken New device token for push notifications
	 * @return ApiResponse containing the result of updating the device token
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@PutMapping("/updateFcm")
	public ApiResponse updateDeviceToken(@RequestHeader(name = "Authorization", required = false) String header,
			@RequestParam("deviceToken") String deviceToken) {
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		return emailLoginService.updateDeviceToken(header, deviceToken);
	}

	/**
	 * Endpoint to log out the user.
	 *
	 * @param header Authorization header
	 * @return ApiResponse containing the result of the logout process
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@PostMapping("/logout")
	public ApiResponse logout(@RequestHeader(name = "Authorization", required = false) String header) {
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		return emailLoginService.logout(header);
	}
}
