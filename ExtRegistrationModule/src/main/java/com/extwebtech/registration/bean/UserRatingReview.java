package com.extwebtech.registration.bean;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class UserRatingReview {
	private Integer userId;
	private String userName;
	private String userMobile;
	private String userEmail;
	private String userProfilePhoto;
	private String businessName;
	private String ownerName;
	private String gst;
	private String accountHolderName;
	private String accountName;
	private String ifscCode;
	private String bankUpi;
	private Integer roleId;
	private Integer languageId;
	private Timestamp createdDate;
	private Timestamp updatedDate;
	private Integer countryId;
	private String businessAddress;
	private String extraField;
	private String[] officialDocuments;
	private Boolean active;
	private Subscriptions subscription;
	private Timestamp subscriptionStartDate;
	private Integer subscriptionDaysRemaining;
	private String referralCode;

}
