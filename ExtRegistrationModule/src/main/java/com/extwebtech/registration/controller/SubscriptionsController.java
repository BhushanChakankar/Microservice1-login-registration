package com.extwebtech.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.extwebtech.registration.bean.Subscriptions;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.exception.MissingAuthorizationHeaderException;
import com.extwebtech.registration.service.SubscriptionsService;

import jakarta.validation.Valid;

/**
 * Controller class for handling subscription-related operations.
 */
@RestController
@RequestMapping("/subscriptions")
@CrossOrigin("*")
public class SubscriptionsController {
	/**
	 * Autowired instance of SubscriptionsService for handling subscription-related
	 * logic.
	 */
	@Autowired
	private SubscriptionsService subscriptionsService;
	/**
	 * Autowired instance of ApiResponseConfig for managing API response
	 * configurations.
	 */
	@Autowired
	ApiResponseConfig apiResponseConfig;

	/**
	 * Endpoint to save a new subscription.
	 *
	 * @param subscriptions Subscriptions object representing the new subscription
	 * @param header        Authorization header
	 * @return ApiResponse containing the result of saving the new subscription
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@PostMapping
	public ApiResponse saveSubscriptions(@Valid @RequestBody Subscriptions subscriptions,
			@RequestHeader(name = "Authorization", required = false) String header) {
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		return subscriptionsService.saveSubscriptions(subscriptions, header);
	}

	/**
	 * Endpoint to retrieve all subscriptions.
	 *
	 * @return ApiResponse containing the list of all subscriptions
	 */
	@GetMapping
	public ApiResponse getAllSubscriptions() {
		return subscriptionsService.getAllSubscriptions();
	}

	/**
	 * Endpoint to delete a subscription by its ID.
	 *
	 * @param id     ID of the subscription to be deleted
	 * @param header Authorization header
	 * @return ApiResponse containing the result of deleting the subscription
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@DeleteMapping("/{id}")
	public ApiResponse deleteSubscriptions(@PathVariable int id,
			@RequestHeader(name = "Authorization", required = false) String header) {
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		return subscriptionsService.deleteSubscriptions(id, header);
	}

	/**
	 * Endpoint to update a subscription by its ID.
	 *
	 * @param id            ID of the subscription to be updated
	 * @param subscriptions Subscriptions object representing the updated
	 *                      subscription information
	 * @param header        Authorization header
	 * @return ApiResponse containing the result of updating the subscription
	 * @throws MissingAuthorizationHeaderException if the token is missing or
	 *                                             invalid
	 */
	@PutMapping("/{id}")
	public ApiResponse updateSubscriptions(@PathVariable int id, @RequestBody Subscriptions subscriptions,
			@RequestHeader(name = "Authorization", required = false) String header) {
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		return subscriptionsService.updateSubscriptions(id, subscriptions, header);
	}

	/**
	 * Endpoint to retrieve a subscription by its ID.
	 *
	 * @param id ID of the subscription
	 * @return ApiResponse containing the subscription with the specified ID
	 */
	@GetMapping("/{id}")
	public ApiResponse getSubscriptionById(@PathVariable int id) {
		return subscriptionsService.getSubscriptionById(id);
	}

}
