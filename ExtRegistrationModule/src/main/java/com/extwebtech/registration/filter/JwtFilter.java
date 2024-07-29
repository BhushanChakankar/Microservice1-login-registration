package com.extwebtech.registration.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.util.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtFilter extends GenericFilterBean {

	private final JWTService jwtService;
	private final int[] allowedRole;

	public JwtFilter(JWTService jwtService, int[] allowedRole) {
		this.jwtService = jwtService;
		this.allowedRole = allowedRole;
	}

	private boolean containsRole(int role, int[] allowedRoles) {
		for (int allowedRole : allowedRoles) {
			if (role == allowedRole) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

			try {
				String mobilenumbeer = jwtService.extractMobileNumber(token);
				int role = jwtService.extractRole(token);
				int userId = jwtService.extractUserId(token);
				Claims claims = jwtService.extractClaims(token);
				if (jwtService.isTokenExpired(claims)) {
					sendErrorResponse(response, "Token has expired.", HttpStatus.UNAUTHORIZED.value());
					return;
				}

				if (!containsRole(role, allowedRole)) {
					sendErrorResponse(response, "Access denied. Invalid role.", HttpStatus.FORBIDDEN.value());
					return;
				}

				if (!jwtService.validateToken(token, mobilenumbeer)) {
					sendErrorResponse(response, "Token validation failed.", HttpStatus.UNAUTHORIZED.value());
					return;
				}

				request.setAttribute("userId", userId);
				filterChain.doFilter(request, response);
			} catch (Exception e) {
				sendErrorResponse(response, "Invalid token. Please provide a valid token.",
						HttpStatus.UNAUTHORIZED.value());
			}
		} else if (request.getRequestURI().endsWith("/register")) {
			filterChain.doFilter(request, response);
		} else {
			sendErrorResponse(response, "Please provide the token.", HttpStatus.FORBIDDEN.value());
		}
	}

	private void sendErrorResponse(HttpServletResponse response, String message, int httpStatus) throws IOException {
		response.setStatus(httpStatus);
		response.setContentType("application/json");

		ApiResponse errorResponse = new ApiResponse();
		errorResponse.setStatus(false);
		errorResponse.setStatusCode(String.valueOf(httpStatus));
		errorResponse.setMessage(message);
		errorResponse.setData(null);

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonResponse = objectMapper.writeValueAsString(errorResponse);
		response.getWriter().write(jsonResponse);
	}
}
