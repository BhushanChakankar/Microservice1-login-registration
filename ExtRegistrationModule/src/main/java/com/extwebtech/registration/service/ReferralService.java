package com.extwebtech.registration.service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.Referral;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.exception.MissingAuthorizationHeaderException;
import com.extwebtech.registration.repository.ReferralRepository;
import com.extwebtech.registration.repository.RegistrationRepository;
import com.extwebtech.registration.util.JWTService;

/**
 * Service class for handling referral-related operations.
 */
@Service
public class ReferralService {

	private static final Logger logger = Logger.getLogger(ReferralService.class);

	@Autowired
	private ReferralRepository referralRepository;

	@Autowired
	JWTService jwtService;

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Autowired
	ApiResponseConfig apiResponseConfig;

	@Autowired
	RegistrationRepository registrationRepository;

	/**
	 * Checks if the given email is valid.
	 *
	 * @param email The email to be validated.
	 * @return True if the email is valid; false otherwise.
	 */
	private boolean isValidEmail(String email) {
		if (StringUtils.hasText(email)) {
			String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
			Pattern pattern = Pattern.compile(emailRegex);
			return pattern.matcher(email).matches();
		}
		return false;
	}

	/**
	 * Sends a referral email to the specified friend.
	 *
	 * @param header      The authorization header containing the JWT token.
	 * @param friendEmail The email of the friend to whom the referral will be sent.
	 * @return An ApiResponse indicating the status of the referral email sending
	 *         process.
	 */
	public ApiResponse sendReferral(String header, String friendEmail) {
		String methodName = "sendReferral";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		if (!isValidEmail(friendEmail)) {
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getInvalidEmail());
			apiResponse.setData(null);
			return apiResponse;
		}

		try {
			String token = header.substring(7);
			jwtService.validateToken1(token);
			int userId = jwtService.extractUserId(token);

			boolean hasReferral = referralRepository.existsByReferrerIdAndFriendEmail(userId, friendEmail);
			if (hasReferral) {
				apiResponse.setStatus(false);
				apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
				apiResponse.setMessage(apiResponseConfig.getReferralExists());
				apiResponse.setData(null);
				return apiResponse;
			}
			String userReferralCode = registrationRepository.findReferralCodeById(userId);

			Referral referral = new Referral();
			referral.setReferrerId(userId);
			referral.setFriendEmail(friendEmail);
			referral.setReferralCode(userReferralCode);
			referral.setReferralDate(new Date());

			referralRepository.save(referral);

			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setFrom(fromEmail);
			mailMessage.setTo(friendEmail);
			mailMessage.setSubject(apiResponseConfig.getEmailSubject());
			String emailText = MessageFormat.format(apiResponseConfig.getEmailText(), userReferralCode);
			mailMessage.setText(emailText);

			javaMailSender.send(mailMessage);
			apiResponse.setStatus(true);
			apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getReferralEmailSent());
			apiResponse.setData(null);
			logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
			return apiResponse;
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(e.getMessage());
			apiResponse.setData(null);
			return apiResponse;
		}

	}

	/**
	 * Retrieves all referrals for the authenticated user.
	 *
	 * @param header The authorization header containing the JWT token.
	 * @return An ApiResponse containing the list of referrals.
	 */
	public ApiResponse getAllReferrals(String header) {
		String methodName = "getAllReferrals";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		try {
			String token = header.substring(7);
			jwtService.validateToken1(token);
			int userId = jwtService.extractUserId(token);
			List<Referral> referrals = referralRepository.findByReferrerId(userId);
			apiResponse.setStatus(true);
			apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
			apiResponse.setMessage("Referral entries retrieved successfully.");
			apiResponse.setData(referrals);
			logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
			return apiResponse;
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(e.getMessage());
			apiResponse.setData(null);
			return apiResponse;
		}
	}

	/**
	 * Deletes a referral entry for the authenticated user.
	 *
	 * @param header     The authorization header containing the JWT token.
	 * @param referralId The ID of the referral entry to be deleted.
	 * @return An ApiResponse indicating the status of the referral entry deletion
	 *         process.
	 */
	public ApiResponse deleteReferral(String header, Long referralId) {
		String methodName = "deleteReferral";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		if (header == null || header.isEmpty()) {
			throw new MissingAuthorizationHeaderException(apiResponseConfig.getPleaseProvideToken());
		}
		String token = header.substring(7);
		jwtService.validateToken1(token);
		int userId = jwtService.extractUserId(token);
		Optional<Referral> optionalReferral = referralRepository.findById(referralId);
		if (optionalReferral.isEmpty() || optionalReferral.get().getReferrerId() != userId) {
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
			apiResponse.setMessage(apiResponseConfig.getNotFoundMessage());
			apiResponse.setData(null);
			return apiResponse;
		}
		referralRepository.deleteById(referralId);
		apiResponse.setStatus(true);
		apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
		apiResponse.setMessage("Referral entry deleted successfully.");
		apiResponse.setData(null);
		logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
		return apiResponse;
	}

}
