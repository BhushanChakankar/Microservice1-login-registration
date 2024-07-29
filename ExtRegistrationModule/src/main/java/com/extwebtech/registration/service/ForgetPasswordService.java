
package com.extwebtech.registration.service;

import org.apache.log4j.Logger;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.UpdatePasswordRequest;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.repository.ForgetPasswordRepository;

/**
 * Service class for handling forget password-related operations.
 */
@Service
public class ForgetPasswordService {

	private static final Logger logger = Logger.getLogger(ForgetPasswordService.class);

	@Autowired
	ApiResponseConfig apiResponseConfig;

	@Autowired
	ForgetPasswordRepository forgetPasswordRepository;

	@Autowired
	private StringEncryptor jasyptStringEncryptor;

	/**
	 * Updates the password for the provided email.
	 *
	 * @param updatePasswordRequest The request object containing email and new
	 *                              password.
	 * @return An ApiResponse indicating the status of the password update.
	 */
	public ApiResponse updatePassword(UpdatePasswordRequest updatePasswordRequest) {
		String methodName = "updatePassword";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		try {
			String email = updatePasswordRequest.getEmail();
			String newPassword = updatePasswordRequest.getNewPassword();
			String encryptedPassword = jasyptStringEncryptor.encrypt(newPassword);
			int updatedRows = forgetPasswordRepository.updatePasswordByEmail(email, encryptedPassword);
			if (updatedRows > 0) {
				apiResponse.setStatus(apiResponseConfig.isSuccessResponseStatus());
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage("Password updated successfully");
				apiResponse.setData(null);
				logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
				return apiResponse;
			} else {
				apiResponse.setStatus(apiResponseConfig.isNotFoundStatus());
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getNotFoundMessage() + " " + email);
				apiResponse.setData(null);
				return apiResponse;
			}

		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			e.printStackTrace();
			apiResponse.setStatus(apiResponseConfig.isErrorResponseStatus());
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage("Failed to update password: " + e.getMessage());
			apiResponse.setData(null);
			return apiResponse;
		}
	}
}
