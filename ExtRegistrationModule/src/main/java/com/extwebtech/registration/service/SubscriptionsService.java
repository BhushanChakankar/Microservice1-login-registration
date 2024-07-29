package com.extwebtech.registration.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.Subscriptions;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.repository.RegistrationRepository;
import com.extwebtech.registration.repository.SubscriptionsRepository;
import com.extwebtech.registration.util.AccessPermissions;
import com.extwebtech.registration.util.JWTService;

/**
 * Service class responsible for managing subscription-related operations,
 * including CRUD operations and access control.
 */
@Service
public class SubscriptionsService {

	Logger logger = Logger.getLogger(SubscriptionsService.class);

	@Autowired
	private SubscriptionsRepository subscriptionsRepository;

	@Autowired
	private ApiResponseConfig apiResponseConfig;

	@Autowired
	AccessPermissions permissions;

	@Autowired
	JWTService jwtService;

	@Autowired
	RegistrationRepository registrationRepository;

	/**
	 * Saves a new subscription, checking for duplicates and validating access
	 * permissions.
	 *
	 * @param subscriptions The subscription to be saved.
	 * @param header        The authorization header containing the JWT token.
	 * @return ApiResponse indicating the success or failure of the operation.
	 */
	public ApiResponse saveSubscriptions(Subscriptions subscriptions, String header) {
		ApiResponse apiResponse = new ApiResponse();
		try {

			String token = header.substring(7);
			jwtService.validateToken1(token);
			int userId = jwtService.extractUserId(token);
			if (permissions.getAuthorization()) {
				int roleId = jwtService.extractRole(token);
				if (!permissions.hasAccess(roleId, apiResponseConfig.getSubscriptionsModule(),
						apiResponseConfig.getCreateOperation())) {
					throw new AccessDeniedException(apiResponseConfig.getAccessDeniedMessage());
				}
			}
			Subscriptions data = subscriptionsRepository.findByPlanName(subscriptions.getPlanName());

			if (data == null) {
				subscriptions.setCreatedBy(userId);
				Subscriptions savedsubscriptions = subscriptionsRepository.save(subscriptions);

				apiResponse.setStatus(apiResponseConfig.isSuccessResponseStatus());
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
				apiResponse.setData(savedsubscriptions);

				logger.info("Saved subscriptions");
				return apiResponse;

			} else {
				apiResponse.setStatus(apiResponseConfig.isErrorResponseStatus());
				apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
				apiResponse.setMessage("Subscription PlanName Already exist");
				apiResponse.setData(null);

				logger.info("Plane Name of  subscriptions already exist");
				return apiResponse;
			}

		} catch (Exception e) {
			apiResponse.setStatus(apiResponseConfig.isErrorResponseStatus());
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage());
			apiResponse.setData(null);

			logger.error("Failed to save subscriptions: " + e.getMessage());

			return apiResponse;
		}
	}

	/**
	 * Retrieves all subscriptions and returns an ApiResponse.
	 *
	 * @return ApiResponse containing the list of subscriptions or an error message.
	 */
	public ApiResponse getAllSubscriptions() {
		ApiResponse apiResponse = new ApiResponse();
		try {
			List<Subscriptions> subscriptionsList = subscriptionsRepository.findAll();

			apiResponse.setStatus(apiResponseConfig.isSuccessResponseStatus());
			apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
			apiResponse.setData(subscriptionsList);

			logger.info("Retrieved all subscriptions");

			return apiResponse;
		} catch (Exception e) {
			apiResponse.setStatus(apiResponseConfig.isErrorResponseStatus());
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage());
			apiResponse.setData(null);

			logger.error("Failed to retrieve subscriptions: " + e.getMessage());

			return apiResponse;
		}
	}

	/**
	 * Deletes a subscription with the given ID, checking for associated users and
	 * validating access permissions.
	 *
	 * @param id     The ID of the subscription to be deleted.
	 * @param header The authorization header containing the JWT token.
	 * @return ApiResponse indicating the success or failure of the delete
	 *         operation.
	 */
	public ApiResponse deleteSubscriptions(int id, String header) {
		ApiResponse apiResponse = new ApiResponse();
		try {
			String token = header.substring(7);
			jwtService.validateToken1(token);
			if (permissions.getAuthorization()) {
				int roleId = jwtService.extractRole(token);
				if (!permissions.hasAccess(roleId, apiResponseConfig.getSubscriptionsModule(),
						apiResponseConfig.getDeleteOperation())) {
					throw new AccessDeniedException(apiResponseConfig.getAccessDeniedMessage());
				}
			}

			Optional<Subscriptions> subscriptionsOptional = subscriptionsRepository.findById(id);

			if (subscriptionsOptional.isPresent()) {

				if (isSubscriptionAssignedToUser(id)) {
					apiResponse.setStatus(apiResponseConfig.isErrorResponseStatus());
					apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
					apiResponse.setMessage(
							"Cannot delete subscription with ID " + id + " because it is assigned to a user.");
					apiResponse.setData(null);
					return apiResponse;
				}

				subscriptionsRepository.deleteById(id);

				apiResponse.setStatus(apiResponseConfig.isSuccessResponseStatus());
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage() + id);
				apiResponse.setData(null);

				logger.info("Deleted subscriptions with id: " + id);

				return apiResponse;
			} else {
				apiResponse.setStatus(apiResponseConfig.isNotFoundStatus());
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getNotFoundMessage() + " with id " + id);
				apiResponse.setData(null);
				return apiResponse;
			}
		} catch (Exception e) {
			apiResponse.setStatus(apiResponseConfig.isErrorResponseStatus());
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage());
			apiResponse.setData(null);

			logger.error("Failed to delete subscriptions with id: " + id + " " + e.getMessage());

			return apiResponse;
		}
	}

	/**
	 * Checks if a subscription with the given ID is assigned to any user.
	 *
	 * @param subscriptionId The ID of the subscription to check.
	 * @return true if the subscription is assigned to any user, false otherwise.
	 */
	private boolean isSubscriptionAssignedToUser(int subscriptionId) {
		return registrationRepository.existsBySubscriptionId(subscriptionId);
	}

	/**
	 * Updates an existing subscription with the provided ID, validating access
	 * permissions and handling duplicates.
	 *
	 * @param id                   The ID of the subscription to be updated.
	 * @param updatedSubscriptions The updated subscription data.
	 * @param header               The authorization header containing the JWT
	 *                             token.
	 * @return ApiResponse indicating the success or failure of the update
	 *         operation.
	 */
	public ApiResponse updateSubscriptions(int id, Subscriptions updatedSubscriptions, String header) {
		ApiResponse apiResponse = new ApiResponse();
		try {
			String token = header.substring(7);
			jwtService.validateToken1(token);
			int userId = jwtService.extractUserId(token);
			if (permissions.getAuthorization()) {
				int roleId = jwtService.extractRole(token);
				if (!permissions.hasAccess(roleId, apiResponseConfig.getSubscriptionsModule(),
						apiResponseConfig.getUpdateOperation())) {
					throw new AccessDeniedException(apiResponseConfig.getAccessDeniedMessage());
				}
			}

			Subscriptions existingSubscriptions = subscriptionsRepository.findById(id).orElse(null);
			if (existingSubscriptions == null) {

				apiResponse.setStatus(apiResponseConfig.isNotFoundStatus());
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getNotFoundMessage() + " with id " + id);
				apiResponse.setData(null);
				return apiResponse;
			}
			if (updatedSubscriptions.getPlanName() != null) {
				if (!updatedSubscriptions.getPlanName().equalsIgnoreCase(existingSubscriptions.getPlanName())) {
					if (subscriptionsRepository.existsByPlanNameIgnoreCase(updatedSubscriptions.getPlanName())) {
						apiResponse.setStatus(apiResponseConfig.isErrorResponseStatus());
						apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
						apiResponse.setMessage("PlanName '" + updatedSubscriptions.getPlanName() + "' already exists.");
						apiResponse.setData(null);
						return apiResponse;
					}
				}
			}
			if (updatedSubscriptions.getPlanName() != null) {
				existingSubscriptions.setPlanName(updatedSubscriptions.getPlanName());
			}

			if (updatedSubscriptions.getPrice() != null) {
				existingSubscriptions.setPrice(updatedSubscriptions.getPrice());
			}

			if (updatedSubscriptions.getDurations() != null) {
				existingSubscriptions.setDurations(updatedSubscriptions.getDurations());
			}

			if (updatedSubscriptions.getRecommended() != null) {
				existingSubscriptions.setRecommended(updatedSubscriptions.getRecommended());
			}

			if (updatedSubscriptions.getFeatures() != null) {
				existingSubscriptions.setFeatures(updatedSubscriptions.getFeatures());
			}
			Boolean isActiveValue = updatedSubscriptions.isActive();
			if (isActiveValue != null) {
				existingSubscriptions.setActive(isActiveValue);
			}
			existingSubscriptions.setUpdatedBy(userId);
			Subscriptions saved = subscriptionsRepository.save(existingSubscriptions);

			apiResponse.setStatus(apiResponseConfig.isSuccessResponseStatus());
			apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
			apiResponse.setData(saved);

			return apiResponse;

		} catch (DataIntegrityViolationException e) {
			apiResponse.setStatus(apiResponseConfig.isErrorResponseStatus());
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage("PlanName '" + updatedSubscriptions.getPlanName() + "' already exists.");
			apiResponse.setData(null);
			return apiResponse;
		} catch (Exception e) {
			apiResponse.setStatus(apiResponseConfig.isErrorResponseStatus());
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(e.getMessage());
			apiResponse.setData(null);
			return apiResponse;
		}
	}

	/**
	 * Retrieves a subscription by its ID and returns an ApiResponse.
	 *
	 * @param id The ID of the subscription to retrieve.
	 * @return ApiResponse containing the retrieved subscription or an error
	 *         message.
	 */
	public ApiResponse getSubscriptionById(int id) {
		ApiResponse apiResponse = new ApiResponse();
		try {
			Optional<Subscriptions> subscriptionsOptional = subscriptionsRepository.findById(id);

			if (subscriptionsOptional.isPresent()) {
				Subscriptions subscription = subscriptionsOptional.get();

				apiResponse.setStatus(apiResponseConfig.isSuccessResponseStatus());
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
				apiResponse.setData(subscription);

				logger.info("Retrieved subscriptions with id: " + id);

				return apiResponse;
			} else {
				apiResponse.setStatus(apiResponseConfig.isNotFoundStatus());
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getNotFoundMessage() + id);
				apiResponse.setData(null);
				return apiResponse;
			}
		} catch (Exception e) {
			apiResponse.setStatus(apiResponseConfig.isErrorResponseStatus());
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage());
			apiResponse.setData(null);

			logger.error("Failed to retrieve subscriptions with id: " + id + " " + e.getMessage());

			return apiResponse;
		}
	}

}
