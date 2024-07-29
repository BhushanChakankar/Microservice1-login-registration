package com.extwebtech.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.extwebtech.registration.bean.ApiResponse;
import com.extwebtech.registration.service.ReferralService;

import jakarta.validation.constraints.Email;

/**
 * Controller class for handling operations related to referrals.
 */
@RestController
@RequestMapping("/api/referral")
@CrossOrigin("*")
public class ReferralController {
	/**
	 * Autowired instance of ReferralService for handling business logic related to
	 * referrals.
	 */
	@Autowired
	ReferralService referralService;

	/**
	 * Endpoint to send a referral to a friend via email.
	 *
	 * @param header      Authorization header
	 * @param friendEmail Email address of the friend to whom the referral is being
	 *                    sent
	 * @return ApiResponse containing the result of sending the referral
	 */
	@PostMapping("/send")
	public ApiResponse sendReferral(@RequestHeader("Authorization") String header,
			@RequestParam @Email String friendEmail) {
		return referralService.sendReferral(header, friendEmail);
	}

	/**
	 * Endpoint to retrieve all referrals associated with the user.
	 *
	 * @param header Authorization header
	 * @return ApiResponse containing the list of all referrals
	 */
	@GetMapping("/getAllReferrals")
	public ApiResponse getAllReferrals(@RequestHeader("Authorization") String header) {
		return referralService.getAllReferrals(header);
	}

	/**
	 * Endpoint to delete a specific referral by its ID.
	 *
	 * @param header     Authorization header
	 * @param referralId ID of the referral to delete
	 * @return ApiResponse containing the result of deleting the referral
	 */
	@DeleteMapping("/deleteReferral/{referralId}")
	public ApiResponse deleteReferral(@RequestHeader("Authorization") String header, @PathVariable Long referralId) {
		return referralService.deleteReferral(header, referralId);
	}
}
