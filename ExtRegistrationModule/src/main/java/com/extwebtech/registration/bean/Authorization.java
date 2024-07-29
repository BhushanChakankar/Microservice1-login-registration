package com.extwebtech.registration.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "authorize")
public class Authorization {

	@Id
	private int id;

	@Column(name = "is_authorized")
	private boolean authorization;
}
