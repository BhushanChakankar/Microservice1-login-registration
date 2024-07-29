package com.extwebtech.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.extwebtech.registration.bean.UserAddress;
import com.extwebtech.registration.repository.UserAddressRepository;

@SpringBootTest
public class UserAddressRepositoryTest {

	@MockBean
	private UserAddressRepository userAddressRepository;

	@Test
	void testSaveUserAddress() {
		// Arrange
		UserAddress dummyUserAddress = createDummyUserAddress();
		when(userAddressRepository.save(dummyUserAddress)).thenReturn(dummyUserAddress);

		// Act
		UserAddress savedUserAddress = userAddressRepository.save(dummyUserAddress);

		// Assert
		assertEquals(dummyUserAddress, savedUserAddress);
	}

	@Test
	void testFindByUserId() {
		// Arrange
		UserAddress dummyUserAddress1 = createDummyUserAddress();
		UserAddress dummyUserAddress2 = createAnotherDummyUserAddress();
		List<UserAddress> dummyUserAddresses = Arrays.asList(dummyUserAddress1, dummyUserAddress2);
		when(userAddressRepository.findByUserId(1)).thenReturn(dummyUserAddresses);

		// Act
		List<UserAddress> foundUserAddresses = userAddressRepository.findByUserId(1);

		// Assert
		assertEquals(dummyUserAddresses, foundUserAddresses);
	}

	@Test
	void testFindByUserIdAndAddressId() {
		// Arrange
		UserAddress dummyUserAddress = createDummyUserAddress();
		when(userAddressRepository.findByUserIdAndAddressId(1, 1)).thenReturn(Optional.of(dummyUserAddress));

		// Act
		Optional<UserAddress> foundUserAddress = userAddressRepository.findByUserIdAndAddressId(1, 1);

		// Assert
		assertEquals(Optional.of(dummyUserAddress), foundUserAddress);
	}

	@Test
	void testFindDefaultByUserId() {
		// Arrange
		UserAddress dummyUserAddress = createDummyUserAddress();
		when(userAddressRepository.findDefaultByUserId(1)).thenReturn(Optional.of(dummyUserAddress));

		// Act
		Optional<UserAddress> foundDefaultUserAddress = userAddressRepository.findDefaultByUserId(1);

		// Assert
		assertEquals(Optional.of(dummyUserAddress), foundDefaultUserAddress);
	}

	private UserAddress createDummyUserAddress() {
		UserAddress userAddress = new UserAddress();
		userAddress.setUserId(1);
		userAddress.setStreetAddress("123 Main Street");
		userAddress.setCity("Cityville");
		userAddress.setPostalCode("12345");
		userAddress.setCountry("Countryland");
		userAddress.setState("Stateland");
		userAddress.setHouseBuildingName("Building A");
		userAddress.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		userAddress.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
		userAddress.setTitle("Mr.");
		userAddress.setDefaultAddress(true);
		userAddress.setLatitude(40.7128); // Example latitude value
		userAddress.setLongitude(-74.0060); // Example longitude value
		userAddress.setExtraField1("Extra Field Value");
		userAddress.setAddressType("Home");

		return userAddress;
	}

	private UserAddress createAnotherDummyUserAddress() {
		UserAddress userAddress = new UserAddress();
		// Set different values for different test scenarios
		userAddress.setUserId(2);
		userAddress.setStreetAddress("456 Oak Street");
		userAddress.setCity("Townsville");
		userAddress.setPostalCode("54321");
		userAddress.setCountry("Otherland");
		userAddress.setState("Otherstate");
		userAddress.setHouseBuildingName("Building B");
		userAddress.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		userAddress.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
		userAddress.setTitle("Mrs.");
		userAddress.setDefaultAddress(false);
		userAddress.setLatitude(35.6895); // Another example latitude value
		userAddress.setLongitude(-105.9378); // Another example longitude value
		userAddress.setExtraField1("Another Extra Field Value");
		userAddress.setAddressType("Work");

		return userAddress;
	}
}
