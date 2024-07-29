package com.extwebtech.registration.service;

/**
 * Service class for managing CountryCode entities.
 */
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.CountryCode;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.repository.CountryCodeRepository;

@Service
public class CountryCodeService {
	@Autowired
	private CountryCodeRepository countryCodeRepository;

	@Autowired
	ApiResponseConfig apiResponseConfig;

	private static final Logger logger = Logger.getLogger(CountryCodeService.class);

	/**
	 * Retrieves all country codes.
	 *
	 * @return ApiResponse containing the list of CountryCode entities
	 */
	public ApiResponse getAllCountryCodes() {
		ApiResponse apiResponse = new ApiResponse();
		try {
			String methodName = "getAllCountryCodes";
			logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);

			List<CountryCode> codes = countryCodeRepository.findAll();
			apiResponse.setStatus(true);
			apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
			apiResponse.setData(codes);
			logger.info(codes + " " + apiResponseConfig.getMethodEndMessage() + " " + methodName);

		} catch (Exception e) {

			String methodName = "getAllCountryCodes";
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);

			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage());
			apiResponse.setData(null);
		}
		return apiResponse;
	}

	/**
	 * Retrieves a specific country code by its ID.
	 *
	 * @param countryId The ID of the country code to retrieve
	 * @return ApiResponse containing the CountryCode entity or an error message
	 */
	public ApiResponse getCountryCodeById(Integer countryId) {
		ApiResponse apiResponse = new ApiResponse();
		try {
			String methodName = "getCountryCodeById";
			logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);

			Optional<CountryCode> countryCode = countryCodeRepository.findById(countryId);
			if (countryCode.isPresent()) {
				apiResponse.setStatus(true);
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
				apiResponse.setData(countryCode.get());
				logger.info(countryCode.get() + " " + apiResponseConfig.getMethodEndMessage() + " " + methodName);

			} else {
				apiResponse.setStatus(false);
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getNotFoundMessage());
				apiResponse.setData(null);
			}
		} catch (Exception e) {
			String methodName = "getCountryCodeById";
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);

			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage());
			apiResponse.setData(null);
		}

		return apiResponse;
	}

	/**
	 * Saves a new country code.
	 *
	 * @param countryCode The CountryCode entity to save
	 * @return ApiResponse containing the saved CountryCode entity or an error
	 *         message
	 */
	public ApiResponse saveCountryCode(CountryCode countryCode) {
		ApiResponse apiResponse = new ApiResponse();
		String methodName = "saveCountryCode";
		try {

			logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
			CountryCode savedCountryCode = countryCodeRepository.save(countryCode);
			apiResponse.setStatus(true);
			apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
			apiResponse.setData(savedCountryCode);
			logger.info(savedCountryCode + " " + apiResponseConfig.getMethodEndMessage() + " " + methodName);

		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage());
			apiResponse.setData(null);
		}
		return apiResponse;
	}

	/**
	 * Deletes a country code by its ID.
	 *
	 * @param countryId The ID of the country code to delete
	 * @return ApiResponse indicating the success or failure of the deletion
	 */
	public ApiResponse deleteCountryCode(Integer countryId) {
		ApiResponse apiResponse = new ApiResponse();
		String methodName = "deleteCountryCode";
		try {
			logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
			countryCodeRepository.deleteById(countryId);
			apiResponse.setStatus(true);
			apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
			logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage());
			apiResponse.setData(null);
		}
		return apiResponse;
	}
}