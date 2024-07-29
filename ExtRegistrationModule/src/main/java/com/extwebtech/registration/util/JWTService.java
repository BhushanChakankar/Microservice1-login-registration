package com.extwebtech.registration.util;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.extwebtech.registration.exception.NullClaimsException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JWTService {

	private static final Logger logger = Logger.getLogger(JWTService.class);

	@Value("${app.jwt.secretKey}")
	private String secretKey;

	public String generateToken(String mobileNumber, int roleId, int userId, int loginId) {
		try {
			Claims claims = Jwts.claims().setSubject(mobileNumber);
			claims.put("role", roleId);
			claims.put("userId", userId);
			claims.put("loginId", loginId);
			Date now = new Date();
			Date expiration = new Date(now.getTime() + 1000 * 60 * 60 * 10);
			String token = Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(expiration)
					.signWith(SignatureAlgorithm.HS256, secretKey).compact();

			logger.info("Token generated for: " + mobileNumber + " with role: " + roleId + ", userId: " + userId
					+ ", loginId: " + loginId);
			return token;
		} catch (JwtException e) {
			logger.error("Token generation failed: " + e.getMessage());
			return null;
		}
	}

	public boolean validateToken(String token, String mobileNumber) {
		try {
			Claims claims = extractClaims(token);
			String tokenMobileNumber = claims.getSubject();

			return tokenMobileNumber.equals(mobileNumber) && !isTokenExpired(claims);
		} catch (Exception e) {
			logger.error("Error during token validation: " + e.getMessage());
			return false;
		}
	}

	public boolean isTokenExpired(Claims claims) {
		Date expiration = claims.getExpiration();
		boolean isExpired = expiration.before(new Date());
		if (isExpired) {
			logger.info("Token has expired.");
		} else {
			logger.info("Token is still valid.");
		}

		return isExpired;
	}

	public String extractMobileNumber(String token) {
		Claims claims = extractClaims(token);
		String mobileNumber = claims.getSubject();
		logger.info("Extracted mobile number from token: " + mobileNumber);
		return mobileNumber;
	}

	public int extractRole(String token) {
		Claims claims = extractClaims(token);
		int role = claims.get("role", Integer.class);
		logger.info("Extracted role from token: " + role);
		return role;
	}

	public Claims extractClaims(String token) {
		Claims claims = null;
		try {
			claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
			logger.info("Claims extracted from token.");
		} catch (Exception e) {
			logger.error("Error extracting claims from token: " + e.getMessage());
			throw new RuntimeException("Token is Invalid .Please pass the valid Token!");
		}
		return claims;
	}

	public int extractUserId(String token) {
		Claims claims = extractClaims(token);
		if (claims == null) {
			throw new NullClaimsException("Claims are null. User ID extraction failed.");
		}
		int userId = claims.get("userId", Integer.class);
		logger.info("Extracted user ID from token: " + userId);
		return userId;
	}

	public int extractLoginId(String token) {
		try {
			Claims claims = extractClaims(token);
			return claims.get("loginId", Integer.class);
		} catch (Exception e) {
			return -1;
		}
	}

	public boolean validateToken1(String token) {
		try {
			Claims claims = extractClaims(token);
			isTokenExpired(claims);

			return Boolean.TRUE;
		} catch (Exception e) {
			logger.error("Error during token validation: " + e.getMessage());
			return false;
		}
	}

}
