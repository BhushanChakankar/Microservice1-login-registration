package com.extwebtech.registration.service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.DeviceDetails;
import com.extwebtech.registration.bean.LoginRequest;
import com.extwebtech.registration.bean.LoginResponse;
import com.extwebtech.registration.bean.OtpDetails;
import com.extwebtech.registration.bean.SendOtpMobile;
import com.extwebtech.registration.bean.Subscriptions;
import com.extwebtech.registration.bean.User;
import com.extwebtech.registration.bean.VerifyOtpMobile;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.repository.MobileLoginRepository;
import com.extwebtech.registration.repository.OtpDetailsRepository;
import com.extwebtech.registration.util.JWTService;
import com.extwebtech.registration.util.NotificationUtil;
import com.extwebtech.registration.util.OtpService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

/**
 * Service class for handling mobile login-related operations.
 */
@Service
public class MobileLoginService {

	private static final Logger logger = Logger.getLogger(ForgetPasswordService.class);

	@Autowired
	private OtpService otpService;

	@Autowired
	private MobileLoginRepository loginRepository;

	@Autowired
	private JWTService jwtService;

	@Autowired
	ApiResponseConfig apiResponseConfig;

	@Autowired
	OtpDetailsRepository otpDetailsRepository;

	@Autowired
	NotificationUtil notificationUtil;

	/**
	 * Sends an OTP to the provided mobile number for login verification.
	 *
	 * @param sendOtpRequest The request object containing the mobile number.
	 * @return An ApiResponse indicating the status of the OTP sending process.
	 */
	public ApiResponse sendOtp(@Valid SendOtpMobile sendOtpRequest) {
		String methodName = "sendOtp";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		String target = sendOtpRequest.getMobileNumber();
		boolean userExists = loginRepository.checkIfUserExists(target);

		if (userExists) {
			String otp = otpService.generateOtp();
			try {
				otpService.sendOtpSms(apiResponseConfig.getSmsTOMobile(), otp);
				OtpDetails otpDetails = new OtpDetails();
				otpDetails.setMobileNumber(sendOtpRequest.getMobileNumber());
				otpDetails.setOtp(otp);
				otpDetailsRepository.save(otpDetails);
				otpService.storeGeneratedOtp(target, otp);
				ApiResponse response = new ApiResponse();
				response.setStatus(apiResponseConfig.isSuccessResponseStatus());
				response.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				response.setMessage(apiResponseConfig.getLoginOtpSentMessageTemplate());
				response.setData(otp);
				logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
				return response;
			} catch (Exception ex) {
				logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, ex);
				ex.printStackTrace();
				ApiResponse response = new ApiResponse();
				response.setStatus(apiResponseConfig.isErrorResponseStatus());
				response.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
				response.setMessage(apiResponseConfig.getErrorOccurredMessage());
				response.setData(null);
				return response;
			}
		} else {
			ApiResponse response = new ApiResponse();
			response.setStatus(apiResponseConfig.isErrorResponseStatus());
			response.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			response.setMessage(apiResponseConfig.getRegistrationMessage());
			response.setData(null);
			logger.info("Exiting sendOtp method.");
			return response;
		}
	}

	/**
	 * Verifies the OTP entered by the user for mobile login.
	 *
	 * @param verifyOtpRequest The request object containing the mobile number and
	 *                         entered OTP.
	 * @return An ApiResponse indicating the status of the OTP verification process.
	 */
	public ApiResponse verifyOtp(@Valid VerifyOtpMobile verifyOtpRequest) {
		logger.info("Entering verifyOtp method.");
		String target = verifyOtpRequest.getMobileNumber();
		String enteredOtp = verifyOtpRequest.getEnteredOtp();
		boolean userExists = loginRepository.checkIfUserExists(target);
		if (userExists) {
			// boolean isOtpValid = otpService.validateOtp(target, enteredOtp);

			OtpDetails details = otpDetailsRepository.getOtdDeatilsWithMobile(target);
			String mobile = details.getMobileNumber();
			String otp = details.getOtp();

			if (target.equals(mobile) && enteredOtp.equals(otp)) {

				Timestamp createdDateTimestamp = (Timestamp) details.getCreatedDate();
				LocalDateTime createdDate = createdDateTimestamp.toLocalDateTime();
				if (Duration.between(createdDate, LocalDateTime.now())
						.getSeconds() < (apiResponseConfig.getOtpExpireTime() * 60)) {
					ApiResponse response = new ApiResponse();
					response.setStatus(apiResponseConfig.isSuccessResponseStatus());
					response.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
					response.setMessage(apiResponseConfig.getValidatedOtp());
					response.setData(null);
					return response;
				} else {
					ApiResponse response = new ApiResponse();
					response.setStatus(apiResponseConfig.isErrorResponseStatus());
					response.setStatusCode(apiResponseConfig.getOtpExpireStatusCode());
					response.setMessage(apiResponseConfig.getOtpExpireMessage());
					response.setData(null);
					return response;
				}
			} else {
				ApiResponse response = new ApiResponse();
				response.setStatus(apiResponseConfig.isErrorResponseStatus());
				response.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
				response.setMessage(apiResponseConfig.getValidateOtpInvalidOtpMessage());
				response.setData(null);
				logger.error("Error occurred in verifyOtp method.");
				return response;
			}

		} else {
			ApiResponse response = new ApiResponse();
			response.setStatus(apiResponseConfig.isNotFoundStatus());
			response.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
			response.setMessage("Please provide valid mobile number");
			response.setData(null);
			return response;
		}

	}

	/**
	 * Stores user data as JSON during the mobile login process.
	 *
	 * @param loginRequest The login request object containing user details.
	 * @param userId       The user ID.
	 * @return The number of rows affected in the database.
	 */
	private int storeUserDataAsJson(LoginRequest loginRequest, int userId) {
		ObjectMapper objectMapper = new ObjectMapper();
		DeviceDetails deviceDetails = loginRequest.getDeviceDetails();
		try {
			String deviceDetailsJson = objectMapper.writeValueAsString(deviceDetails);
			return loginRepository.insertOrUpdateUserData(loginRequest.getMobileNumber(), deviceDetailsJson,
					loginRequest.getDeviceToken(), loginRequest.getDeviceType(), userId);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Handles the mobile login process.
	 *
	 * @param loginRequest The login request object containing user details.
	 * @return The login response object containing the authentication token and
	 *         user data.
	 */
	public LoginResponse login(LoginRequest loginRequest) {
		LoginResponse response = new LoginResponse();
		logger.info("Entering login method.");
		try {
			boolean userExists = loginRepository.checkIfUserExists(loginRequest.getMobileNumber());
			if (!userExists) {

				response.setStatus(apiResponseConfig.isNotFoundStatus());
				response.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				response.setMessage(apiResponseConfig.getRegistrationMessage());
				response.setData(null);
				return response;
			} else {
				Object[] user = findByMobileNumberWithId(loginRequest);
				int userId = (int) user[0];
				int roleId = (int) user[13];

				User userEntity = mapToUserEntity(user);
				Subscriptions subscription = userEntity.getSubscription();
				if (subscription != null) {
					LocalDateTime subscriptionStartDate = userEntity.getSubscriptionStartDate()
							.toLocalDateTime() != null ? userEntity.getSubscriptionStartDate().toLocalDateTime() : null;
					LocalDateTime now = LocalDateTime.now();
					Duration duration = Duration.between(subscriptionStartDate, now);
					String subscriptionType = subscription.getDurations();
					if (subscriptionType.equalsIgnoreCase("weekly") && duration.toDays() >= 7) {
						userEntity.setSubscription(null);
						response.setMessage(apiResponseConfig.getSubscriptionExpiredWeekly());
					} else if (subscriptionType.equalsIgnoreCase("monthly") && duration.toDays() >= 30) {
						userEntity.setSubscription(null);
						response.setMessage(apiResponseConfig.getSubscriptionExpiredMonthly());
					} else if (subscriptionType.equalsIgnoreCase("yearly") && duration.toDays() >= 365) {
						userEntity.setSubscription(null);
						response.setMessage(apiResponseConfig.getSubscriptionExpiredYearly());
					} else {
						response.setMessage(apiResponseConfig.getSuccessResponseMessage());
					}
				} else {
					response.setMessage(apiResponseConfig.getSuccessResponseMessage());
				}
				int loginId = storeUserDataAsJson(loginRequest, userId);
				String token = jwtService.generateToken(loginRequest.getMobileNumber(), roleId, userId, loginId);
				// notificationUtil.sendPushNotification(userId, "login");
				response.setStatus(apiResponseConfig.isSuccessResponseStatus());
				response.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				response.setToken(token);
				response.setLoginId(loginId);
				response.setData(userEntity);
				logger.info("Exiting login method.");
				return response;
			}
		} catch (IllegalArgumentException e) {
			logger.error("Error occurred in login method.", e);
			response.setStatus(apiResponseConfig.isErrorResponseStatus());
			response.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			response.setMessage(e.getMessage());
			return response;
		}
	}

	/**
	 * Retrieves user data from the database based on the provided mobile number.
	 *
	 * @param loginRequest The login request object containing the mobile number.
	 * @return An array containing user data.
	 */
	public Object[] findByMobileNumberWithId(LoginRequest loginRequest) {
		return loginRepository.findByMobileNumberWithId(loginRequest);
	}

	/**
	 * Maps the array of user data to the User entity.
	 *
	 * @param userData The array containing user data.
	 * @return The User entity object.
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

}
