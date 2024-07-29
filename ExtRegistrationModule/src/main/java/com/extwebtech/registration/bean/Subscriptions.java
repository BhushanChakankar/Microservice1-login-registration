package com.extwebtech.registration.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class Subscriptions {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotEmpty(message = "Plan name cannot be empty")
	@Column(unique = true)
	private String planName;

	@NotNull(message = "Price cannot be null")
	private BigDecimal price;

	@NotEmpty(message = "Duration cannot be empty")
	private String durations;

	private Boolean recommended;

	@NotEmpty(message = "Features cannot be empty")
	private String features;

	@Column(name = "created_date")
	private Timestamp createdDate;

	@Column(name = "updated_date")
	private Timestamp updatedDate;

	@Column(name = "plan_details")
	private List<String> planDetails;

	@Column(name = "role_id")
	private Integer roleId;

	@Column(name = "active", nullable = false)
	private boolean active;

	public Subscriptions() {
		this.active = true;
	}

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

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
