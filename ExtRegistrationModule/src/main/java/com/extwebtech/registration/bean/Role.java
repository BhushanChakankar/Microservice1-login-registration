package com.extwebtech.registration.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "roles", schema = "public")
@Data
public class Role {

	@Id
	@Column(name = "id")
	private Integer roleId;

	@Column(name = "role_name", nullable = false, length = 255)
	private String roleName;

	@Column(name = "permissions")
	private String permissions;
}
