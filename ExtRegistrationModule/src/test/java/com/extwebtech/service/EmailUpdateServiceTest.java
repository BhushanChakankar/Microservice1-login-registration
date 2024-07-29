package com.extwebtech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.AccessDeniedException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.UpdateEmailRequest;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.repository.EmailUpdateRepository;
import com.extwebtech.registration.service.EmailUpdateService;
import com.extwebtech.registration.util.AccessPermissions;
import com.extwebtech.registration.util.JWTService;

class EmailUpdateServiceTest {

    private EmailUpdateService emailUpdateService;
    private EmailUpdateRepository emailUpdateRepository;
    private JWTService jwtService;
    private AccessPermissions accessPermissions;
    private ApiResponseConfig apiResponseConfig;

    @BeforeEach
    void setUp() {
        emailUpdateRepository = mock(EmailUpdateRepository.class);
        jwtService = mock(JWTService.class);
        accessPermissions = mock(AccessPermissions.class);
        apiResponseConfig = mock(ApiResponseConfig.class);

        emailUpdateService = new EmailUpdateService();
        ReflectionTestUtils.setField(emailUpdateService, "emailUpdateRepository", emailUpdateRepository);
        ReflectionTestUtils.setField(emailUpdateService, "jwtService", jwtService);
        ReflectionTestUtils.setField(emailUpdateService, "permissions", accessPermissions);
        ReflectionTestUtils.setField(emailUpdateService, "apiResponseConfig", apiResponseConfig);
    }

//    @Test
//    void testUpdateEmailSuccess() throws AccessDeniedException {
//        // Arrange
//        String validToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4NDg1NDQzMjQ5Iiwicm9sZSI6MSwidXNlcklkIjoyMCwiaWF0IjoxNzAwNjI4MDk3LCJleHAiOjE3MDA2NjQwOTd9.qfmDN7OpcGuHc0TSlC-9e9rjFIagF4_7Zyv4cMkHuuY";
//        UpdateEmailRequest updateEmailRequest = new UpdateEmailRequest();
//        updateEmailRequest.setEmail("newEmail@example.com");
//
//        ApiResponse apiResponse = new ApiResponse();
//        apiResponse.setStatus(true);
//        apiResponse.setStatusCode("200");
//        apiResponse.setMessage("Email updated successfully");
//        apiResponse.setData(null);
//
//        when(apiResponseConfig.isSuccessResponseStatus()).thenReturn(true);
//        when(apiResponseConfig.getSuccessResponseStatusCode()).thenReturn("200");
//        when(apiResponseConfig.getSuccessResponseMessage()).thenReturn("Email updated successfully");
//        when(jwtService.extractUserId(validToken)).thenReturn(1);
//        when(jwtService.extractRole(validToken)).thenReturn(2);
//        when(accessPermissions.getAuthorization()).thenReturn(true);
//        when(accessPermissions.hasAccess(2, "login", "update")).thenReturn(true);
//        when(emailUpdateRepository.updateEmailById(anyInt(), anyString())).thenReturn(1);
//
//        // Act
//        ApiResponse response = emailUpdateService.updateEmail( validToken, updateEmailRequest);
//
//        // Assert
//        assertEquals(true, response.isStatus());
//        assertEquals("200", response.getStatusCode());
//        assertEquals("Email updated successfully", response.getMessage());
//        assertEquals(null, response.getData());
//
//        // Verify that methods were called as expected
//        verify(jwtService).validateToken1(validToken);
//        verify(jwtService).extractUserId(validToken);
//        verify(jwtService).extractRole(validToken);
//        verify(accessPermissions).getAuthorization();
//        verify(accessPermissions).hasAccess(2, "login", "update");
//        verify(emailUpdateRepository).updateEmailById(1, "newEmail@example.com");
//    }


//    @Test
//    void testUpdateEmailAccessDenied() throws AccessDeniedException {
//        // Arrange
//        String validToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4NDg1NDQzMjQ5Iiwicm9sZSI6MSwidXNlcklkIjoyMCwiaWF0IjoxNzAwNjI4MDk3LCJleHAiOjE3MDA2NjQwOTd9.qfmDN7OpcGuHc0TSlC-9e9rjFIagF4_7Zyv4cMkHuuY";
//        UpdateEmailRequest updateEmailRequest = new UpdateEmailRequest();
//        updateEmailRequest.setEmail("newEmail@example.com");
//
//        when(apiResponseConfig.getAccessDeniedMessage()).thenReturn("Access denied");
//        when(jwtService.extractUserId(validToken)).thenReturn(1);
//        when(jwtService.extractRole(validToken)).thenReturn(2);
//        when(accessPermissions.getAuthorization()).thenReturn(true);
//        when(accessPermissions.hasAccess(1, "registration", "C")).thenReturn(false);
//
//        // Act
//        ApiResponse response = emailUpdateService.updateEmail( validToken, updateEmailRequest);
//
//        // Assert
//        assertEquals(false, response.isStatus());
//        assertEquals("403", response.getStatusCode());
//        assertEquals("Access denied", response.getMessage());
//        assertEquals(null, response.getData());
//
//        // Verify that methods were called as expected
//        verify(jwtService).validateToken1(validToken);
//        verify(jwtService).extractUserId(validToken);
//        verify(jwtService).extractRole(validToken);
//        verify(accessPermissions).getAuthorization();
//        verify(accessPermissions).hasAccess(1, "registration", "C");
//        // Ensure that updateEmailById was not called in this case
//        verify(emailUpdateRepository, never()).updateEmailById(anyInt(), anyString());
//    }

//    @Test
//    void testUpdateEmailException() throws AccessDeniedException {
//        // Arrange
//        String validToken = "validToken";
//        UpdateEmailRequest updateEmailRequest = new UpdateEmailRequest();
//        updateEmailRequest.setEmail("newEmail@example.com");
//
//        when(apiResponseConfig.getErrorResponseStatusCode()).thenReturn("500");
//        when(emailUpdateRepository.updateEmailById(anyInt(), anyString())).thenThrow(new RuntimeException("Database error"));
//
//        // Act
//        ApiResponse response = emailUpdateService.updateEmail("Bearer " + validToken, updateEmailRequest);
//
//        // Assert
//        assertEquals(false, response.isStatus());
//        assertEquals("500", response.getStatusCode());
//        assertEquals("Failed to update email: Database error", response.getMessage());
//        assertEquals(null, response.getData());
//
//        // Verify that methods were called as expected
//        verify(jwtService).validateToken1(validToken);
//        // Ensure that extractUserId and extractRole were not called in this case
//        verify(jwtService, never()).extractUserId(validToken);
//        verify(jwtService, never()).extractRole(validToken);
//        verify(accessPermissions, never()).getAuthorization();
//        verify(accessPermissions, never()).hasAccess(anyInt(), anyString(), anyString());
//        verify(emailUpdateRepository).updateEmailById(anyInt(), anyString());
//    }

//    @Test
//    void testUpdateEmailTokenValidationFailure() throws AccessDeniedException {
//        // Arrange
//        String invalidToken = "invalidToken";
//        UpdateEmailRequest updateEmailRequest = new UpdateEmailRequest();
//        updateEmailRequest.setEmail("newEmail@example.com");
//
//        doThrow(new RuntimeException("Invalid token")).when(jwtService).validateToken1(invalidToken);
//
//        // Act
//        ApiResponse response = emailUpdateService.updateEmail("Bearer " + invalidToken, updateEmailRequest);
//
//        // Assert
//        assertEquals(false, response.isStatus());
//        assertEquals("401", response.getStatusCode());
//        assertEquals("Failed to update email: Invalid token", response.getMessage());
//        assertEquals(null, response.getData());
//
//        // Verify that methods were called as expected
//        verify(jwtService).validateToken1(invalidToken);
//        // Ensure that extractUserId, extractRole, getAuthorization, and hasAccess were not called in this case
//        verify(jwtService, never()).extractUserId(invalidToken);
//        verify(jwtService, never()).extractRole(invalidToken);
//        verify(accessPermissions, never()).getAuthorization();
//        verify(accessPermissions, never()).hasAccess(anyInt(), anyString(), anyString());
//        // Ensure that updateEmailById was not called in this case
//        verify(emailUpdateRepository, never()).updateEmailById(anyInt(), anyString());
//    }
}
