package com.extwebtech.registration.bean;

import java.sql.Timestamp;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "userAddress")
@Data
public class UserAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer addressId;

	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "street_address", length = 255)
	private String streetAddress;

	@Column(name = "city", length = 255)
	private String city;

	@Column(name = "postal_code", length = 15)
	private String postalCode;

	@Column(name = "country", length = 255)
	private String country;

	@Column(name = "state", length = 255)
	private String state;

	@Column(name = "house_building_name", length = 255)
	private String houseBuildingName;

	@Column(name = "created_date")
	private Timestamp createdDate;

	@Column(name = "updated_date")
	private Timestamp updatedDate;

	@Column(name = "title")
	private String title;

	@Column(name = "is_default")
	private boolean isDefaultAddress;

	@Column(name = "latitude")
	private Double latitude;

	@Column(name = "longitude")
	private Double longitude;

	@Column(name = "extra_field")
	private String extraField1;

	@Column(name = "address_type", length = 50)
	private String addressType;

	@Column(name = "mobile_number", length = 15)
	private String mobileNumber;

	@Column(name = "name", length = 255)
	private String name;

	@PrePersist
	protected void onCreate() {
		createdDate = new Timestamp(new Date().getTime());
		updatedDate = new Timestamp(new Date().getTime());
	}

	@PreUpdate
	protected void onUpdate() {
		updatedDate = new Timestamp(new Date().getTime());
	}

	public void updateFields(UserAddress updatedAddress) {
		if (updatedAddress.getStreetAddress() != null) {
			this.setStreetAddress(updatedAddress.getStreetAddress());
		}
		if (updatedAddress.getCity() != null) {
			this.setCity(updatedAddress.getCity());
		}
		if (updatedAddress.getPostalCode() != null) {
			this.setPostalCode(updatedAddress.getPostalCode());
		}
		if (updatedAddress.getCountry() != null) {
			this.setCountry(updatedAddress.getCountry());
		}
		if (updatedAddress.getState() != null) {
			this.setState(updatedAddress.getState());
		}
		if (updatedAddress.getHouseBuildingName() != null) {
			this.setHouseBuildingName(updatedAddress.getHouseBuildingName());
		}
		if (updatedAddress.getTitle() != null) {
			this.setTitle(updatedAddress.getTitle());
		}
		if (updatedAddress.isDefaultAddress() != this.isDefaultAddress) {
			this.isDefaultAddress = updatedAddress.isDefaultAddress();
		}
		if (updatedAddress.getLatitude() != null) {
			this.setLatitude(updatedAddress.getLatitude());
		}
		if (updatedAddress.getLongitude() != null) {
			this.setLongitude(updatedAddress.getLongitude());
		}
		if (updatedAddress.getExtraField1() != null) {
			this.setExtraField1(updatedAddress.getExtraField1());
		}
		if (updatedAddress.getAddressType() != null) {
			this.setAddressType(updatedAddress.getAddressType());
		}
		if (updatedAddress.getMobileNumber() != null) {
			this.setMobileNumber(updatedAddress.getMobileNumber());
		}
		if (updatedAddress.getName() != null) {
			this.setName(updatedAddress.getName());
		}
	}
}