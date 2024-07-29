package com.extwebtech.registration.bean;

import java.sql.Timestamp;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "otpDetails")
public class OtpDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String email;
	private String mobileNumber;

	private String otp;

	@Column(name = "created_date")
	private Timestamp createdDate;

	@PrePersist
	protected void onCreate() {
		createdDate = new Timestamp(new Date().getTime());
	}

}