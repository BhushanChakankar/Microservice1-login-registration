package com.extwebtech.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.CountryCode;
import com.extwebtech.registration.controller.CountryCodeController;
import com.extwebtech.registration.service.CountryCodeService;

@ExtendWith(MockitoExtension.class)
class CountryCodeControllerTest {

	@Mock
	private CountryCodeService countryCodeService;

	@InjectMocks
	private CountryCodeController countryCodeController;

	@Test
	void testGetAllCountryCodes() {
		List<CountryCode> countryCodes = Arrays.asList(createCountryCode(1, "US", "USA"),
				createCountryCode(2, "CA", "Canada"));
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setStatus(true);
		apiResponse.setStatusCode("200");
		apiResponse.setMessage("Success");
		apiResponse.setData(countryCodes);
		when(countryCodeService.getAllCountryCodes()).thenReturn(apiResponse);
		ApiResponse response = countryCodeController.getAllCountryCodes();
		assertEquals(true, response.isStatus());
		assertEquals("200", response.getStatusCode());
		assertEquals("Success", response.getMessage());
		assertEquals(countryCodes, response.getData());
	}

	@Test
	void testGetCountryCodeById() {
		int countryId = 1;
		CountryCode countryCode = createCountryCode(countryId, "US", "USA");
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setStatus(true);
		apiResponse.setStatusCode("200");
		apiResponse.setMessage("Success");
		apiResponse.setData(countryCode);
		when(countryCodeService.getCountryCodeById(countryId)).thenReturn(apiResponse);
		ApiResponse response = countryCodeController.getCountryCodeById(countryId);
		assertEquals(true, response.isStatus());
		assertEquals("200", response.getStatusCode());
		assertEquals("Success", response.getMessage());
		assertEquals(countryCode, response.getData());
	}

	@Test
	void testSaveCountryCode() {
		CountryCode countryCode = createCountryCode(1, "US", "USA");
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setStatus(true);
		apiResponse.setStatusCode("201");
		apiResponse.setMessage("Country code saved successfully");
		apiResponse.setData(countryCode);
		when(countryCodeService.saveCountryCode(countryCode)).thenReturn(apiResponse);
		ApiResponse response = countryCodeController.saveCountryCode(countryCode);
		assertEquals(true, response.isStatus());
		assertEquals("201", response.getStatusCode());
		assertEquals("Country code saved successfully", response.getMessage());
		assertEquals(countryCode, response.getData());
	}

	@Test
	void testDeleteCountryCode() {
		int countryId = 1;
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setStatus(true);
		apiResponse.setStatusCode("204");
		apiResponse.setMessage("Country code deleted successfully");
		when(countryCodeService.deleteCountryCode(countryId)).thenReturn(apiResponse);
		ApiResponse response = countryCodeController.deleteCountryCode(countryId);
		assertEquals(true, response.isStatus());
		assertEquals("204", response.getStatusCode());
		assertEquals("Country code deleted successfully", response.getMessage());
		assertEquals(null, response.getData());
	}

	private CountryCode createCountryCode(Integer countryId, String countryCode, String countryName) {
		CountryCode country = new CountryCode();
		country.setCountryId(countryId);
		country.setCountryCode(countryCode);
		country.setCountryName(countryName);
		return country;
	}
}
