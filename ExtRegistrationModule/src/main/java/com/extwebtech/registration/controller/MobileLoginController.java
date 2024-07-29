package com.extwebtech.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.LoginRequest;
import com.extwebtech.registration.bean.LoginResponse;
import com.extwebtech.registration.bean.SendOtpMobile;
import com.extwebtech.registration.bean.VerifyOtpMobile;
import com.extwebtech.registration.service.MobileLoginService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.validation.Valid;

/**
 * Controller class for handling operations related to mobile-based login and
 * authentication.
 */
@RestController
@RequestMapping("/mobile")
@CrossOrigin("*")
public class MobileLoginController {
	/**
	 * Autowired instance of MobileLoginService for handling business logic related
	 * to mobile-based login.
	 */
	@Autowired
	MobileLoginService mobileLoginService;

	/**
	 * Endpoint to send an OTP (One-Time Password) via mobile for authentication.
	 *
	 * @param sendOtpRequest SendOtpMobile object containing mobile information
	 * @return ApiResponse containing the result of the OTP sending process
	 */
	@PostMapping("/sendotp")
	public ApiResponse sendOtp(@Valid @RequestBody SendOtpMobile sendOtpRequest) {
		return mobileLoginService.sendOtp(sendOtpRequest);
	}

	/**
	 * Endpoint to verify an OTP received via mobile for authentication.
	 *
	 * @param verifyOtpRequest VerifyOtpMobile object containing mobile and OTP
	 *                         information
	 * @return ApiResponse containing the result of the OTP verification process
	 */
	@PostMapping("/verifyotp")
	public ApiResponse verifyOtp(@Valid @RequestBody VerifyOtpMobile verifyOtpRequest) {
		return mobileLoginService.verifyOtp(verifyOtpRequest);
	}

	/**
	 * Endpoint for user login using mobile credentials.
	 *
	 * @param loginRequest LoginRequest object containing mobile and password
	 *                     information
	 * @return LoginResponse containing the result of the login process
	 * @throws JsonProcessingException if there is an issue processing JSON data
	 */
	@PostMapping("/login")
	public LoginResponse login(@RequestBody LoginRequest loginRequest) throws JsonProcessingException {
		return mobileLoginService.login(loginRequest);
	}
}
