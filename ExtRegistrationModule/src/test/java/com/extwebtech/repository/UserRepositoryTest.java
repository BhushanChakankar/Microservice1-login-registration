package com.extwebtech.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.extwebtech.registration.bean.Subscriptions;
import com.extwebtech.registration.bean.User;
import com.extwebtech.registration.repository.RegistrationRepository;

@DataJpaTest
public class UserRepositoryTest {

	@Autowired
	private RegistrationRepository registrationRepository;

	@Test
	void testGetById() {
		User dummyUser = createDummyUser();
		registrationRepository.save(dummyUser);
		User foundUser = registrationRepository.getById(dummyUser.getId());
		assertEquals(dummyUser, foundUser);
	}

	@Test
	void testFindAllBySubscriptionIsNotNull() {
		User dummyUser1 = createDummyUser();
		User dummyUser2 = createAnotherDummyUser();
		registrationRepository.saveAll(Arrays.asList(dummyUser1, dummyUser2));
		List<User> foundUsers = registrationRepository.findAllBySubscriptionIsNotNull();
		assertEquals(0, foundUsers.size());
	}

	@Test
	void testExistsBySubscriptionId() {
		User dummyUser = createDummyUser();
		registrationRepository.save(dummyUser);
		boolean exists = registrationRepository.existsBySubscriptionId(dummyUser.getId());
		assertTrue(exists);
	}

	@Test
	void testFindBySubscriptionNotNullAndId() {
		User dummyUser = createDummyUser();
		registrationRepository.save(dummyUser);
		Optional<User> foundUser = Optional
				.ofNullable(registrationRepository.findBySubscriptionNotNullAndId(dummyUser.getId()));
		assertTrue(foundUser.isPresent());
		assertEquals(dummyUser, foundUser.get());
	}

	private User createDummyUser() {
		User user = new User();
		user.setName("John Doe");
		user.setMobile("1234567890");
		user.setEmail("john.doe@example.com");
		user.setPassword("password123");
		user.setRoleId(1);
		user.setCountryId(1);
		user.setActive(true);
		user.setSubscription(createDummySubscription());
		user.setSubscriptionStartDate(new Timestamp(System.currentTimeMillis()));
		user.setSubscriptionDaysRemaining(30);
		return user;
	}

	private User createAnotherDummyUser() {
		User user = new User();
		user.setName("Jane Doe");
		user.setMobile("9876543210");
		user.setEmail("jane.doe@example.com");
		user.setPassword("pass456");
		user.setRoleId(2);
		user.setCountryId(2);
		user.setActive(true);
		return user;
	}

	private Subscriptions createDummySubscription() {
		Subscriptions subscription = new Subscriptions();
		subscription.setPlanName("Basic");
		subscription.setPrice(new BigDecimal("19.99"));
		subscription.setDurations("1 month");
		subscription.setRecommended(true);
		subscription.setFeatures("Basic features");
		subscription.setRoleId(1);
		subscription.setActive(true);
		return subscription;
	}
}
