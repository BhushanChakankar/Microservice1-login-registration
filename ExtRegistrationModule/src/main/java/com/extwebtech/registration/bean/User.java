package com.extwebtech.registration.bean;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "users", schema = "public")
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "name", nullable = false, length = 255)
	@NotNull(message = "Name is required")
	@Size(max = 255, message = "Name must not exceed 255 characters")
	private String name;

	@Column(name = "mobile", nullable = false, length = 15)
	@NotNull(message = "Mobile is required")
	@Size(max = 15, message = "Mobile must not exceed 15 characters")
	private String mobile;

	@Column(name = "email", nullable = false, length = 55, unique = true)
	@NotNull(message = "Email is required")
	//@Size(max = 25, message = "Email must not exceed 25 characters")
	@Email(message = "Email should be a valid email address")
	private String email;

	@Column(name = "profile_photo", length = 255)
	private String profilePhoto;

	@Column(name = "password", nullable = false, length = 255)
	@NotNull(message = "Password is required")
	@Size(max = 255, message = "Password must not exceed 255 characters")
	private String password;

	@Column(name = "business_name", length = 255)
	private String businessName;

	@Column(name = "business_address", length = 255)
	private String businessAddress;

	@Column(name = "owner_name", length = 255)
	private String ownerName;

	@Column(name = "gst", length = 15)
	private String gst;

	@Column(name = "account_holder_name", length = 255)
	private String accountHolderName;

	@Column(name = "account_name", length = 255)
	private String accountName;

	@Column(name = "ifsc_code", length = 15)
	private String ifscCode;

	@Column(name = "bank_upi", length = 255)
	private String bankUpi;

	@Column(name = "official_documents")
	private List<String> officialDocuments;

	@Column(name = "role_id", nullable = false)
	private Integer roleId;

	@Column(name = "language_id")
	private Integer languageId;

	@Column(name = "created_date")
	private Timestamp createdDate;

	@Column(name = "updated_date")
	private Timestamp updatedDate;

	@Column(name = "country_id")
	private Integer countryId;

	@Column(name = "extra_field")
	private String extraField;

	@Column(name = "active", nullable = false)
	private boolean active;

	public User() {
		this.active = true;
	}

	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	@JoinColumn(name = "subscription_id")
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Subscriptions subscription;

	@Column(name = "subscription_start_date")
	private Timestamp subscriptionStartDate;

	@Column(name = "subscription_days_remaining")
	private Integer subscriptionDaysRemaining;
	
	@Column(name = "referral_code", length = 6)
	private String referralCode;


	@PrePersist
	protected void onCreate() {
		createdDate = new Timestamp(new Date().getTime());
		updatedDate = new Timestamp(new Date().getTime());
	}

	@PreUpdate
	protected void onUpdate() {
		updatedDate = new Timestamp(new Date().getTime());
	}
	

	public void updateFields(User updatedUser) {
		if (updatedUser.getName() != null) {
			this.setName(updatedUser.getName());
		}
		if (updatedUser.getMobile() != null) {
			this.setMobile(updatedUser.getMobile());
		}
		if (updatedUser.getEmail() != null) {
			this.setEmail(updatedUser.getEmail());
		}
		if (updatedUser.getProfilePhoto() != null) {
			this.setProfilePhoto(updatedUser.getProfilePhoto());
		}
		if (updatedUser.getPassword() != null) {
			this.setPassword(updatedUser.getPassword());
		}
		if (updatedUser.getBusinessName() != null) {
			this.setBusinessName(updatedUser.getBusinessName());
		}
		if (updatedUser.getBusinessAddress() != null) {
			this.setBusinessAddress(updatedUser.getBusinessAddress());
		}
		if (updatedUser.getOwnerName() != null) {
			this.setOwnerName(updatedUser.getOwnerName());
		}
		if (updatedUser.getGst() != null) {
			this.setGst(updatedUser.getGst());
		}
		if (updatedUser.getAccountHolderName() != null) {
			this.setAccountHolderName(updatedUser.getAccountHolderName());
		}
		if (updatedUser.getAccountName() != null) {
			this.setAccountName(updatedUser.getAccountName());
		}
		if (updatedUser.getIfscCode() != null) {
			this.setIfscCode(updatedUser.getIfscCode());
		}
		if (updatedUser.getBankUpi() != null) {
			this.setBankUpi(updatedUser.getBankUpi());
		}
		if (updatedUser.getOfficialDocuments() != null) {
			this.setOfficialDocuments(updatedUser.getOfficialDocuments());
		}
		if (updatedUser.getRoleId() != null) {
			this.setRoleId(updatedUser.getRoleId());
		}
		if (updatedUser.getLanguageId() != null) {
			this.setLanguageId(updatedUser.getLanguageId());
		}
		if (updatedUser.getCountryId() != null) {
			this.setCountryId(updatedUser.getCountryId());
		}
		if (updatedUser.isActive()) {
			this.setActive(updatedUser.isActive());
		}
		if (updatedUser.getExtraField() != null) {
			this.setExtraField(updatedUser.getExtraField());
		}

	}
}