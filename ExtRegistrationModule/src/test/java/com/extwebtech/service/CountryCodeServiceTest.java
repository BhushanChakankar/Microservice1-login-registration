package com.extwebtech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.CountryCode;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.repository.CountryCodeRepository;
import com.extwebtech.registration.service.CountryCodeService;

@ExtendWith(MockitoExtension.class)
class CountryCodeServiceTest {

	@Mock
	private CountryCodeRepository countryCodeRepository;

	@Mock
	private ApiResponseConfig apiResponseConfig;

	@InjectMocks
	private CountryCodeService countryCodeService;

	@Test
	void testGetAllCountryCodesException() {
	    when(countryCodeRepository.findAll()).thenThrow(new RuntimeException("Something went wrong"));
	    when(apiResponseConfig.getMethodStartMessage()).thenReturn("Method Start");
	    when(apiResponseConfig.getMethodErrorMessage()).thenReturn("Error occurred");
	    when(apiResponseConfig.getErrorResponseStatusCode()).thenReturn("500");
	    when(apiResponseConfig.getErrorResponseMessage()).thenReturn("Internal Server Error");

	    ApiResponse response = countryCodeService.getAllCountryCodes();

	    assertFalse(response.isStatus());
	    assertEquals("500", response.getStatusCode());
	    assertEquals("Internal Server Error", response.getMessage());
	    assertNull(response.getData());
	}

	@Test
	void testGetCountryCodeByIdException() {
		int countryId = 1;
		when(countryCodeRepository.findById(countryId)).thenThrow(new RuntimeException("Something went wrong"));
		when(apiResponseConfig.getMethodStartMessage()).thenReturn("Method Start");
		when(apiResponseConfig.getMethodErrorMessage()).thenReturn("Error occurred");
		when(apiResponseConfig.getErrorResponseStatusCode()).thenReturn("500");
		when(apiResponseConfig.getErrorResponseMessage()).thenReturn("Internal Server Error");

		ApiResponse response = countryCodeService.getCountryCodeById(countryId);

		assertFalse(response.isStatus());
		assertEquals("500", response.getStatusCode());
		assertEquals("Internal Server Error", response.getMessage());
		assertNull(response.getData());
	}

	@Test
	void testSaveCountryCodeException() {
		CountryCode countryCode = createCountryCode(1, "US", "USA");
		when(countryCodeRepository.save(countryCode)).thenThrow(new RuntimeException("Something went wrong"));
		when(apiResponseConfig.getMethodStartMessage()).thenReturn("Method Start");
		when(apiResponseConfig.getMethodErrorMessage()).thenReturn("Error occurred");
		when(apiResponseConfig.getErrorResponseStatusCode()).thenReturn("500");
		when(apiResponseConfig.getErrorResponseMessage()).thenReturn("Internal Server Error");

		ApiResponse response = countryCodeService.saveCountryCode(countryCode);

		assertFalse(response.isStatus());
		assertEquals("500", response.getStatusCode());
		assertEquals("Internal Server Error", response.getMessage());
		assertNull(response.getData());
	}

	@Test
	void testDeleteCountryCodeException() {
		int countryId = 1;
		doThrow(new RuntimeException("Something went wrong")).when(countryCodeRepository).deleteById(countryId);
		when(apiResponseConfig.getMethodStartMessage()).thenReturn("Method Start");
		when(apiResponseConfig.getMethodErrorMessage()).thenReturn("Error occurred");
		when(apiResponseConfig.getErrorResponseStatusCode()).thenReturn("500");
		when(apiResponseConfig.getErrorResponseMessage()).thenReturn("Internal Server Error");

		ApiResponse response = countryCodeService.deleteCountryCode(countryId);

		assertFalse(response.isStatus());
		assertEquals("500", response.getStatusCode());
		assertEquals("Internal Server Error", response.getMessage());
		assertNull(response.getData());
	}

	private CountryCode createCountryCode(Integer countryId, String countryCode, String countryName) {
		CountryCode country = new CountryCode();
		country.setCountryId(countryId);
		country.setCountryCode(countryCode);
		country.setCountryName(countryName);
		return country;
	}
}
