package com.extwebtech.registration.service;

import java.nio.file.AccessDeniedException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.UpdateEmailRequest;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.repository.EmailLoginRepository;
import com.extwebtech.registration.repository.EmailUpdateRepository;
import com.extwebtech.registration.util.AccessPermissions;
import com.extwebtech.registration.util.JWTService;

@Service
public class EmailUpdateService {

	private static final Logger logger = Logger.getLogger(EmailUpdateService.class);

	@Autowired
	EmailUpdateRepository emailUpdateRepository;

	@Autowired
	private JWTService jwtService;

	@Autowired
	AccessPermissions permissions;

	@Autowired
	ApiResponseConfig apiResponseConfig;

	@Autowired
	EmailLoginRepository emailLoginRepository;

	/**
	 * Updates the email address associated with the user's account.
	 * 
	 * @param header             The JWT token in the request header.
	 * @param updateEmailRequest The object containing the updated email address.
	 * @return ApiResponse indicating the status of the email update operation.
	 */
	public ApiResponse updateEmail(String header, UpdateEmailRequest updateEmailRequest) {
		String methodName = "updateEmail";
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
			String email = updateEmailRequest.getEmail();
			if (emailLoginRepository.checkIfUserExistsWIthuserId(email, userId)) {
				apiResponse.setStatus(apiResponseConfig.isNotFoundStatus());
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage("This email is already register with another account");
				return apiResponse;
			}
			int updatedRows = emailUpdateRepository.updateEmailById(userId, email);

			if (updatedRows > 0) {
				logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
				apiResponse.setStatus(apiResponseConfig.isSuccessResponseStatus());
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
				return apiResponse;
			} else {
				apiResponse.setStatus(apiResponseConfig.isNotFoundStatus());
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getNotFoundMessage());
				return apiResponse;
			}

		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			e.printStackTrace();
			apiResponse.setStatus(apiResponseConfig.isErrorResponseStatus());
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage("Failed to update email: " + e.getMessage());
			apiResponse.setData(null);
			return apiResponse;
		}
	}
}
