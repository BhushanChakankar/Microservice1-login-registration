package com.extwebtech.registration.exception;

import java.nio.file.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.configuration.ApiResponseConfig;

@RestControllerAdvice
public class ApplicationExceptionHandler {

	@Autowired
	private ApiResponseConfig apiResponseConfig;

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ApiResponse handleInvalidAgrument(MethodArgumentNotValidException e) {

		if (e.getBindingResult().getFieldErrors().size() > 0) {
			FieldError firstError = e.getBindingResult().getFieldErrors().get(0);
			ApiResponse apiResponse = new ApiResponse();
			apiResponse.setStatus(apiResponseConfig.isValidationFailureStatus());
			apiResponse.setStatusCode(apiResponseConfig.getValidationFailureStatusCode());
			apiResponse.setMessage(firstError.getDefaultMessage());
			apiResponse.setData(null);
			return apiResponse;
		}
		return null;

	}

	@ExceptionHandler(MissingAuthorizationHeaderException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ApiResponse handleMissingAuthorizationHeader(MissingAuthorizationHeaderException ex) {
		ApiResponse response = new ApiResponse();
		response.setStatus(false);
		response.setStatusCode("401");
		response.setMessage("Please provide the token");
		response.setData(null);
		return response;
	}

	@ExceptionHandler(RuntimeException.class)
	public ApiResponse handleRuntimeException(RuntimeException e) {
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setStatus(false);
		apiResponse.setStatusCode("500");
		apiResponse.setMessage("");
		apiResponse.setData(null);

		return apiResponse;
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public ApiResponse handleDuplicateKeyException(DuplicateKeyException ex) {
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setStatus(false);
		apiResponse.setStatusCode("409");
		apiResponse.setMessage("Duplicate key violation");
		apiResponse.setData(null);
		return apiResponse;
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ApiResponse handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setStatus(false);
		apiResponse.setStatusCode("400");
		apiResponse.setMessage("Data integrity violation");
		apiResponse.setData(null);
		return apiResponse;
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setStatus(false);
		apiResponse.setStatusCode("403");
		apiResponse.setMessage("Access Denied: " + ex.getMessage());
		apiResponse.setData(null);
		return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(NullClaimsException.class)
	public ApiResponse handleNullClaimsException(NullClaimsException ex) {
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setStatus(false);
		apiResponse.setStatusCode("403");
		apiResponse.setMessage("Access Denied: " + ex.getMessage());
		apiResponse.setData(null);
		return apiResponse;
	}
}
