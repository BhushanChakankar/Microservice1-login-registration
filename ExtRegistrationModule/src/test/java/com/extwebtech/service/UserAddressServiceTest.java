package com.extwebtech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.UserAddress;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.repository.UserAddressRepository;
import com.extwebtech.registration.service.UserAddressService;
import com.extwebtech.registration.util.AccessPermissions;
import com.extwebtech.registration.util.JWTService;
import com.extwebtech.registration.util.NotificationUtil;

@SpringBootTest
public class UserAddressServiceTest {

	@Mock
	private UserAddressRepository userAddressRepository;

	@Mock
	private JWTService jwtService;

	@Mock
	private AccessPermissions permissions;

	@Mock
	private NotificationUtil notificationUtil;

	@InjectMocks
	private UserAddressService userAddressService;
	
	@Mock
	private ApiResponseConfig apiResponseConfig;

	@Test
    void testCreateAddress_Success() {
        when(jwtService.validateToken1(any())).thenReturn(true);
        when(jwtService.extractUserId(any())).thenReturn(1);
        when(jwtService.extractRole(any())).thenReturn(1);
        when(permissions.getAuthorization()).thenReturn(true);
        when(permissions.hasAccess(anyInt(), any(), any())).thenReturn(false);
        when(userAddressRepository.findByUserId(anyInt())).thenReturn(Arrays.asList());
        UserAddress userAddress = new UserAddress();
        userAddress.setAddressId(123);
        ApiResponse apiResponse = userAddressService.createAddress(userAddress, "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMTExMTExMTExIiwicm9sZSI6MSwidXNlcklkIjo4NywiaWF0IjoxNzAxNDIyODA4LCJleHAiOjE3MDE0NTg4MDh9.y044I3ON8qbt75IKqPoinsLXTJje-bZfGs8Cp_2WYZw");
        assertTrue(apiResponse.isStatus());
        assertEquals(200, apiResponse.getStatusCode());
        assertNotNull(apiResponse.getData());
    }

	@Test
    void testGetAddress_Success() {
        when(jwtService.validateToken1(any())).thenReturn(true);
        when(jwtService.extractUserId(any())).thenReturn(1);
        when(permissions.getAuthorization()).thenReturn(true);
        when(permissions.hasAccess(anyInt(), any(), any())).thenReturn(false);
        when(userAddressRepository.findByUserId(anyInt())).thenReturn(Arrays.asList(new UserAddress()));

        // Test method
        ApiResponse apiResponse = userAddressService.getAddress("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMTExMTExMTExIiwicm9sZSI6MSwidXNlcklkIjo4NywiaWF0IjoxNzAxNDIyODA4LCJleHAiOjE3MDE0NTg4MDh9.y044I3ON8qbt75IKqPoinsLXTJje-bZfGs8Cp_2WYZw");

        // Assertions
        assertTrue(apiResponse.isStatus());
        assertEquals(200, apiResponse.getStatusCode());
        assertNotNull(apiResponse.getData());
    }

	@Test
    void testGetAddress_AddressNotFound() {
        when(jwtService.validateToken1(any())).thenReturn(true);
        when(jwtService.extractUserId(any())).thenReturn(1);
        when(permissions.getAuthorization()).thenReturn(true);
        when(permissions.hasAccess(anyInt(), any(), any())).thenReturn(false);
        when(userAddressRepository.findByUserId(anyInt())).thenReturn(Arrays.asList());
        ApiResponse apiResponse = userAddressService.getAddress("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMTExMTExMTExIiwicm9sZSI6MSwidXNlcklkIjo4NywiaWF0IjoxNzAxNDIyODA4LCJleHAiOjE3MDE0NTg4MDh9.y044I3ON8qbt75IKqPoinsLXTJje-bZfGs8Cp_2WYZw");
        assertFalse(apiResponse.isStatus());
        assertEquals(404, apiResponse.getStatusCode());
        assertNull(apiResponse.getData());
    }

	@Test
    void testDeleteAddress_Success() {
        when(jwtService.validateToken1(any())).thenReturn(true);
        when(jwtService.extractUserId(any())).thenReturn(1);
        when(jwtService.extractRole(any())).thenReturn(1);
        when(permissions.getAuthorization()).thenReturn(true);
        when(permissions.hasAccess(anyInt(), any(), any())).thenReturn(false);
        when(userAddressRepository.findByUserIdAndAddressId(anyInt(), anyInt())).thenReturn(java.util.Optional.of(new UserAddress()));
        ApiResponse apiResponse = userAddressService.deleteAddress("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMTExMTExMTExIiwicm9sZSI6MSwidXNlcklkIjo4NywiaWF0IjoxNzAxNDIyODA4LCJleHAiOjE3MDE0NTg4MDh9.y044I3ON8qbt75IKqPoinsLXTJje-bZfGs8Cp_2WYZw", 1);
        assertTrue(apiResponse.isStatus());
        assertEquals(200, apiResponse.getStatusCode());
        assertNull(apiResponse.getData());
    }

	@Test
    void testDeleteAddress_AddressNotFound() {
        when(jwtService.validateToken1(any())).thenReturn(true);
        when(jwtService.extractUserId(any())).thenReturn(1);
        when(jwtService.extractRole(any())).thenReturn(1);
        when(permissions.getAuthorization()).thenReturn(true);
        when(permissions.hasAccess(anyInt(), any(), any())).thenReturn(false);
        when(userAddressRepository.findByUserIdAndAddressId(anyInt(), anyInt())).thenReturn(java.util.Optional.empty());
        ApiResponse apiResponse = userAddressService.deleteAddress("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMTExMTExMTExIiwicm9sZSI6MSwidXNlcklkIjo4NywiaWF0IjoxNzAxNDIyODA4LCJleHAiOjE3MDE0NTg4MDh9.y044I3ON8qbt75IKqPoinsLXTJje-bZfGs8Cp_2WYZw", 1);
        assertFalse(apiResponse.isStatus());
        assertEquals(404, apiResponse.getStatusCode());
        assertNull(apiResponse.getData());
    }

	@Test
    void testUpdateAddress_Success() {
        when(jwtService.validateToken1(any())).thenReturn(true);
        when(jwtService.extractUserId(any())).thenReturn(1);
        when(jwtService.extractRole(any())).thenReturn(1);
        when(permissions.getAuthorization()).thenReturn(true);
        when(permissions.hasAccess(anyInt(), any(), any())).thenReturn(false);
        when(userAddressRepository.findByUserIdAndAddressId(anyInt(), anyInt())).thenReturn(java.util.Optional.of(new UserAddress()));
        when(userAddressRepository.findByUserId(anyInt())).thenReturn(Arrays.asList(new UserAddress()));
        UserAddress updatedUserAddress = new UserAddress();
        ApiResponse apiResponse = userAddressService.updateAddress("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMTExMTExMTExIiwicm9sZSI6MSwidXNlcklkIjo4NywiaWF0IjoxNzAxNDIyODA4LCJleHAiOjE3MDE0NTg4MDh9.y044I3ON8qbt75IKqPoinsLXTJje-bZfGs8Cp_2WYZw", 33, updatedUserAddress);
        assertTrue(apiResponse.isStatus());
        assertEquals(200, apiResponse.getStatusCode());
        assertEquals(404, apiResponse.getStatusCode());
        assertNotNull(apiResponse.getData());
    }

	@Test
    void testFindByUserId_Success() {
        when(jwtService.validateToken1(any())).thenReturn(true);
        when(jwtService.extractUserId(any())).thenReturn(1);
        when(permissions.getAuthorization()).thenReturn(true);
        when(permissions.hasAccess(anyInt(), any(), any())).thenReturn(false);
        when(userAddressRepository.findByUserId(anyInt())).thenReturn(Arrays.asList(new UserAddress()));
        ApiResponse apiResponse = userAddressService.findByUserId("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMTExMTExMTExIiwicm9sZSI6MSwidXNlcklkIjo4NywiaWF0IjoxNzAxNDIyODA4LCJleHAiOjE3MDE0NTg4MDh9.y044I3ON8qbt75IKqPoinsLXTJje-bZfGs8Cp_2WYZw");
        assertTrue(apiResponse.isStatus());
        assertEquals(200, apiResponse.getStatusCode());
        assertNotNull(apiResponse.getData());
    }

	@Test
    void testFindByUserId_NoAddressesFound() {
        when(jwtService.validateToken1(any())).thenReturn(true);
        when(jwtService.extractUserId(any())).thenReturn(1);
        when(permissions.getAuthorization()).thenReturn(true);
        when(permissions.hasAccess(anyInt(), any(), any())).thenReturn(false);
        when(userAddressRepository.findByUserId(anyInt())).thenReturn(Arrays.asList());
        ApiResponse apiResponse = userAddressService.findByUserId("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMTExMTExMTExIiwicm9sZSI6MSwidXNlcklkIjo4NywiaWF0IjoxNzAxNDIyODA4LCJleHAiOjE3MDE0NTg4MDh9.y044I3ON8qbt75IKqPoinsLXTJje-bZfGs8Cp_2WYZw");
        assertFalse(apiResponse.isStatus());
        assertEquals(404, apiResponse.getStatusCode());
        assertNull(apiResponse.getData());
    }
}
