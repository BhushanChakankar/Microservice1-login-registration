package com.extwebtech.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.UpdateEmailRequest;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.controller.EmailUpdateController;
import com.extwebtech.registration.exception.MissingAuthorizationHeaderException;
import com.extwebtech.registration.service.EmailUpdateService;

class EmailUpdateControllerTest {

	 private EmailUpdateController emailUpdateController;
	    private EmailUpdateService emailUpdateService;
	    private ApiResponseConfig apiResponseConfig;

	    @BeforeEach
	    void setUp() throws NoSuchFieldException, IllegalAccessException {
	        emailUpdateController = new EmailUpdateController();
	        emailUpdateService = mock(EmailUpdateService.class);
	        apiResponseConfig = mock(ApiResponseConfig.class);

	        setPrivateField(emailUpdateController, "emailUpdateService", emailUpdateService);
	        setPrivateField(emailUpdateController, "apiResponseConfig", apiResponseConfig);
	    }

	    @Test
	    void testUpdateEmailSuccess() {
	        // Arrange
	        String validToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4NDg1NDQzMjQ5Iiwicm9sZSI6MSwidXNlcklkIjoyMCwiaWF0IjoxNzAwNjI4MDk3LCJleHAiOjE3MDA2NjQwOTd9.qfmDN7OpcGuHc0TSlC-9e9rjFIagF4_7Zyv4cMkHuuY";
	        UpdateEmailRequest updateEmailRequest = new UpdateEmailRequest();
	        updateEmailRequest.setEmail("newEmail@example.com");

	        ApiResponse apiResponse = new ApiResponse();
	        apiResponse.setStatus(true);
	        apiResponse.setStatusCode(String.valueOf(HttpStatus.OK.value()));
	        apiResponse.setMessage("Email updated successfully");
	        apiResponse.setData(null);

	        when(apiResponseConfig.getPleaseProvideToken()).thenReturn("Please provide a valid token");
	        when(emailUpdateService.updateEmail(eq(validToken), eq(updateEmailRequest))).thenReturn(apiResponse);

	        // Act
	        ApiResponse response = emailUpdateController.updateEmail(validToken, updateEmailRequest);

	        // Assert
	      //  assertTrue(null);
//	        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
//	        assertEquals("Email updated successfully", response.getMessage());
//	        assertNull(response.getData());

	        verify(emailUpdateService, times(1)).updateEmail(eq(validToken), eq(updateEmailRequest));
	    }

	@Test
	void testUpdateEmailMissingAuthorizationHeader() {
		// Arrange
		UpdateEmailRequest updateEmailRequest = new UpdateEmailRequest();
		updateEmailRequest.setEmail("newEmail@example.com");

		when(apiResponseConfig.getPleaseProvideToken()).thenReturn("Please provide a valid token");

		// Act and Assert
		MissingAuthorizationHeaderException exception = assertThrows(MissingAuthorizationHeaderException.class,
				() -> emailUpdateController.updateEmail(null, updateEmailRequest));

		assertEquals("Please provide a valid token", exception.getMessage());
		verify(emailUpdateService, never()).updateEmail(any(), any());
	}
	
	 private void setPrivateField(Object target, String fieldName, Object value)
	            throws NoSuchFieldException, IllegalAccessException {
	        Field field = target.getClass().getDeclaredField(fieldName);
	        field.setAccessible(true);
	        field.set(target, value);
	    }
}
