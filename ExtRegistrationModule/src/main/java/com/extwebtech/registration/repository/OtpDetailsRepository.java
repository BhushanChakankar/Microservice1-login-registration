package com.extwebtech.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.extwebtech.registration.bean.OtpDetails;

/**
 * Repository interface for handling database operations related to OTP details.
 */
@Repository
public interface OtpDetailsRepository extends JpaRepository<OtpDetails, Integer> {

	/**
	 * Retrieves the latest OTP details for a given email address.
	 * 
	 * @param email The email address
	 * @return The latest OTP details for the email
	 */
	@Query(value = "select * from otpDetails where email Ilike :email order by created_date desc limit 1", nativeQuery = true)
	OtpDetails getOtdDeatils(String email);

	/**
	 * Retrieves the latest OTP details for a given mobile number.
	 * 
	 * @param mobile The mobile number
	 * @return The latest OTP details for the mobile number
	 */
	@Query(value = "select * from otpDetails where mobilenumber = :mobile order by created_date desc limit 1", nativeQuery = true)
	OtpDetails getOtdDeatilsWithMobile(String mobile);
}