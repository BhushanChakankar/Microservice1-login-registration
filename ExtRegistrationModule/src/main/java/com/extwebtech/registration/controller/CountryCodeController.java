package com.extwebtech.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.CountryCode;
import com.extwebtech.registration.service.CountryCodeService;

/**
 * Controller class for handling operations related to Country Codes.
 */
@RestController
@RequestMapping("/countryCodes")
@CrossOrigin("*")
public class CountryCodeController {
	@Autowired
	private CountryCodeService countryCodeService;

	/**
	 * Autowired instance of CountryCodeService for handling business logic related
	 * to Country Codes.
	 */
	@GetMapping
	public ApiResponse getAllCountryCodes() {
		return countryCodeService.getAllCountryCodes();
	}

	/**
	 * Endpoint to retrieve all country codes.
	 *
	 * @return ApiResponse containing the list of all country codes
	 */
	@GetMapping("/{countryId}")
	public ApiResponse getCountryCodeById(@PathVariable Integer countryId) {
		return countryCodeService.getCountryCodeById(countryId);
	}

	/**
	 * Endpoint to save a new country code.
	 *
	 * @param countryCode CountryCode object to be saved
	 * @return ApiResponse containing the result of saving the country code
	 */
	@PostMapping
	public ApiResponse saveCountryCode(@RequestBody CountryCode countryCode) {
		return countryCodeService.saveCountryCode(countryCode);
	}

	/**
	 * Endpoint to delete a country code by its ID.
	 *
	 * @param countryId The ID of the country code to delete
	 * @return ApiResponse containing the result of deleting the country code
	 */
	@DeleteMapping("/{countryId}")
	public ApiResponse deleteCountryCode(@PathVariable Integer countryId) {
		return countryCodeService.deleteCountryCode(countryId);
	}
}