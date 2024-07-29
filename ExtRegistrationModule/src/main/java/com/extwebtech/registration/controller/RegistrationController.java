package com.extwebtech.registration.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.User;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.exception.MissingAuthorizationHeaderException;
import com.extwebtech.registration.service.AWSS3Service;
import com.extwebtech.registration.service.RegistrationService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Controller class for handling user registration and related operations.
 */
@RestController
@RequestMapping("/register")
@CrossOrigin("*")
public class RegistrationController {
	/**
	 * Autowired instance of RegistrationService for handling user registration
	 * logic.
	 */
	@Autowired
	RegistrationService registrationService;
	/**
	 * Autowired instance of AWSS3Service for handling Amazon S3 interactions.
	 */
	@Autowired
	AWSS3Service awss3Service;
	/**
	 * Autowired instance of ApiResponseConfig for configuring API responses.
	 */
	@Autowired
	ApiResponseConfig apiResponseConfig;

//	@Autowired
//	RegistrationProducer registrationProducer;
	/**
	 * Endpoint to create a new user with the specified role.
	 *
	 * @param roleId            ID of the user role
	 * @param profilePhoto      Profile photo of the user
	 * @param officialDocuments List of official documents of the user
	 * @param name              User's name
	 * @param mobile            User's mobile number
	 * @param email             User's email address
	 * @param password          User's password
	 * @param businessName      User's business name
	 * @param businessAddress   User's business address
	 * @param ownerName         User's owner name
	 * @param gst               User's GST number
	 * @param accountHolderName User's account holder name
	 * @param accountName       User's account name
	 * @param ifscCode          User's IFSC code
	 * @param bankUpi           User's bank UPI
	 * @param languageId        User's language ID
	 * @param countryId         User's country ID
	 * @param extraField        Extra field for additional information
	 * @param fcmToken          Firebase Cloud Messaging token
	 * @return ApiResponse containing the result of user registration
	 */
	@PostMapping(path = "/{roleId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ApiResponse createUser(@PathVariable int roleId,
			@RequestPart(value = "profile_photo", required = false) MultipartFile profilePhoto,
			@RequestPart(value = "official_documents", required = false) List<MultipartFile> officialDocuments,
			@Valid @RequestParam(value = "name", required = false) @NotBlank(message = "Name is required") String name,
			@Valid @RequestParam(value = "mobile", required = false) @Pattern(regexp = "\\d{10}", message = "Mobile should be 10 digits") @NotBlank(message = "mobile is required") String mobile,
			@Valid @RequestParam(value = "email", required = false) @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format") @NotBlank(message = "email is required") String email,
			@Valid @RequestParam(value = "password", required = false) @NotBlank(message = "Password is required") String password,
			@RequestParam(value = "business_name", required = false) String businessName,
			@RequestParam(value = "business_address", required = false) String businessAddress,
			@RequestParam(value = "owner_name", required = false) String ownerName,
			@RequestParam(value = "gst", required = false) String gst,
			@RequestParam(value = "account_holder_name", required = false) String accountHolderName,
			@RequestParam(value = "account_name", required = false) String accountName,
			@RequestParam(value = "ifsc_code", required = false) String ifscCode,
			@RequestParam(value = "bank_upi", required = false) String bankUpi,
			@RequestParam(value = "languageId", required = false) Integer languageId,
			@RequestParam(value = "countryId", required = false) Integer countryId,
			@RequestParam(value = "extraField", required = false) String extraField,
			@RequestParam(value = "fcmToken", required = false) String fcmToken) {
		User user = new User();
		user.setName(name);
		user.setMobile(mobile);
		user.setEmail(email);
		user.setPassword(password);
		user.setBusinessName(businessName);
		user.setBusinessAddress(businessAddress);
		user.setOwnerName(ownerName);
		user.setGst(gst);
		user.setAccountHolderName(accountHolderName);
		user.setAccountName(accountName);
		user.setIfscCode(ifscCode);
		user.setBankUpi(bankUpi);
		user.setLanguageId(languageId);
		user.setCountryId(countryId);
		user.setExtraField(extraField);

//		KafkaEvent registrationEvent = new KafkaEvent();
//		registrationEvent.setTopic("registration");
//		registrationEvent.setUserId(1);
//		registrationEvent.setDeviceToken("cKLW0eaNm49iMmhGymGzOb:APA91bEkg6uuDgIKbUT8wmzPal8Y-33ark20xIt2pdgqaeq7RkuOOpgJNfvKsLAKS3oYfTf7N_w6ZqBYSxr8TuQovPO6jY9-nR5LuvC67V2cbtnV59rd5GTtCiYpQWi4pf4-Bt6BNy72");
//		registrationProducer.sendMessage(registrationEvent);
		return registrationService.saveUser(roleId, profilePhoto, user, officialDocuments, fcmToken);
	}

	/**
	 * Endpoint to update user information.
	 *
	 * @param header            Authorization header
	 * @param profilePhoto      Updated profile photo of the user
	 * @param officialDocuments Updated list of official documents of the user
	 * @param name              Updated user's name
	 * @param businessName      Updated user's business name
	 * @param businessAddress   Updated user's business address
	 * @param ownerName         Updated user's owner name
	 * @param gst               Updated user's GST number
	 * @param accountHolderName Updated user's account holder name
	 * @param accountName       Updated user's account name
	 * @param ifscCode          Updated user's IFSC code
	 * @param bankUpi           Updated user's bank UPI
	 * @param languageId        Updated user's language ID
	 * @param countryId         Updated user's country ID
	 * @param extraField        Updated extra field for additional information
	 * @return ApiResponse containing the result of updating user information
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse updateUser(@RequestHeader(name = "Authorization", required = false) String header,
			@RequestPart(value = "profile_photo", required = false) MultipartFile profilePhoto,
			@RequestPart(value = "official_documents", required = false) List<MultipartFile> officialDocuments,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "business_name", required = false) String businessName,
			@RequestParam(value = "business_address", required = false) String businessAddress,
			@RequestParam(value = "owner_name", required = false) String ownerName,
			@RequestParam(value = "gst", required = false) String gst,
			@RequestParam(value = "account_holder_name", required = false) String accountHolderName,
			@RequestParam(value = "account_name", required = false) String accountName,
			@RequestParam(value = "ifsc_code", required = false) String ifscCode,
			@RequestParam(value = "bank_upi", required = false) String bankUpi,
			@RequestParam(value = "languageId", required = false) Integer languageId,
			@RequestParam(value = "countryId", required = false) Integer countryId,
			@RequestParam(value = "extraField", required = false) String extraField) {

		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}

		User updatedUser = new User();
		updatedUser.setName(name);

		updatedUser.setBusinessName(businessName);
		updatedUser.setBusinessAddress(businessAddress);
		updatedUser.setOwnerName(ownerName);
		updatedUser.setGst(gst);
		updatedUser.setAccountHolderName(accountHolderName);
		updatedUser.setAccountName(accountName);
		updatedUser.setIfscCode(ifscCode);
		updatedUser.setBankUpi(bankUpi);
		updatedUser.setLanguageId(languageId);
		updatedUser.setCountryId(countryId);
		updatedUser.setExtraField(extraField);
		return registrationService.updateUser(header, profilePhoto, officialDocuments, updatedUser);

	}

	/**
	 * Endpoint to deactivate a user account.
	 *
	 * @param header Authorization header
	 * @return ApiResponse containing the result of deactivating the user account
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@DeleteMapping
	public ApiResponse deactivateUser(@RequestHeader(name = "Authorization", required = false) String header) {
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		return registrationService.makeUserInactive(header);

	}

	/**
	 * Endpoint to retrieve user ratings and reviews by user ID.
	 *
	 * @param header Authorization header
	 * @return ApiResponse containing the user ratings and reviews
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@GetMapping
	public ApiResponse getById(@RequestHeader(name = "Authorization", required = false) String header) {
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		return registrationService.getUserRatingReviewByUserId(header);
	}

//	@GetMapping("/all")
//	public ApiResponse getAllUsers() {
//		return registrationService.getAllUsersWithAverageRatings();
//	}

	/**
	 * Endpoint to retrieve all users with average ratings.
	 *
	 * @param page      Page number for pagination
	 * @param size      Number of items per page
	 * @param sortBy    Field to sort by
	 * @param sortOrder Sort order (asc or desc)
	 * @return ApiResponse containing the list of users with average ratings
	 */
	@GetMapping("/all")
	public ApiResponse getAllUsersWithAverageRatings(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder) {

		return registrationService.getAllUsersWithAverageRatings(page, size, sortBy, sortOrder);
	}

	/**
	 * Endpoint to retrieve user ratings and reviews by user ID (admin access).
	 *
	 * @param header Authorization header
	 * @param userId User ID
	 * @return ApiResponse containing the user ratings and reviews
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@GetMapping("/{userId}")
	public ApiResponse getByIdAdmin(@RequestHeader(name = "Authorization", required = false) String header,
			@PathVariable Integer userId) {
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		return registrationService.getUserRatingReviewByUserId(header, userId);
	}

	/**
	 * Endpoint to update the subscription for a user.
	 *
	 * @param header         Authorization header
	 * @param subscriptionId ID of the subscription to update
	 * @return ApiResponse containing the result of updating the subscription
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@PutMapping("/updateSubscription")
	public ApiResponse updateSubscriptionForUser(@RequestHeader(name = "Authorization", required = false) String header,
			@RequestParam Integer subscriptionId) {
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		return registrationService.updateSubscriptionForUser(header, subscriptionId);
	}

}
