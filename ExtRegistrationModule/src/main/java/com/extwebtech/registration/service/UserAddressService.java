package com.extwebtech.registration.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.bean.UserAddress;
import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.exception.AddressNotFoundException;
import com.extwebtech.registration.repository.UserAddressRepository;
import com.extwebtech.registration.util.AccessPermissions;
import com.extwebtech.registration.util.JWTService;
import com.extwebtech.registration.util.NotificationUtil;

/**
 * Service class for managing user addresses.
 */
@Service
public class UserAddressService {
	private static final Logger logger = Logger.getLogger(UserAddressService.class);
	@Autowired
	private UserAddressRepository userAddressRepository;

	@Autowired
	JWTService jwtService;

	@Autowired
	AccessPermissions permissions;

	@Autowired
	ApiResponseConfig apiResponseConfig;

	@Autowired
	NotificationUtil notificationUtil;

//	@Autowired
//	private NotificationRepository notificationRepository;
	/**
	 * Creates a new user address, sets it as the default address, and sends a push
	 * notification.
	 *
	 * @param userAddress The user address to be created.
	 * @param header      The authorization header containing the JWT token.
	 * @return ApiResponse indicating the success or failure of the operation.
	 */
	@Transactional
	public ApiResponse createAddress(UserAddress userAddress, String header) {
		String methodName = "createAddress";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		try {
			String token = header.substring(7);
			jwtService.validateToken1(token);
			int userId = jwtService.extractUserId(token);
			int roleId = jwtService.extractRole(token);
			if (permissions.getAuthorization()) {
				if (permissions.hasAccess(roleId, apiResponseConfig.getAddressModule(),
						apiResponseConfig.getCreateOperation())) {
					throw new AccessDeniedException(apiResponseConfig.getAccessDeniedMessage());
				}
			}

			List<UserAddress> existingAddresses = userAddressRepository.findByUserId(userId);

			existingAddresses.forEach(addr -> addr.setDefaultAddress(false));

			userAddress.setUserId(userId);
			userAddress.setDefaultAddress(true);

			userAddressRepository.saveAll(existingAddresses);
			UserAddress address = userAddressRepository.save(userAddress);
		//	notificationUtil.sendPushNotification(userId, "saveAddress");
			apiResponse.setStatus(true);
			apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
			apiResponse.setData(address);
			logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName + " " + address);
			return apiResponse;
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			e.printStackTrace();
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage() + e.getMessage());
			apiResponse.setData(null);
			return apiResponse;
		}
	}

	/**
	 * Retrieves the default user address for the authenticated user.
	 *
	 * @param header The authorization header containing the JWT token.
	 * @return ApiResponse containing the default user address or an error message.
	 */
	public ApiResponse getAddress(String header) {
		String methodName = "getAddress";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		try {
			String token = header.substring(7);
			jwtService.validateToken1(token);
			int userId = jwtService.extractUserId(token);
			if (permissions.getAuthorization()) {
				int roleId = jwtService.extractRole(token);
				if (permissions.hasAccess(roleId, apiResponseConfig.getAddressModule(),
						apiResponseConfig.getReadOperation())) {
					throw new AccessDeniedException(apiResponseConfig.getAccessDeniedMessage());
				}
			}
			List<UserAddress> addresses = userAddressRepository.findByUserId(userId);
			UserAddress defaultAddress = addresses.stream().filter(UserAddress::isDefaultAddress).findFirst()
					.orElse(null);

			if (defaultAddress != null) {
				apiResponse.setStatus(true);
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
				apiResponse.setData(defaultAddress);
				logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName + " " + defaultAddress);
				return apiResponse;
			} else {
				throw new AddressNotFoundException(apiResponseConfig.getNotFoundMessage());
			}
		} catch (AddressNotFoundException e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
			apiResponse.setMessage(apiResponseConfig.getNotFoundMessage());
			apiResponse.setData(null);
			return apiResponse;

		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			e.printStackTrace();
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage());
			apiResponse.setData(null);
			return apiResponse;
		}
	}

	/**
	 * Deletes a user address by the specified address ID, sends a push
	 * notification, and handles errors.
	 *
	 * @param header    The authorization header containing the JWT token.
	 * @param addressId The ID of the user address to be deleted.
	 * @return ApiResponse indicating the success or failure of the operation.
	 */
	public ApiResponse deleteAddress(String header, Integer addressId) {
		String methodName = "deleteAddress";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		String token = header.substring(7);
		jwtService.validateToken1(token);
		int userId = jwtService.extractUserId(token);
		int roleId = jwtService.extractRole(token);
		try {
			if (permissions.getAuthorization()) {
				if (permissions.hasAccess(roleId, apiResponseConfig.getAddressModule(),
						apiResponseConfig.getDeleteOperation())) {
					throw new AccessDeniedException(apiResponseConfig.getAccessDeniedMessage());
				}
			}

			UserAddress address = userAddressRepository.findByUserIdAndAddressId(userId, addressId).orElse(null);
			if (address != null) {
			//	notificationUtil.sendPushNotification(userId, "deleteAddress");
				userAddressRepository.delete(address);

				apiResponse.setStatus(true);
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
				apiResponse.setData(null);
				logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
				return apiResponse;
			} else {
				throw new AddressNotFoundException(apiResponseConfig.getNotFoundMessage());
			}
		} catch (AddressNotFoundException e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
			apiResponse.setMessage(apiResponseConfig.getNotFoundMessage() + e.getMessage());
			apiResponse.setData(null);
			return apiResponse;
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage() + e.getMessage());
			apiResponse.setData(null);
			return apiResponse;
		}
	}

	/**
	 * Updates an existing user address by the specified address ID, sets it as the
	 * default address, and sends a push notification.
	 *
	 * @param header             The authorization header containing the JWT token.
	 * @param addressId          The ID of the user address to be updated.
	 * @param updatedUserAddress The updated user address information.
	 * @return ApiResponse indicating the success or failure of the operation.
	 */
	@Transactional
	public ApiResponse updateAddress(String header, Integer addressId, UserAddress updatedUserAddress) {
		String methodName = "getAllCountryCodes";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		String token = header.substring(7);
		jwtService.validateToken1(token);
		int userId = jwtService.extractUserId(token);
		int roleId = jwtService.extractRole(token);
		try {
			if (permissions.getAuthorization()) {
				if (permissions.hasAccess(roleId, apiResponseConfig.getAddressModule(),
						apiResponseConfig.getUpdateOperation())) {
					throw new AccessDeniedException(apiResponseConfig.getAccessDeniedMessage());
				}
			}
			Optional<UserAddress> existingAddress = userAddressRepository.findByUserIdAndAddressId(userId, addressId);
			if (existingAddress.isPresent()) {
				UserAddress userAddress = existingAddress.get();
				userAddress.updateFields(updatedUserAddress);
				userAddress.setDefaultAddress(true);
				List<UserAddress> userAddresses = userAddressRepository.findByUserId(userId);
				for (UserAddress address : userAddresses) {
					if (!address.getAddressId().equals(addressId)) {
						address.setDefaultAddress(false);
					}
				}
				//notificationUtil.sendPushNotification(userId, "updateAddress");

				List<UserAddress> address = userAddressRepository.saveAll(userAddresses);
				List<UserAddress> orderUserAddresses = userAddressRepository.findByUserIdOrderByIsDefaultAddressDesc(userId);
				apiResponse.setStatus(true);
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
				apiResponse.setData(orderUserAddresses);
				logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
				return apiResponse;
			} else {
				throw new AddressNotFoundException(apiResponseConfig.getNotFoundMessage());
			}
		} catch (AddressNotFoundException e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			e.printStackTrace();
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
			apiResponse.setMessage(apiResponseConfig.getNotFoundMessage() + e.getMessage());
			apiResponse.setData(null);
			return apiResponse;
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			e.printStackTrace();
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage() + e.getMessage());
			apiResponse.setData(null);
			return apiResponse;
		}
	}

	/**
	 * Retrieves all user addresses for the authenticated user.
	 *
	 * @param header The authorization header containing the JWT token.
	 * @return ApiResponse containing a list of user addresses or an error message.
	 */
	public ApiResponse findByUserId(String header) {
		String methodName = "findByUserId";
		logger.info(apiResponseConfig.getMethodStartMessage() + " " + methodName);
		ApiResponse apiResponse = new ApiResponse();
		try {
			String token = header.substring(7);
			jwtService.validateToken1(token);
			int userId1 = jwtService.extractUserId(token);
			int roleId = jwtService.extractRole(token);
			if (permissions.getAuthorization()) {
				if (permissions.hasAccess(roleId, apiResponseConfig.getAddressModule(),
						apiResponseConfig.getReadOperation())) {
					throw new AccessDeniedException(apiResponseConfig.getAccessDeniedMessage());
				}
			}

			List<UserAddress> orderUserAddresses = userAddressRepository.findByUserIdOrderByIsDefaultAddressDesc(userId1);

			if (orderUserAddresses.isEmpty()) {
				apiResponse.setStatus(false);
				apiResponse.setStatusCode(apiResponseConfig.getNotFoundStatusCode());
				apiResponse.setMessage(apiResponseConfig.getNotFoundMessage() + userId1);
				apiResponse.setData(null);
			} else {
				apiResponse.setStatus(true);
				apiResponse.setStatusCode(apiResponseConfig.getSuccessResponseStatusCode());
				apiResponse.setMessage(apiResponseConfig.getSuccessResponseMessage());
				apiResponse.setData(orderUserAddresses);
				logger.info(apiResponseConfig.getMethodEndMessage() + " " + methodName);
			}

			return apiResponse;
		} catch (Exception e) {
			logger.error(apiResponseConfig.getMethodErrorMessage() + " " + methodName, e);
			apiResponse.setStatus(false);
			apiResponse.setStatusCode(apiResponseConfig.getErrorResponseStatusCode());
			apiResponse.setMessage(apiResponseConfig.getErrorResponseMessage() + e.getMessage());
			apiResponse.setData(null);
			return apiResponse;
		}
	}
}
