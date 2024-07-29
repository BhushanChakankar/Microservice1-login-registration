package com.extwebtech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.DeviceDetails;
import com.extwebtech.registration.bean.LoginEmail;
import com.extwebtech.registration.bean.LoginResponse;
import com.extwebtech.registration.bean.OtpDetails;
import com.extwebtech.registration.bean.SendOtpEmail;
import com.extwebtech.registration.bean.VerifyOtpEmail;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.repository.EmailLoginRepository;
import com.extwebtech.registration.repository.OtpDetailsRepository;
import com.extwebtech.registration.service.EmailLoginService;
import com.extwebtech.registration.util.AccessPermissions;
import com.extwebtech.registration.util.JWTService;
import com.extwebtech.registration.util.OtpService;

@SpringBootTest
@ComponentScan(basePackages = "com.extwebtech.service")
class EmailLoginServiceTest {

	@InjectMocks
	private EmailLoginService emailLoginService;

	@Mock
	private OtpService otpService;

	@Mock
	private EmailLoginRepository loginRepository;

	@Mock
	private JWTService jwtService;

	@Mock
	private ApiResponseConfig apiResponseConfig;

	@Mock
	private AccessPermissions permissions;

	@Mock
	private StringEncryptor jasyptStringEncryptor;

	@Mock
	private OtpDetailsRepository otpDetailsRepository;

//	@Test
//    void testSendOtpUserExists() {
//        // Arrange
//        when(loginRepository.checkIfUserExists(anyString())).thenReturn(true);
//        when(otpService.generateOtp()).thenReturn("123456");
//        when(otpDetailsRepository.save(any())).thenReturn(new OtpDetails());
//
//        // Act
//        SendOtpEmail sendOtpRequest = new SendOtpEmail();
//        sendOtpRequest.setEmail("test@example.com");
//        ApiResponse response = emailLoginService.sendOtp(sendOtpRequest);
//
//        // Assert
//        assertTrue(response.isStatus());
//        assertEquals(apiResponseConfig.getSuccessResponseStatusCode(), response.getStatusCode());
//        assertEquals(apiResponseConfig.getLoginOtpSentMessageTemplate(), response.getMessage());
//        assertNotNull(response.getData());
//
//        // Verify interactions
//        verify(otpService, times(1)).sendOtpByEmail(eq("test@example.com"), eq("123456"));
//        verify(otpDetailsRepository, times(1)).save(any(OtpDetails.class));
//    }
	
//	@Test
//	void testSendOtpException() {
//	    // Arrange
//	    when(loginRepository.checkIfUserExists(anyString())).thenReturn(true);
//	    when(otpService.generateOtp()).thenReturn("123456");
//	    doThrow(new RuntimeException("Simulated exception")).when(otpService).sendOtpByEmail(anyString(), anyString());
//
//	    // Act
//	    SendOtpEmail sendOtpRequest = new SendOtpEmail();
//	    sendOtpRequest.setEmail("test@example.com");
//
//	    // Assert and handle exception
//	    try {
//	        ApiResponse response = emailLoginService.sendOtp(sendOtpRequest);
//	        // Additional assertions if needed
//	    } catch (RuntimeException e) {
//	        // Handle the exception or perform necessary cleanup
//	    }
//
//	    // Verify interactions
//	    verify(otpService, times(1)).sendOtpByEmail(eq("test@example.com"), eq("123456"));
//	    verify(otpDetailsRepository, never()).save(any(OtpDetails.class));
//	}

//	@Test
//    void testSendOtpUserDoesNotExist() {
//        // Arrange
//        when(loginRepository.checkIfUserExists(anyString())).thenReturn(false);
//
//        // Act
//        SendOtpEmail sendOtpRequest = new SendOtpEmail();
//        sendOtpRequest.setEmail("nonexistent@example.com");
//        ApiResponse response = emailLoginService.sendOtp(sendOtpRequest);
//
//        // Assert
//        assertFalse(response.isStatus());
//        assertEquals(apiResponseConfig.getErrorResponseStatusCode(), response.getStatusCode());
//        assertEquals(apiResponseConfig.getRegistrationMessage(), response.getMessage());
//        assertNull(response.getData());
//
//        // Verify interactions
//        verify(otpService, never()).generateOtp();
//        verify(otpService, never()).sendOtpByEmail(anyString(), anyString());
//        verify(otpDetailsRepository, never()).save(any(OtpDetails.class));
//    }


//	@Test
//    void testVerifyOtpValid() {
//        // Arrange
//        when(loginRepository.checkIfUserExists(anyString())).thenReturn(true);
//
//        VerifyOtpEmail verifyOtpRequest = new VerifyOtpEmail();
//        verifyOtpRequest.setEmail("test@example.com");
//        verifyOtpRequest.setEnteredOtp("123456");
//
//        OtpDetails otpDetails = new OtpDetails();
//        otpDetails.setEmail("test@example.com");
//        otpDetails.setOtp("123456");
//        otpDetails.setCreatedDate(Timestamp.valueOf(LocalDateTime.now().minusMinutes(1))); // Simulate a recent OTP
//
//        when(otpDetailsRepository.getOtdDeatils(eq("test@example.com"))).thenReturn(otpDetails);
//
//        // Act
//        ApiResponse response = emailLoginService.verifyOtp(verifyOtpRequest);
//
//        // Assert
//        assertTrue(response.isStatus());
//        assertEquals(apiResponseConfig.getSuccessResponseStatusCode(), response.getStatusCode());
//        assertEquals(apiResponseConfig.getValidatedOtp(), response.getMessage());
//        assertNull(response.getData());
//    }

//	@Test
//    void testVerifyOtpExpired() {
//        // Arrange
//        when(loginRepository.checkIfUserExists(anyString())).thenReturn(true);
//
//        VerifyOtpEmail verifyOtpRequest = new VerifyOtpEmail();
//        verifyOtpRequest.setEmail("test@example.com");
//        verifyOtpRequest.setEnteredOtp("123456");
//
//        OtpDetails otpDetails = new OtpDetails();
//        otpDetails.setEmail("test@example.com");
//        otpDetails.setOtp("123456");
//        otpDetails.setCreatedDate(Timestamp.valueOf(LocalDateTime.now().minusMinutes(10))); // Simulate an expired OTP
//
//        when(otpDetailsRepository.getOtdDeatils(eq("test@example.com"))).thenReturn(otpDetails);
//
//        // Act
//        ApiResponse response = emailLoginService.verifyOtp(verifyOtpRequest);
//
//        // Assert
//        assertFalse(response.isStatus());
//        assertEquals(apiResponseConfig.isErrorResponseStatus(), response.getStatusCode());
//        assertEquals(apiResponseConfig.getOtpExpireMessage(), response.getMessage());
//        assertNull(response.getData());
//    }

//	@Test
//    void testVerifyOtpInvalid() {
//        // Arrange
//        when(loginRepository.checkIfUserExists(anyString())).thenReturn(true);
//
//        VerifyOtpEmail verifyOtpRequest = new VerifyOtpEmail();
//        verifyOtpRequest.setEmail("test@example.com");
//        verifyOtpRequest.setEnteredOtp("654321"); // Invalid OTP
//
//        OtpDetails otpDetails = new OtpDetails();
//        otpDetails.setEmail("test@example.com");
//        otpDetails.setOtp("123456");
//        otpDetails.setCreatedDate(Timestamp.valueOf(LocalDateTime.now().minusMinutes(1)));
//
//        when(otpDetailsRepository.getOtdDeatils(eq("test@example.com"))).thenReturn(otpDetails);
//
//        // Act
//        ApiResponse response = emailLoginService.verifyOtp(verifyOtpRequest);
//
//        // Assert
//        assertFalse(response.isStatus());
//        assertEquals(apiResponseConfig.isErrorResponseStatus(), response.getStatusCode());
//        assertEquals(apiResponseConfig.getValidateOtpInvalidOtpMessage(), response.getMessage());
//        assertNull(response.getData());
//    }

//	@Test
//    void testVerifyOtpUserDoesNotExist() {
//        // Arrange
//        when(loginRepository.checkIfUserExists(anyString())).thenReturn(false);
//
//        VerifyOtpEmail verifyOtpRequest = new VerifyOtpEmail();
//        verifyOtpRequest.setEmail("nonexistent@example.com");
//        verifyOtpRequest.setEnteredOtp("123456");
//
//        // Act
//        ApiResponse response = emailLoginService.verifyOtp(verifyOtpRequest);
//
//        // Assert
//        assertFalse(response.isStatus());
//        assertEquals(apiResponseConfig.isNotFoundStatus(), response.getStatusCode());
//        assertEquals("Please provide valid email", response.getMessage());
//        assertNull(response.getData());
//    }

//	@Test
//    void testLoginSuccess() {
//        // Arrange
//        when(loginRepository.checkIfUserExists(anyString())).thenReturn(true);
//
//        LoginEmail loginEmail = new LoginEmail();
//        loginEmail.setEmail("test@example.com");
//        loginEmail.setPassword("encryptedPassword");
//        loginEmail.setDeviceDetails(new DeviceDetails());
//        loginEmail.setDeviceToken("deviceToken");
//        loginEmail.setDeviceType("Android");
//
//        Object[] userData = {/* your user data here */};
//        when(loginRepository.findByEmail(eq(loginEmail))).thenReturn(userData);
//
//        when(jasyptStringEncryptor.decrypt(anyString())).thenReturn("decryptedPassword");
//
//        when(jwtService.generateToken(anyString(), anyInt(), anyInt())).thenReturn("testToken");
//
//        // Act
//        LoginResponse response = emailLoginService.loginEmail(loginEmail);
//
//        // Assert
//        assertTrue(response.isStatus());
//        assertEquals(apiResponseConfig.getSuccessResponseStatusCode(), response.getStatusCode());
//        assertEquals(apiResponseConfig.getSuccessResponseMessage(), response.getMessage());
//        assertNotNull(response.getData());
//        assertNotNull(response.getToken());
//    }

//	@Test
//    void testLoginIncorrectPassword() {
//        // Arrange
//        when(loginRepository.checkIfUserExists(anyString())).thenReturn(true);
//
//        LoginEmail loginEmail = new LoginEmail();
//        loginEmail.setEmail("test@example.com");
//        loginEmail.setPassword("incorrectPassword");
//        loginEmail.setDeviceDetails(new DeviceDetails());
//        loginEmail.setDeviceToken("deviceToken");
//        loginEmail.setDeviceType("Android");
//
//        when(loginRepository.getPassword(eq("test@example.com"))).thenReturn("encryptedPassword");
//        when(jasyptStringEncryptor.decrypt(eq("encryptedPassword"))).thenReturn("decryptedPassword");
//
//        // Act
//        LoginResponse response = emailLoginService.loginEmail(loginEmail);
//
//        // Assert
//        assertFalse(response.isStatus());
//        assertEquals(apiResponseConfig.isNotFoundStatus(), response.getStatusCode());
//        assertEquals("Please provide the valid Password!.", response.getMessage());
//        assertNull(response.getData());
//        assertNull(response.getToken());
//    }

//	@Test
//    void testLoginExpiredSubscription() {
//        // Arrange
//        when(loginRepository.checkIfUserExists(anyString())).thenReturn(true);
//
//        LoginEmail loginEmail = new LoginEmail();
//        loginEmail.setEmail("test@example.com");
//        loginEmail.setPassword("correctPassword");
//        loginEmail.setDeviceDetails(new DeviceDetails());
//        loginEmail.setDeviceToken("deviceToken");
//        loginEmail.setDeviceType("Android");
//
//        Object[] userData = {/* your user data here with expired subscription */};
//        when(loginRepository.findByEmail(eq(loginEmail))).thenReturn(userData);
//
//        when(jasyptStringEncryptor.decrypt(anyString())).thenReturn("decryptedPassword");
//
//        // Act
//        LoginResponse response = emailLoginService.loginEmail(loginEmail);
//
//        // Assert
//        assertFalse(response.isStatus());
//        assertEquals(apiResponseConfig.isErrorResponseStatus(), response.getStatusCode());
//        assertTrue(response.getMessage().contains("subscription has expired"));
//        assertNull(response.getData());
//        assertNull(response.getToken());
//    }

//	@Test
//    void testLoginUserDoesNotExist() {
//        // Arrange
//        when(loginRepository.checkIfUserExists(anyString())).thenReturn(false);
//
//        LoginEmail loginEmail = new LoginEmail();
//        loginEmail.setEmail("nonexistent@example.com");
//        loginEmail.setPassword("anyPassword");
//        loginEmail.setDeviceDetails(new DeviceDetails());
//        loginEmail.setDeviceToken("deviceToken");
//        loginEmail.setDeviceType("Android");
//
//        // Act
//        LoginResponse response = emailLoginService.loginEmail(loginEmail);
//
//        // Assert
//        assertFalse(response.isStatus());
//        assertEquals(apiResponseConfig.isNotFoundStatus(), response.getStatusCode());
//        assertEquals(apiResponseConfig.getRegistrationMessage(), response.getMessage());
//        assertNull(response.getData());
//        assertNull(response.getToken());
//    }

//	@Test
//	void testUpdateDeviceTokenSuccess() {
//	    // Arrange
//	    String validToken = "validToken";
//	    String deviceToken = "newDeviceToken";
//
//	    // Mocking JWT and Permissions
//	    when(jwtService.extractMobileNumber(eq(validToken))).thenReturn("mobileNumber");
//	    when(jwtService.validateToken1(eq(validToken))).thenReturn(true);
//	    when(jwtService.extractRole(eq(validToken))).thenReturn(1);
//	    when(permissions.getAuthorization()).thenReturn(true);
//	    when(permissions.hasAccess(eq(1), any(), any())).thenReturn(true);
//
//	    // Mocking Repository
//	    when(loginRepository.updateDeviceToken(eq("mobileNumber"), eq(deviceToken))).thenReturn(1);
//
//	    // Act
//	    ApiResponse response = emailLoginService.updateDeviceToken("Bearer " + validToken, deviceToken);
//
//	    // Assert
//	    assertTrue(response.isStatus(), "Expected a successful response");
//	    assertEquals(apiResponseConfig.getSuccessResponseStatusCode(), response.getStatusCode(), "Unexpected status code");
//	    assertEquals(apiResponseConfig.getSuccessResponseMessage(), response.getMessage(), "Unexpected message");
//	    assertNull(response.getData(), "Expected data to be null");
//
//	    // You may add additional assertions based on the specifics of your ApiResponse and service logic.
//	}

//
//	@Test
//	void testUpdateDeviceTokenAccessDenied() {
//		// Arrange
//		String validToken = "validToken";
//		String deviceToken = "newDeviceToken";
//
//		when(jwtService.extractMobileNumber(eq(validToken))).thenReturn("mobileNumber");
//		when(jwtService.validateToken1(eq(validToken))).thenReturn(true);
//		when(jwtService.extractRole(eq(validToken))).thenReturn(1); // Replace with a valid role
//		when(permissions.getAuthorization()).thenReturn(true);
//		when(permissions.hasAccess(eq(1), any(), any())).thenReturn(false);
//
//		// Act
//		ApiResponse response = emailLoginService.updateDeviceToken("Bearer " + validToken, deviceToken);
//
//		// Assert
//		assertFalse(response.isStatus());
//		assertEquals(apiResponseConfig.getNotFoundStatusCode(), response.getStatusCode());
//		assertEquals(apiResponseConfig.getAccessDeniedMessage(), response.getMessage());
//		assertNull(response.getData());
//	}

//	@Test
//	void testUpdateDeviceTokenUnauthorized() {
//		// Arrange
//		String invalidToken = "invalidToken";
//		String deviceToken = "newDeviceToken";
//
//		when(jwtService.validateToken1(eq(invalidToken))).thenReturn(false);
//
//		// Act
//		ApiResponse response = emailLoginService.updateDeviceToken("Bearer " + invalidToken, deviceToken);
//
//		// Assert
//		assertFalse(response.isStatus());
//		assertEquals(apiResponseConfig.getUnauthorizedStatusCode(), response.getStatusCode());
//		assertEquals(apiResponseConfig.getUnauthorizedMessage(), response.getMessage());
//		assertNull(response.getData());
//	}

	@Test
	void testUpdateDeviceTokenFailure() {
		// Arrange
		String validToken = "validToken";
		String deviceToken = "newDeviceToken";

		when(jwtService.extractMobileNumber(eq(validToken))).thenReturn("mobileNumber");
		when(jwtService.validateToken1(eq(validToken))).thenReturn(true);
		when(jwtService.extractRole(eq(validToken))).thenReturn(1); // Replace with a valid role
		when(permissions.getAuthorization()).thenReturn(true);
		when(permissions.hasAccess(eq(1), any(), any())).thenReturn(true);
		when(loginRepository.updateDeviceToken(eq("mobileNumber"), eq(deviceToken))).thenReturn(0);

		// Act
		ApiResponse response = emailLoginService.updateDeviceToken("Bearer " + validToken, deviceToken);

		// Assert
		assertFalse(response.isStatus());
		assertEquals(apiResponseConfig.getNotFoundStatusCode(), response.getStatusCode());
		assertEquals(apiResponseConfig.getUserNotFoundMessage(), response.getMessage());
		assertNull(response.getData());
	}

	@Test
	void testUpdateDeviceTokenError() {
		// Arrange
		String validToken = "validToken";
		String deviceToken = "newDeviceToken";

		when(jwtService.extractMobileNumber(eq(validToken))).thenReturn("mobileNumber");
		when(jwtService.validateToken1(eq(validToken))).thenThrow(new RuntimeException("Token validation failed"));

		// Act
		ApiResponse response = emailLoginService.updateDeviceToken("Bearer " + validToken, deviceToken);

		// Assert
		assertFalse(response.isStatus());
		assertEquals(apiResponseConfig.getErrorResponseStatusCode(), response.getStatusCode());
		assertTrue(response.getMessage().contains("Failed to update device token"));
		assertNull(response.getData());
	}

//	@Test
//	void testLogoutSuccess() {
//		// Arrange
//		String validToken = "validToken";
//
//		when(jwtService.extractMobileNumber(eq(validToken))).thenReturn("mobileNumber");
//		when(jwtService.validateToken1(eq(validToken))).thenReturn(true);
//		when(jwtService.extractRole(eq(validToken))).thenReturn(1); // Replace with a valid role
//		when(permissions.getAuthorization()).thenReturn(true);
//		when(permissions.hasAccess(eq(1), any(), any())).thenReturn(true);
//		when(loginRepository.deleteLoginDetails(eq("mobileNumber"))).thenReturn(1);
//
//		// Act
//		ApiResponse response = emailLoginService.logout("Bearer " + validToken);
//
//		// Assert
//		assertTrue(response.isStatus());
//		assertEquals(apiResponseConfig.getSuccessResponseStatusCode(), response.getStatusCode());
//		assertEquals("Logged out successfully.", response.getMessage());
//		assertNull(response.getData());
//	}
//
//	@Test
//	void testLogoutAccessDenied() {
//		// Arrange
//		String validToken = "validToken";
//
//		when(jwtService.extractMobileNumber(eq(validToken))).thenReturn("mobileNumber");
//		when(jwtService.validateToken1(eq(validToken))).thenReturn(true);
//		when(jwtService.extractRole(eq(validToken))).thenReturn(1); // Replace with a valid role
//		when(permissions.getAuthorization()).thenReturn(true);
//		when(permissions.hasAccess(eq(1), any(), any())).thenReturn(false);
//
//		// Act
//		ApiResponse response = emailLoginService.logout("Bearer " + validToken);
//
//		// Assert
//		assertFalse(response.isStatus());
//		assertEquals(apiResponseConfig.getNotFoundStatusCode(), response.getStatusCode());
//		assertEquals(apiResponseConfig.getAccessDeniedMessage(), response.getMessage());
//		assertNull(response.getData());
//	}

//	@Test
//	void testLogoutUnauthorized() {
//		// Arrange
//		String invalidToken = "invalidToken";
//
//		when(jwtService.validateToken1(eq(invalidToken))).thenReturn(false);
//
//		// Act
//		ApiResponse response = emailLoginService.logout("Bearer " + invalidToken);
//
//		// Assert
//		assertFalse(response.isStatus());
//		assertEquals(apiResponseConfig.getUnauthorizedStatusCode(), response.getStatusCode());
//		assertEquals(apiResponseConfig.getUnauthorizedMessage(), response.getMessage());
//		assertNull(response.getData());
//	}

	@Test
	void testLogoutFailure() {
		// Arrange
		String validToken = "validToken";

		when(jwtService.extractMobileNumber(eq(validToken))).thenReturn("mobileNumber");
		when(jwtService.validateToken1(eq(validToken))).thenReturn(true);
		when(jwtService.extractRole(eq(validToken))).thenReturn(1); // Replace with a valid role
		when(permissions.getAuthorization()).thenReturn(true);
		when(permissions.hasAccess(eq(1), any(), any())).thenReturn(true);
		when(loginRepository.deleteLoginDetails(eq("mobileNumber"))).thenReturn(0);

		// Act
		ApiResponse response = emailLoginService.logout("Bearer " + validToken);

		// Assert
		assertFalse(response.isStatus());
		assertEquals(apiResponseConfig.getNotFoundStatusCode(), response.getStatusCode());
		assertEquals(apiResponseConfig.getNotFoundMessage(), response.getMessage());
		assertNull(response.getData());
	}

	@Test
	void testLogoutError() {
		// Arrange
		String validToken = "validToken";

		when(jwtService.extractMobileNumber(eq(validToken))).thenReturn("mobileNumber");
		when(jwtService.validateToken1(eq(validToken))).thenThrow(new RuntimeException("Token validation failed"));

		// Act
		ApiResponse response = emailLoginService.logout("Bearer " + validToken);

		// Assert
		assertFalse(response.isStatus());
		assertEquals(apiResponseConfig.getErrorResponseStatusCode(), response.getStatusCode());
		assertTrue(response.getMessage().contains("Failed to logout"));
		assertNull(response.getData());
	}
}
