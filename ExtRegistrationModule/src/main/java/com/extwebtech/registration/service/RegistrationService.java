package com.extwebtech.registration.service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.PaginationResponse;
import com.extwebtech.registration.bean.Subscriptions;
import com.extwebtech.registration.bean.User;
import com.extwebtech.registration.bean.UserAverageRatingDTO;
import com.extwebtech.registration.bean.UserRatingReview;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.repository.EmailLoginRepository;
import com.extwebtech.registration.repository.MobileLoginRepository;
import com.extwebtech.registration.repository.RegistrationRepository;
import com.extwebtech.registration.repository.UserRatingRepository;
import com.extwebtech.registration.util.AccessPermissions;
import com.extwebtech.registration.util.JWTService;
import com.extwebtech.registration.util.NotificationUtil;

import jakarta.transaction.Transactional;

@Service
public class RegistrationService {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.LogManager
			.getLogger(RegistrationService.class);
	@Autowired
	AWSS3Service awss3Service;

	@Autowired
	RegistrationRepository registrationRepository;
	@Autowired
	EmailLoginRepository emailLoginRepository;
	@Autowired
	MobileLoginRepository mobileLoginRepository;

	@Autowired
	JWTService jwtService;

	@Autowired
	ApiResponseConfig apiResponseConfig;

	@Autowired
	AccessPermissions permissions;

	@Autowired
	UserRatingRepository ratingRepository;

	@Autowired
	NotificationUtil notificationUtil;

	@Autowired
	private StringEncryptor jasyptStringEncryptor;

	/**
	 * Generates a referral code based on user's name and UUID.
	 *
	 * @param name The user's name.
	 * @return The generated referral code.
	 */
	private String generateReferralCode(String name) {
		String code = UUID.randomUUID().toString().substring(0, 3);
		String s = code + name.substring(0, 3);
		return s;
	}

	/**
	 * Saves a new user with the provided information.
	 *
	 * @param roleId            The role ID of the user.
	 * @param profilePhoto      The profile photo of the user.
	 * @param user              The user object containing user information.
	 * @param officialDocuments List of official documents.
	 * @param fcmToken          The FCM token for push notifications.
	 * @return An ApiResponse indicating the status of the user creation process.
	 */
	public ApiResponse saveUser(int roleId, MultipartFile profilePhoto, User user,
			List<MultipartFile> officialDocuments, String fcmToken) {
		logger.info(apiResponseConfig.getMethodStartMessage() + "saveUser" + user.getEmail());
		String photoUrl = null;
		ApiResponse apiResponse = new ApiResponse();
		try {
			boolean userExistsWithEmail = emailLoginRepository.checkIfUserExists(user.getEmail());
			boolean userExistsWithPhoneNumber = mobileLoginRepository.checkIfUserExists(user.getMobile());
			if (userExistsWithEmail || userExistsWithPhoneNumber) {
				apiResponse.setStatus(false);
				apiResponse.setStatusCode("403");
				if (userExistsWithEmail) {
					apiResponse.setMessage(apiResponseConfig.getDuplicateUser());
				} else {
					apiResponse.setMessage(apiResponseConfig.getDuplicateMobileNumberErrorMessage());
				}

				return apiResponse;
			}

			List<String> documentPaths = new ArrayList<>();
			if (officialDocuments != null && !officialDocuments.isEmpty()) {
				for (MultipartFile document : officialDocuments) {
					String documentPath = awss3Service.uploadFile(document);

					documentPaths.add(documentPath);
				}
			}

			user.setOfficialDocuments(documentPaths);

			if (profilePhoto != null) {
				photoUrl = awss3Service.uploadFile(profilePhoto);
			}

			user.setProfilePhoto(photoUrl);
			user.setRoleId(roleId);
			String encryptedPassword = jasyptStringEncryptor.encrypt(user.getPassword());
			user.setPassword(encryptedPassword);
			user.setReferralCode(generateReferralCode(user.getName()));
			User savedUser = registrationRepository.save(user);

			apiResponse.setStatus(true);
			apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
			apiResponse.setData(savedUser);
			logger.info(apiResponseConfig.getMethodEndMessage() + " saveUser " + user.getEmail());
			return apiResponse;
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " saveUser " + user.getEmail(), e);
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage());
			apiResponse.setData(null);
			return apiResponse;
		}

	}

	/**
	 * Updates the information of an existing user.
	 *
	 * @param header            The authorization header containing the JWT token.
	 * @param profilePhoto      The new profile photo of the user.
	 * @param officialDocuments List of new official documents.
	 * @param updatedUser       The updated user information.
	 * @return An ApiResponse indicating the status of the user update process.
	 */
	@Transactional
	public ApiResponse updateUser(String header, MultipartFile profilePhoto, List<MultipartFile> officialDocuments,
			User updatedUser) {

		String token = header.substring(7);
		jwtService.validateToken1(token);
		int userId = jwtService.extractUserId(token);
		int roleId = jwtService.extractRole(token);
		ApiResponse apiResponse = new ApiResponse();
		try {
			logger.info(apiResponseConfig.getMethodStartMessage() + "updateUser " + userId);

			if (permissions.getAuthorization()) {
				if (!permissions.hasAccess(roleId, apiResponseConfig.getRegistrationModule(),
						apiResponseConfig.getUpdateOperation())) {
					throw new AccessDeniedException(apiResponseConfig.getAccessDeniedMessage());
				}
			}
			User existingUser = registrationRepository.getById(userId);
			if (existingUser != null) {

				if (profilePhoto != null) {
					String newProfilePhotoUrl = awss3Service.uploadFile(profilePhoto);
					existingUser.setProfilePhoto(newProfilePhotoUrl);
				}
				if (officialDocuments != null && !officialDocuments.isEmpty()) {
					List<String> newDocumentUrls = new ArrayList<>();
					for (MultipartFile document : officialDocuments) {
						String documentUrl = awss3Service.uploadFile(document);
						newDocumentUrls.add(documentUrl);
					}
					existingUser.setOfficialDocuments(newDocumentUrls);
				}
				existingUser.updateFields(updatedUser);
				User user = registrationRepository.save(existingUser);

				//notificationUtil.sendPushNotification(userId, "updateUser");

				apiResponse.setStatus(true);
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
				apiResponse.setData(user);
				logger.info(apiResponseConfig.getMethodEndMessage() + " updateUser " + userId);
			} else {
				apiResponse.setStatus(false);
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getNotFoundMessage());
				apiResponse.setData(null);
			}
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " updateUser " + userId, e);
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(e.getMessage());
			apiResponse.setData(null);
		}
		return apiResponse;
	}

	/**
	 * Deactivates a user account.
	 *
	 * @param header The authorization header containing the JWT token.
	 * @return An ApiResponse indicating the status of the user deactivation
	 *         process.
	 */
	public ApiResponse makeUserInactive(String header) {
		try {
			String token = header.substring(7);
			jwtService.validateToken1(token);
			int userId = jwtService.extractUserId(token);
			int roleId = jwtService.extractRole(token);
			if (permissions.getAuthorization()) {
				if (!permissions.hasAccess(roleId, apiResponseConfig.getRegistrationModule(),
						apiResponseConfig.getUpdateOperation())) {
					throw new AccessDeniedException(apiResponseConfig.getAccessDeniedMessage());
				}
			}
			User user = registrationRepository.getById(userId);

			if (user == null) {
				ApiResponse apiResponse = new ApiResponse();
				apiResponse.setStatus(false);
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getNotFoundMessage() + userId);
				apiResponse.setData(null);
				return apiResponse;
			}

			user.setActive(false);
			registrationRepository.save(user);

		//	notificationUtil.sendPushNotification(userId, "deleteUser");

			ApiResponse apiResponse = new ApiResponse();
			apiResponse.setStatus(true);
			apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
			apiResponse.setMessage("User deactivated");
			apiResponse.setData(null);

			return apiResponse;
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse();
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage("An error occurred while deactivating the user." + e.getMessage());
			apiResponse.setData(null);

			return apiResponse;
		}
	}

	/**
	 * Updates the subscription information for a user.
	 *
	 * @param header         The authorization header containing the JWT token.
	 * @param subscriptionId The ID of the subscription to be assigned to the user.
	 * @return An ApiResponse indicating the status of the subscription update
	 *         process.
	 */
	public ApiResponse updateSubscriptionForUser(String header, Integer subscriptionId) {
		String token = header.substring(7);
		jwtService.validateToken1(token);
		int userId = jwtService.extractUserId(token);
		int roleId = jwtService.extractRole(token);
		ApiResponse apiResponse = new ApiResponse();
		try {
			logger.info(apiResponseConfig.getMethodStartMessage() + "updateSubscriptionForUser " + userId);

			if (permissions.getAuthorization()) {
				if (!permissions.hasAccess(roleId, apiResponseConfig.getRegistrationModule(),
						apiResponseConfig.getUpdateOperation())) {
					throw new AccessDeniedException(apiResponseConfig.getAccessDeniedMessage());
				}
			}

			User userWithExistingSubscription = registrationRepository.findBySubscriptionNotNullAndId(userId);
			if (userWithExistingSubscription != null) {
				apiResponse.setStatus(apiResponseConfig.isErrorResponseStatus());
				apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
				apiResponse.setMessage("User already has an active subscription.");
				apiResponse.setData(null);
				return apiResponse;
			}

			Optional<User> optionalUser = registrationRepository.findById(userId);
			if (optionalUser.isEmpty()) {
				apiResponse.setStatus(apiResponseConfig.isNotFoundStatus());
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getNotFoundMessage() + userId);
				apiResponse.setData(null);
			} else {
				User user = optionalUser.get();
				Subscriptions subscription = emailLoginRepository.fetchSubscriptionDetails(subscriptionId);
				if (subscription != null) {
					user.setSubscription(subscription);
					user.setSubscriptionStartDate(new Timestamp(new Date().getTime()));

					int daysRemaining = 0;
					if ("weekly".equalsIgnoreCase(subscription.getDurations())) {
						daysRemaining = 7;
					} else if ("monthly".equalsIgnoreCase(subscription.getDurations())) {
						daysRemaining = 30;
					} else if ("yearly".equalsIgnoreCase(subscription.getDurations())) {
						daysRemaining = 365;
					}

					user.setSubscriptionDaysRemaining(daysRemaining);
					User user2 = registrationRepository.save(user);

					// notificationUtil.sendPushNotification(userId, "saveSubscription");

					apiResponse.setStatus(apiResponseConfig.isSuccessResponseStatus());
					apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
					apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
					apiResponse.setData(user2);
					logger.info(apiResponseConfig.getMethodEndMessage() + " updateSubscriptionForUser " + userId);

				} else {
					apiResponse.setStatus(apiResponseConfig.isNotFoundStatus());
					apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
					apiResponse.setMessage(
							apiResponseConfig.getNotFoundMessage() + " with subscriptionId " + subscriptionId);
					apiResponse.setData(null);
				}
			}
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " updateSubscriptionForUser", e);
			apiResponse.setStatus(apiResponseConfig.isErrorResponseStatus());
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage() + e.getMessage());
			apiResponse.setData(null);
		}

		return apiResponse;
	}

	/**
	 * Retrieves user rating and review information based on the provided user ID in
	 * the JWT token.
	 *
	 * @param header JWT token in the request header.
	 * @return ApiResponse containing user rating and review information.
	 */
	public ApiResponse getUserRatingReviewByUserId(String header) {
		ApiResponse response = new ApiResponse();
		try {
			String token = header.substring(7);
			jwtService.validateToken1(token);
			int userId = jwtService.extractUserId(token);
			logger.info(apiResponseConfig.getMethodStartMessage() + "getUserRatingReviewByUserId " + userId);

			Object[] userInfo = ratingRepository.getUserInfoByUserId(userId);

			if (userInfo == null) {
				response.setStatus(false);
				response.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				response.setMessage(apiResponseConfig.getNotFoundMessage());
			} else {

				UserRatingReview userRatingReview = new UserRatingReview();
				userRatingReview.setUserId((Integer) userInfo[0]);
				userRatingReview.setUserName((String) userInfo[1]);
				userRatingReview.setUserMobile((String) userInfo[2]);
				userRatingReview.setUserEmail((String) userInfo[3]);
				userRatingReview.setUserProfilePhoto((String) userInfo[4]);
				userRatingReview.setBusinessName((String) userInfo[5]);
				userRatingReview.setOwnerName((String) userInfo[6]);
				userRatingReview.setGst((String) userInfo[7]);
				userRatingReview.setAccountHolderName((String) userInfo[8]);
				userRatingReview.setAccountName((String) userInfo[9]);
				userRatingReview.setIfscCode((String) userInfo[10]);
				userRatingReview.setBankUpi((String) userInfo[11]);
				userRatingReview.setRoleId((Integer) userInfo[12]);
				userRatingReview.setLanguageId((Integer) userInfo[13]);
				userRatingReview.setCreatedDate((Timestamp) userInfo[14]);
				userRatingReview.setUpdatedDate((Timestamp) userInfo[15]);
				userRatingReview.setCountryId((Integer) userInfo[16]);
				userRatingReview.setBusinessAddress((String) userInfo[17]);
				userRatingReview.setExtraField((String) userInfo[18]);
				String[] officialDocumentsObj = (String[]) userInfo[19];
				userRatingReview.setOfficialDocuments(officialDocumentsObj);
				if (officialDocumentsObj == null) {
					officialDocumentsObj = new String[0];
				}
				userRatingReview.setActive((Boolean) userInfo[20]);
				Integer SubscriptionId = ((Integer) userInfo[21]);
				Subscriptions subscriptions = emailLoginRepository.fetchSubscriptionDetails(SubscriptionId);
				userRatingReview.setSubscription(subscriptions);
				userRatingReview.setSubscriptionStartDate((Timestamp) userInfo[22]);
				userRatingReview.setSubscriptionDaysRemaining((Integer) userInfo[23]);
				response.setStatus(true);
				response.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				response.setMessage(apiResponseConfig.getSuccessResponseMessage());
				response.setData(userRatingReview);
				logger.info(apiResponseConfig.getMethodEndMessage() + " getUserRatingReviewByUserId " + userId);

			}
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " getUserRatingReviewByUserId", e);

			response.setStatus(false);
			response.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			response.setMessage(apiResponseConfig.getErrorResponseMessage() + e.getMessage());
		}
		return response;
	}

//	public ApiResponse getAllUsersWithAverageRatings() {
//		ApiResponse response = new ApiResponse();
//
//		try {
//			List<Object[]> userAndAverageRatings = ratingRepository.getAllUsersWithAverageRatings();
//
//			if (userAndAverageRatings.isEmpty()) {
//				response.setStatus(false);
//				response.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
//				response.setMessage(apiResponseConfig.getNotFoundMessage());
//			} else {
//				List<UserAverageRatingDTO> usersWithAverageRatings = new ArrayList<>();
//
//				for (Object[] row : userAndAverageRatings) {
//					UserAverageRatingDTO userWithAverageRating = new UserAverageRatingDTO();
//					userWithAverageRating.setUserId((Integer) row[0]);
//					userWithAverageRating.setUserName((String) row[1]);
//					userWithAverageRating.setUserMobile((String) row[2]);
//					userWithAverageRating.setUserEmail((String) row[3]);
//					userWithAverageRating.setUserProfilePhoto((String) row[4]);
//					userWithAverageRating.setBusinessName((String) row[6]);
//					userWithAverageRating.setOwnerName((String) row[7]);
//					userWithAverageRating.setGst((String) row[8]);
//					userWithAverageRating.setAccountHolderName((String) row[9]);
//					userWithAverageRating.setAccountName((String) row[10]);
//					userWithAverageRating.setIfscCode((String) row[11]);
//					userWithAverageRating.setBankUpi((String) row[12]);
//					userWithAverageRating.setRoleId((Integer) row[13]);
//					userWithAverageRating.setLanguageId((Integer) row[14]);
//					userWithAverageRating.setCreatedDate((Timestamp) row[15]);
//					userWithAverageRating.setUpdatedDate((Timestamp) row[16]);
//					userWithAverageRating.setCountryId((Integer) row[17]);
//					userWithAverageRating.setBusinessAddress((String) row[18]);
//					userWithAverageRating.setExtraField((String) row[19]);
//
//					userWithAverageRating.setOfficialDocuments((String[]) row[20]);
//					if (userWithAverageRating.getOfficialDocuments() == null) {
//						userWithAverageRating.setOfficialDocuments(new String[0]);
//					}
//
//					userWithAverageRating.setActive((Boolean) row[21]);
//					Integer SubscriptionId = ((Integer) row[22]);
//					Subscriptions subscriptions = emailLoginRepository.fetchSubscriptionDetails(SubscriptionId);
//					userWithAverageRating.setSubscription(subscriptions);
//					userWithAverageRating.setSubscriptionStartDate((Timestamp) row[23]);
//					userWithAverageRating.setSubscriptionDaysRemaining((Integer) row[24]);
//					userWithAverageRating.setReferralCode((String) row[25]);
//					userWithAverageRating.setAverageRating((BigDecimal) row[26]);
//
//					usersWithAverageRatings.add(userWithAverageRating);
//				}
//
//				response.setStatus(true);
//				response.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
//				response.setMessage(apiResponseConfig.getSuccessResponseMessage());
//				response.setData(usersWithAverageRatings);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			response.setStatus(false);
//			response.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
//			response.setMessage(apiResponseConfig.getErrorResponseMessage() + e.getMessage());
//		}
//
//		return response;
//	}
	/**
	 * Retrieves all users with their average ratings based on pagination
	 * parameters.
	 *
	 * @param page      Page number for pagination.
	 * @param size      Number of items per page.
	 * @param sortBy    Field to sort the results by.
	 * @param sortOrder Sort order (asc or desc).
	 * @return ApiResponse containing a list of users with average ratings and
	 *         pagination details.
	 */
	public ApiResponse getAllUsersWithAverageRatings(int page, int size, String sortBy, String sortOrder) {
		ApiResponse response = new ApiResponse();

		try {
			Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy) : Sort.by(sortBy).descending();
			Pageable pageable = PageRequest.of(page - 1, size, sort);
			Page<Object[]> usersPage = registrationRepository.getAllUsersWithAverageRatings(pageable);

			if (usersPage.isEmpty()) {
				response.setStatus(false);
				response.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				response.setMessage(apiResponseConfig.getNotFoundMessage());
			} else {
				List<UserAverageRatingDTO> usersWithAverageRatings = new ArrayList<>();

				for (Object[] row : usersPage.getContent()) {
					UserAverageRatingDTO userWithAverageRating = mapToObject(row);
					usersWithAverageRatings.add(userWithAverageRating);
				}

				response.setStatus(true);
				response.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				response.setMessage(apiResponseConfig.getSuccessResponseMessage());
				response.setData(new PaginationResponse<>(usersWithAverageRatings, usersPage.getTotalElements(),
						usersPage.getTotalPages(), usersPage.getNumber() + 1, usersPage.getSize()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			response.setMessage(apiResponseConfig.getErrorResponseMessage() + e.getMessage());
		}

		return response;
	}

	private UserAverageRatingDTO mapToObject(Object[] row) {
		UserAverageRatingDTO userWithAverageRating = new UserAverageRatingDTO();
		userWithAverageRating.setUserId((Integer) row[0]);
		userWithAverageRating.setUserName((String) row[1]);
		userWithAverageRating.setUserMobile((String) row[2]);
		userWithAverageRating.setUserEmail((String) row[3]);
		userWithAverageRating.setUserProfilePhoto((String) row[4]);
		userWithAverageRating.setBusinessName((String) row[6]);
		userWithAverageRating.setOwnerName((String) row[7]);
		userWithAverageRating.setGst((String) row[8]);
		userWithAverageRating.setAccountHolderName((String) row[9]);
		userWithAverageRating.setAccountName((String) row[10]);
		userWithAverageRating.setIfscCode((String) row[11]);
		userWithAverageRating.setBankUpi((String) row[12]);
		userWithAverageRating.setRoleId((Integer) row[13]);
		userWithAverageRating.setLanguageId((Integer) row[14]);
		userWithAverageRating.setCreatedDate((Timestamp) row[15]);
		userWithAverageRating.setUpdatedDate((Timestamp) row[16]);
		userWithAverageRating.setCountryId((Integer) row[17]);
		userWithAverageRating.setBusinessAddress((String) row[18]);
		userWithAverageRating.setExtraField((String) row[19]);

		userWithAverageRating.setOfficialDocuments((String[]) row[20]);
		if (userWithAverageRating.getOfficialDocuments() == null) {
			userWithAverageRating.setOfficialDocuments(new String[0]);
		}

		userWithAverageRating.setActive((Boolean) row[21]);
		Integer SubscriptionId = ((Integer) row[22]);
		Subscriptions subscriptions = emailLoginRepository.fetchSubscriptionDetails(SubscriptionId);
		userWithAverageRating.setSubscription(subscriptions);
		userWithAverageRating.setSubscriptionStartDate((Timestamp) row[23]);
		userWithAverageRating.setSubscriptionDaysRemaining((Integer) row[24]);
		userWithAverageRating.setReferralCode((String) row[25]);
		userWithAverageRating.setAverageRating((BigDecimal) row[26]);

		return userWithAverageRating;
	}

	public ApiResponse getUserRatingReviewByUserId(String header, Integer userId) {
		ApiResponse response = new ApiResponse();
		try {
			logger.info(apiResponseConfig.getMethodStartMessage() + "getAllUsersWithAverageRatings");
			String token = header.substring(7);
			jwtService.validateToken1(token);
			Object[] userInfo = ratingRepository.getUserInfoByUserId(userId);

			if (userInfo == null) {
				response.setStatus(false);
				response.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				response.setMessage(apiResponseConfig.getNotFoundMessage() + " " + userId);
				logger.info(apiResponseConfig.getMethodEndMessage() + " getAllUsersWithAverageRatings");

			} else {
				UserRatingReview userRatingReview = new UserRatingReview();
				userRatingReview.setUserId((Integer) userInfo[0]);
				userRatingReview.setUserName((String) userInfo[1]);
				userRatingReview.setUserMobile((String) userInfo[2]);
				userRatingReview.setUserEmail((String) userInfo[3]);
				userRatingReview.setUserProfilePhoto((String) userInfo[4]);
				userRatingReview.setBusinessName((String) userInfo[5]);
				userRatingReview.setOwnerName((String) userInfo[6]);
				userRatingReview.setGst((String) userInfo[7]);
				userRatingReview.setAccountHolderName((String) userInfo[8]);
				userRatingReview.setAccountName((String) userInfo[9]);
				userRatingReview.setIfscCode((String) userInfo[10]);
				userRatingReview.setBankUpi((String) userInfo[11]);
				userRatingReview.setRoleId((Integer) userInfo[12]);
				userRatingReview.setLanguageId((Integer) userInfo[13]);
				userRatingReview.setCreatedDate((Timestamp) userInfo[14]);
				userRatingReview.setUpdatedDate((Timestamp) userInfo[15]);
				userRatingReview.setCountryId((Integer) userInfo[16]);
				userRatingReview.setBusinessAddress((String) userInfo[17]);
				userRatingReview.setExtraField((String) userInfo[18]);
				String[] officialDocumentsObj = (String[]) userInfo[19];
				userRatingReview.setOfficialDocuments(officialDocumentsObj);
				if (officialDocumentsObj == null) {
					officialDocumentsObj = new String[0];
				}
				userRatingReview.setActive((Boolean) userInfo[20]);
				Integer SubscriptionId = ((Integer) userInfo[21]);
				Subscriptions subscriptions = emailLoginRepository.fetchSubscriptionDetails(SubscriptionId);
				userRatingReview.setSubscription(subscriptions);
				userRatingReview.setSubscriptionStartDate((Timestamp) userInfo[22]);
				userRatingReview.setSubscriptionDaysRemaining((Integer) userInfo[23]);
				userRatingReview.setReferralCode((String) userInfo[24]);
				response.setStatus(true);
				response.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				response.setMessage(apiResponseConfig.getSuccessResponseMessage());
				response.setData(userRatingReview);
				logger.info(apiResponseConfig.getMethodEndMessage() + " getAllUsersWithAverageRatings");

			}
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " getAllUsersWithAverageRatings", e);

			response.setStatus(false);
			response.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			response.setMessage(apiResponseConfig.getErrorResponseMessage() + e.getMessage());
		}
		return response;
	}

}
