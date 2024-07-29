package com.extwebtech.registration.bean;

import java.sql.Timestamp;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

@Entity
@Data
public class Referral {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer referrerId;
	private String friendEmail;
	private String referralCode;
	private Date referralDate;

	private Timestamp createdDate;
	private Timestamp updatedDate;

	@PrePersist
	protected void onCreate() {
		createdDate = new Timestamp(new Date().getTime());
		updatedDate = new Timestamp(new Date().getTime());
	}

	@PreUpdate
	protected void onUpdate() {
		updatedDate = new Timestamp(new Date().getTime());
	}

}
