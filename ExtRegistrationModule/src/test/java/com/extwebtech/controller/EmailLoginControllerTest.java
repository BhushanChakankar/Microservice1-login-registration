package com.extwebtech.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.LoginEmail;
import com.extwebtech.registration.bean.LoginResponse;
import com.extwebtech.registration.bean.SendOtpEmail;
import com.extwebtech.registration.bean.VerifyOtpEmail;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.controller.EmailLoginController;
import com.extwebtech.registration.exception.MissingAuthorizationHeaderException;
import com.extwebtech.registration.service.EmailLoginService;

@ExtendWith(MockitoExtension.class)
class EmailLoginControllerTest {

	@Mock
	private EmailLoginService emailLoginService;

	@Mock
	private ApiResponseConfig apiResponseConfig;

	@InjectMocks
	private EmailLoginController emailLoginController;

	@BeforeEach
	void setUp() {
		reset(apiResponseConfig);
	}

	@Test
	void testSendOtp() {
		SendOtpEmail sendOtpRequest = new SendOtpEmail();
		ApiResponse expectedApiResponse = new ApiResponse();
		when(emailLoginService.sendOtp(sendOtpRequest)).thenReturn(expectedApiResponse);
		ApiResponse response = emailLoginController.sendOtp(sendOtpRequest);
		assertEquals(expectedApiResponse, response);
	}

	@Test
	void testVerifyOtp() {
		VerifyOtpEmail verifyOtpRequest = new VerifyOtpEmail();
		ApiResponse expectedApiResponse = new ApiResponse();
		when(emailLoginService.verifyOtp(verifyOtpRequest)).thenReturn(expectedApiResponse);
		ApiResponse response = emailLoginController.verifyOtp(verifyOtpRequest);
		assertEquals(expectedApiResponse, response);
	}

	@Test
	void testLoginEmail() {
		LoginEmail loginEmail = new LoginEmail();
		LoginResponse expectedLoginResponse = new LoginResponse();
		when(emailLoginService.loginEmail(loginEmail)).thenReturn(expectedLoginResponse);
		LoginResponse response = emailLoginController.loginEmail(loginEmail);
		assertEquals(expectedLoginResponse, response);
	}

	@Test
	void testUpdateDeviceToken() {
		String header = "Bearer token";
		String deviceToken = "deviceToken";
		ApiResponse expectedApiResponse = new ApiResponse();
		lenient().when(apiResponseConfig.getPleaseProvideToken()).thenReturn("Please provide token");
		when(emailLoginService.updateDeviceToken(header, deviceToken)).thenReturn(expectedApiResponse);
		ApiResponse response = emailLoginController.updateDeviceToken(header, deviceToken);
		assertEquals(expectedApiResponse, response);
	}

	@Test
	void testUpdateDeviceTokenMissingHeader() {
		String header = null;
		String deviceToken = "deviceToken";
		when(apiResponseConfig.getPleaseProvideToken()).thenReturn("Please provide token");
		MissingAuthorizationHeaderException exception = assertThrows(MissingAuthorizationHeaderException.class,
				() -> emailLoginController.updateDeviceToken(header, deviceToken));

		assertEquals("Please provide token", exception.getMessage());
	}

	@Test
	void testLogout() {
		String header = "Bearer token";
		ApiResponse expectedApiResponse = new ApiResponse();
		lenient().when(apiResponseConfig.getPleaseProvideToken()).thenReturn("Please provide token");
		when(emailLoginService.logout(header)).thenReturn(expectedApiResponse);
		ApiResponse response = emailLoginController.logout(header);
		assertEquals(expectedApiResponse, response);
	}

	@Test
	void testLogoutMissingHeader() {
		String header = null;
		when(apiResponseConfig.getPleaseProvideToken()).thenReturn("Please provide token");
		MissingAuthorizationHeaderException exception = assertThrows(MissingAuthorizationHeaderException.class,
				() -> emailLoginController.logout(header));
		assertEquals("Please provide token", exception.getMessage());
	}
}
