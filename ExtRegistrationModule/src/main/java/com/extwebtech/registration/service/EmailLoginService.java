package com.extwebtech.registration.service;

import java.nio.file.AccessDeniedException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.DeviceDetails;
import com.extwebtech.registration.bean.LoginEmail;
import com.extwebtech.registration.bean.LoginResponse;
import com.extwebtech.registration.bean.OtpDetails;
import com.extwebtech.registration.bean.SendOtpEmail;
import com.extwebtech.registration.bean.Subscriptions;
import com.extwebtech.registration.bean.User;
import com.extwebtech.registration.bean.VerifyOtpEmail;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.repository.EmailLoginRepository;
import com.extwebtech.registration.repository.OtpDetailsRepository;
import com.extwebtech.registration.util.AccessPermissions;
import com.extwebtech.registration.util.JWTService;
import com.extwebtech.registration.util.NotificationUtil;
import com.extwebtech.registration.util.OtpService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@Service
public class EmailLoginService {

	private static final Logger logger = Logger.getLogger(UserAddressService.class);

	@Autowired
	private OtpService otpService;

	@Autowired
	private EmailLoginRepository loginRepository;

	@Autowired
	private JWTService jwtService;

	@Autowired
	ApiResponseConfig apiResponseConfig;

	@Autowired
	AccessPermissions permissions;

	@Autowired
	private StringEncryptor jasyptStringEncryptor;

	@Autowired
	OtpDetailsRepository otpDetailsRepository;

	@Autowired
	NotificationUtil notificationUtil;

	/**
	 * Generates and sends an OTP to the provided email address.
	 * 
	 * @param sendOtpRequest The object containing the email address to send OTP.
	 * @return ApiResponse indicating the status of the OTP sending operation.
	 */
	public ApiResponse sendOtp(@Valid SendOtpEmail sendOtpRequest) {
		logger.info("Entering sendOtp method.");
		String target = sendOtpRequest.getEmail();
		boolean userExists = loginRepository.checkIfUserExists(target);
		ApiResponse response = new ApiResponse();
		if (userExists) {
			String otp = otpService.generateOtp();

			try {
				otpService.sendOtpByEmail(target, otp);
				OtpDetails otpDetails = new OtpDetails();
				otpDetails.setEmail(target);
				otpDetails.setOtp(otp);
				otpDetailsRepository.save(otpDetails);

				otpService.storeGeneratedOtp(target, otp);
				response.setStatus(apiResponseConfig.isSuccessResponseStatus());
				response.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				response.setMessage(apiResponseConfig.getLoginOtpSentMessageTemplate());
				response.setData(otp);
				logger.info("Exiting sendOtp method.");
				return response;
			} catch (Exception ex) {
				logger.error("Error occurred in sendOtp method.", ex);
				ex.printStackTrace();
				response.setStatus(apiResponseConfig.isErrorResponseStatus());
				response.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
				response.setMessage(apiResponseConfig.getErrorOccurredMessage());
				response.setData(null);
				return response;
			}
		} else {
			response.setStatus(apiResponseConfig.isErrorResponseStatus());
			response.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			response.setMessage(apiResponseConfig.getRegistrationMessage());
			response.setData(null);
			logger.info("Exiting sendOtp method.");
			return response;
		}
	}

	/**
	 * Verifies the entered OTP against the stored OTP for a given email.
	 * 
	 * @param verifyOtpRequest The object containing email and entered OTP.
	 * @return ApiResponse indicating the status of the OTP verification.
	 */
	public ApiResponse verifyOtp(@Valid VerifyOtpEmail verifyOtpRequest) {
		logger.info("Entering verifyOtp method.");
		String target = verifyOtpRequest.getEmail();
		ApiResponse response = new ApiResponse();
		boolean userExists = loginRepository.checkIfUserExists(target);
		if (userExists) {
			String enteredOtp = verifyOtpRequest.getEnteredOtp();

			// boolean isOtpValid = otpService.validateOtp(target, enteredOtp);

			OtpDetails details = otpDetailsRepository.getOtdDeatils(target);
			String email = details.getEmail();
			String otp = details.getOtp();

			if (target.equalsIgnoreCase(email) && enteredOtp.equals(otp)) {
				Timestamp createdDateTimestamp = (Timestamp) details.getCreatedDate();
				LocalDateTime createdDate = createdDateTimestamp.toLocalDateTime();
				if (Duration.between(createdDate, LocalDateTime.now())
						.getSeconds() < (apiResponseConfig.getOtpExpireTime() * 60)) {
					response.setStatus(apiResponseConfig.isSuccessResponseStatus());
					response.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
					response.setMessage(apiResponseConfig.getValidatedOtp());
					response.setData(null);
					return response;
				} else {
					response.setStatus(apiResponseConfig.isErrorResponseStatus());
					response.setStatusCode(apiResponseConfig.getOtpExpireStatusCode());
					response.setMessage(apiResponseConfig.getOtpExpireMessage());
					response.setData(null);
					return response;
				}
			} else {
				response.setStatus(apiResponseConfig.isErrorResponseStatus());
				response.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
				response.setMessage(apiResponseConfig.getValidateOtpInvalidOtpMessage());
				response.setData(null);
				return response;
			}
		} else {
			response.setStatus(apiResponseConfig.isNotFoundStatus());
			response.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
			response.setMessage("Please provide valid email");
			response.setData(null);
			return response;
		}

	}

	private int insertOrUpdateUserDataEmail(String email, DeviceDetails deviceDetailsJson, String deviceToken,
			int userId, String deviceType) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			Map<String, Object> deviceDetailsMap = objectMapper.convertValue(deviceDetailsJson, Map.class);
			String deviceDetailsJsonString = objectMapper.writeValueAsString(deviceDetailsMap);
			return loginRepository.insertOrUpdateUserDataEmail(email, deviceDetailsJsonString, deviceToken, deviceType,
					userId);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Handles user login using email and password.
	 * 
	 * @param loginEmail The object containing email, password, and device details.
	 * @return LoginResponse containing user details, JWT token, and login status.
	 */
	public LoginResponse loginEmail(LoginEmail loginEmail) {
		logger.info("Starting loginEmail method.");
		LoginResponse response = new LoginResponse();
		try {
			boolean userExists = loginRepository.checkIfUserExists(loginEmail.getEmail());

			if (userExists) {
				String storedPassword = loginRepository.getPassword(loginEmail.getEmail());
				String decryptedPassword = null;

				try {
					decryptedPassword = jasyptStringEncryptor.decrypt(storedPassword);
				} catch (Exception e) {
					response.setStatus(apiResponseConfig.isErrorResponseStatus());
					response.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
					response.setMessage("Error during password decryption: " + e.getMessage());
					return response;
				}

				if (decryptedPassword == null || !decryptedPassword.equals(loginEmail.getPassword())) {
					response.setStatus(apiResponseConfig.isNotFoundStatus());
					response.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
					response.setMessage("Please provide the valid Password!.");
					response.setData(null);
					return response;
				}
				loginEmail.setPassword(storedPassword);
				Object[] user = loginRepository.findByEmail(loginEmail);
				if (user == null) {
					response.setStatus(apiResponseConfig.isNotFoundStatus());
					response.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
					response.setMessage("Please provide the valid Password!.");
					response.setData(null);
					return response;
				} else {
					int userId = (int) user[0];
					int roleId = (int) user[13];
					User userEntity = mapToUserEntity(user);
					Subscriptions subscription = userEntity.getSubscription();
					if (subscription != null) {
						LocalDateTime subscriptionStartDate = userEntity.getSubscriptionStartDate()
								.toLocalDateTime() != null ? userEntity.getSubscriptionStartDate().toLocalDateTime()
										: null;
						LocalDateTime now = LocalDateTime.now();
						Duration duration = Duration.between(subscriptionStartDate, now);
						String subscriptionType = subscription.getDurations();
						if (subscriptionType.equalsIgnoreCase("weekly") && duration.toDays() >= 7) {
							userEntity.setSubscription(null);
							response.setMessage("Your weekly subscription has expired. Please renew it.");
						} else if (subscriptionType.equalsIgnoreCase("monthly") && duration.toDays() >= 30) {
							userEntity.setSubscription(null);
							response.setMessage("Your monthly subscription has expired. Please renew it.");
						} else if (subscriptionType.equalsIgnoreCase("yearly") && duration.toDays() >= 365) {
							userEntity.setSubscription(null);
							response.setMessage("Your yearly subscription has expired. Please renew it.");
						} else {
							response.setMessage(apiResponseConfig.getSuccessResponseMessage());
						}
					} else {
						response.setMessage(apiResponseConfig.getSuccessResponseMessage());
					}
					int loginId = insertOrUpdateUserDataEmail(loginEmail.getEmail(), loginEmail.getDeviceDetails(),
							loginEmail.getDeviceToken(), userId, loginEmail.getDeviceType());
					String token = jwtService.generateToken(loginEmail.getEmail(), roleId, userId, loginId);
				//	notificationUtil.sendPushNotification(userId, "login");
					response.setStatus(apiResponseConfig.isSuccessResponseStatus());
					response.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
					response.setToken(token);
					response.setLoginId(loginId);
					response.setData(userEntity);
					logger.info("Exiting loginEmail method.");
					return response;
				}
			} else {
				response.setStatus(apiResponseConfig.isNotFoundStatus());
				response.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				response.setMessage(apiResponseConfig.getRegistrationMessage());
				response.setData(null);
				return response;
			}
		} catch (IllegalArgumentException e) {
			logger.error("Error occurred in login method.", e);
			response.setStatus(apiResponseConfig.isErrorResponseStatus());
			response.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			response.setMessage(e.getMessage());
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(apiResponseConfig.isErrorResponseStatus());
			response.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			response.setMessage(e.getMessage());
			return response;
		}
	}

	/**
	 * Maps the data obtained from the database to a User entity.
	 * 
	 * @param userData An array containing user data from the database.
	 * @return User entity with mapped data.
	 */
	private User mapToUserEntity(Object[] userData) {
		User user = new User();

		user.setId((Integer) userData[0]);
		user.setName((String) userData[1]);
		user.setMobile((String) userData[2]);
		user.setEmail((String) userData[3]);
		user.setProfilePhoto((String) userData[4]);
		user.setBusinessName((String) userData[6]);
		user.setOwnerName((String) userData[7]);
		user.setGst((String) userData[8]);
		user.setAccountHolderName((String) userData[9]);
		user.setAccountName((String) userData[10]);
		user.setIfscCode((String) userData[11]);
		user.setBankUpi((String) userData[12]);
		user.setRoleId((Integer) userData[13]);
		user.setLanguageId((Integer) userData[14]);
		user.setCreatedDate((Timestamp) userData[15]);
		user.setUpdatedDate((Timestamp) userData[16]);
		user.setCountryId((Integer) userData[17]);
		user.setBusinessAddress((String) userData[18]);
		user.setExtraField((String) userData[19]);

		String[] officialDocumentsObj = (String[]) userData[20];
		List<String> officialDocumentsList = new ArrayList<String>();
		if (officialDocumentsObj != null) {
			for (String object : officialDocumentsObj) {
				officialDocumentsList.add(object);
			}
		} else {
			officialDocumentsList = Collections.emptyList();
		}
		user.setOfficialDocuments(officialDocumentsList);
		user.setActive((Boolean) userData[21]);
		Integer subscriptionId = (Integer) userData[22];
		Subscriptions subscription = loginRepository.fetchSubscriptionDetails(subscriptionId);
		user.setSubscription(subscription);
		user.setSubscriptionStartDate((Timestamp) userData[23]);
		user.setSubscriptionDaysRemaining((Integer) userData[24]);
		user.setReferralCode((String) userData[25]);
		return user;
	}

	/**
	 * Updates the device token for a user.
	 * 
	 * @param header      The JWT token in the request header.
	 * @param deviceToken The new device token.
	 * @return ApiResponse indicating the status of the device token update
	 *         operation.
	 */
	public ApiResponse updateDeviceToken(String header, String deviceToken) {
		String methodName = "updateDeviceToken";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		try {
			String token = header.substring(7);
			jwtService.validateToken1(token);
			int roleId = jwtService.extractRole(token);
			if (permissions.getAuthorization()) {
				if (!permissions.hasAccess(roleId, apiResponseConfig.getLoginModule(),
						apiResponseConfig.getUpdateOperation())) {
					throw new AccessDeniedException(apiResponseConfig.getAccessDeniedMessage());
				}
			}
			String details = jwtService.extractMobileNumber(token);
			int loginId = jwtService.extractLoginId(token);
			int updatedRows = loginRepository.updateDeviceToken(loginId, details, deviceToken);

			if (updatedRows > 0) {
				apiResponse.setStatus(apiResponseConfig.isSuccessResponseStatus());
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
				apiResponse.setData(null);
				logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
			} else {
				apiResponse.setStatus(apiResponseConfig.isNotFoundStatus());
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getUserNotFoundMessage());
				apiResponse.setData(null);
			}
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			apiResponse.setStatus(apiResponseConfig.isErrorResponseStatus());
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage("Failed to update device token: " + e.getMessage());
			apiResponse.setData(null);
		}

		return apiResponse;
	}

	/**
	 * Handles user logout by deleting login details.
	 * 
	 * @param header The JWT token in the request header.
	 * @return ApiResponse indicating the status of the logout operation.
	 */
	public ApiResponse logout(String header) {
		String methodName = "logout";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		try {
			String token = header.substring(7);
			jwtService.validateToken1(token);
			int roleId = jwtService.extractRole(token);
			if (permissions.getAuthorization()) {
				if (!permissions.hasAccess(roleId, apiResponseConfig.getLoginModule(),
						apiResponseConfig.getUpdateOperation())) {
					throw new AccessDeniedException(apiResponseConfig.getAccessDeniedMessage());
				}
			}
			String details = jwtService.extractMobileNumber(token);
			int deletedRows = loginRepository.deleteLoginDetails(details);

			if (deletedRows > 0) {
				apiResponse.setStatus(apiResponseConfig.isSuccessResponseStatus());
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage("Logged out successfully.");
				apiResponse.setData(null);
				logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
			} else {
				apiResponse.setStatus(apiResponseConfig.isNotFoundStatus());
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getNotFoundMessage());
				apiResponse.setData(null);
			}
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			apiResponse.setStatus(apiResponseConfig.isErrorResponseStatus());
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage("Failed to logout: " + e.getMessage());
			apiResponse.setData(null);
		}

		return apiResponse;
	}

}
