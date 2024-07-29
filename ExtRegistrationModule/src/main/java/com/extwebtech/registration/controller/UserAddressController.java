package com.extwebtech.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.UserAddress;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.exception.MissingAuthorizationHeaderException;
import com.extwebtech.registration.service.UserAddressService;

/**
 * Controller class for handling user address-related operations.
 */
@RestController
@RequestMapping(("/address"))
@CrossOrigin("*")
public class UserAddressController {
	/**
	 * Autowired instance of UserAddressService for handling user address-related
	 * logic.
	 */
	@Autowired
	private UserAddressService userAddressService;
	/**
	 * Autowired instance of ApiResponseConfig for managing API response
	 * configurations.
	 */
	@Autowired
	ApiResponseConfig apiResponseConfig;

	/**
	 * Endpoint to create a new user address.
	 *
	 * @param header      Authorization header
	 * @param userAddress UserAddress object representing the new user address
	 * @return ApiResponse containing the result of creating the new user address
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ApiResponse createAddress(@RequestHeader(value = "Authorization", required = false) String header,
			@RequestBody UserAddress userAddress) {
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		return userAddressService.createAddress(userAddress, header);
	}

	/**
	 * Endpoint to retrieve the active user address.
	 *
	 * @param header Authorization header
	 * @return ApiResponse containing the active user address
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@GetMapping("/active")
	public ApiResponse getAddress(@RequestHeader(value = "Authorization", required = false) String header) {
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		return userAddressService.getAddress(header);
	}

	/**
	 * Endpoint to update a user address by its ID.
	 *
	 * @param header      Authorization header
	 * @param addressId   ID of the user address to be updated
	 * @param userAddress UserAddress object representing the updated user address
	 *                    information
	 * @return ApiResponse containing the result of updating the user address
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@PutMapping("/{addressId}")
	public ApiResponse updateAddress(@RequestHeader(value = "Authorization", required = false) String header,
			@PathVariable Integer addressId, @RequestBody UserAddress userAddress) {
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		return userAddressService.updateAddress(header, addressId, userAddress);
	}

	/**
	 * Endpoint to delete a user address by its ID.
	 *
	 * @param header    Authorization header
	 * @param addressId ID of the user address to be deleted
	 * @return ApiResponse containing the result of deleting the user address
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@DeleteMapping("/{addressId}")
	public ApiResponse deleteAddress(@RequestHeader(value = "Authorization", required = false) String header,
			@PathVariable Integer addressId) {
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		return userAddressService.deleteAddress(header, addressId);
	}

	/**
	 * Endpoint to retrieve all user addresses for a given user ID.
	 *
	 * @param header Authorization header
	 * @return ApiResponse containing the list of user addresses for the specified
	 *         user
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@GetMapping
	public ApiResponse getAddressesByUserId(@RequestHeader(value = "Authorization", required = false) String header) {
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		return userAddressService.findByUserId(header);
	}
}
