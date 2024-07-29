package com.extwebtech.registration.service;

import java.nio.file.AccessDeniedException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.UpdateMobileRequest;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.repository.MobileLoginRepository;
import com.extwebtech.registration.repository.MobileUpdateRepository;
import com.extwebtech.registration.util.AccessPermissions;
import com.extwebtech.registration.util.JWTService;

/**
 * Service class for handling mobile number update-related operations.
 */
@Service
public class MobileUpdateService {

	private static final Logger logger = LogManager.getLogger(MobileUpdateService.class);

	@Autowired
	private MobileUpdateRepository mobileUpdateRepository;

	@Autowired
	private JWTService jwtService;

	@Autowired
	ApiResponseConfig apiResponseConfig;

	@Autowired
	AccessPermissions permissions;

	@Autowired
	MobileLoginRepository mobileLoginRepository;

	/**
	 * Updates the mobile number for the authenticated user.
	 *
	 * @param header              The authorization header containing the JWT token.
	 * @param updateMobileRequest The request object containing the updated mobile
	 *                            number.
	 * @return An ApiResponse indicating the status of the mobile number update
	 *         process.
	 */
	public ApiResponse updateMobileNumber(String header, UpdateMobileRequest updateMobileRequest) {
		String methodName = "updateMobileNumber";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		try {
			String token = header.substring(7);
			jwtService.validateToken1(token);
			int userId = jwtService.extractUserId(token);
			int roleId = jwtService.extractRole(token);
			if (permissions.getAuthorization()) {
				if (!permissions.hasAccess(roleId, apiResponseConfig.getLoginModule(),
						apiResponseConfig.getUpdateOperation())) {
					throw new AccessDeniedException(apiResponseConfig.getAccessDeniedMessage());
				}
			}
			String mobileNumber = updateMobileRequest.getMobileNumber();
			if (mobileLoginRepository.checkIfUserExistsWithUserId(mobileNumber, userId)) {

				apiResponse.setStatus(apiResponseConfig.isNotFoundStatus());
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getRegistrationMessage());
				return apiResponse;
			}
			int updatedRows = mobileUpdateRepository.updateMobileNumberById(userId, mobileNumber);
			if (updatedRows > 0) {
				logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
				apiResponse.setStatus(apiResponseConfig.isSuccessResponseStatus());
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage("Mobile number updated successfully");
				apiResponse.setData(null);
				return apiResponse;
			} else {
				apiResponse.setStatus(apiResponseConfig.isNotFoundStatus());
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getUserNotFoundMessage() + mobileNumber);
				apiResponse.setData(null);
				return apiResponse;
			}

		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			apiResponse.setStatus(apiResponseConfig.isErrorResponseStatus());
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage("Failed to update mobile number: " + e.getMessage());
			apiResponse.setData(null);
			return apiResponse;
		}
	}

}
