package com.extwebtech.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.extwebtech.registration.bean.LoginEmail;
import com.extwebtech.registration.bean.Subscriptions;
import com.extwebtech.registration.exception.CommonException;
import com.extwebtech.registration.exception.UserNotFoundException;
import com.extwebtech.registration.repository.EmailLoginRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

class EmailLoginRepositoryTest {

	@Mock
	private EntityManager entityManager;

	@InjectMocks
	private EmailLoginRepository emailLoginRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testInsertOrUpdateUserDataEmail_WhenUserExists() {
		// Mocking
		Query checkQuery = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).thenReturn(checkQuery);
		when(checkQuery.setParameter(anyString(), any())).thenReturn(checkQuery);
		when(checkQuery.getSingleResult()).thenReturn(1L);

		Query updateQuery = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).thenReturn(updateQuery);
		when(updateQuery.executeUpdate()).thenReturn(1);

		// Test
		emailLoginRepository.insertOrUpdateUserDataEmail("test@email.com", "{}", "token", "type", 1);

		// Verify
		verify(checkQuery).setParameter("email", "test@email.com");
		verify(updateQuery).setParameter("email", "test@email.com");
		verify(updateQuery).setParameter("userDataJson", "{}");
		verify(updateQuery).setParameter("deviceToken", "token");
		verify(updateQuery).setParameter("deviceType", "type");
		verify(updateQuery).setParameter("userId", 1);
		verify(updateQuery).executeUpdate();
	}

	@Test
	void testInsertOrUpdateUserDataEmail_WhenUserDoesNotExist() {
		// Mocking
		Query checkQuery = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).thenReturn(checkQuery);
		when(checkQuery.setParameter(anyString(), any())).thenReturn(checkQuery);
		when(checkQuery.getSingleResult()).thenReturn(0L);

		Query insertQuery = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).thenReturn(insertQuery);
		when(insertQuery.executeUpdate()).thenReturn(1);

		// Test
		emailLoginRepository.insertOrUpdateUserDataEmail("test@email.com", "{}", "token", "type", 1);

		// Verify
		verify(checkQuery).setParameter("email", "test@email.com");
		verify(insertQuery).setParameter("email", "test@email.com");
		verify(insertQuery).setParameter("userDataJson", "{}");
		verify(insertQuery).setParameter("deviceToken", "token");
		verify(insertQuery).setParameter("deviceType", "type");
		verify(insertQuery).setParameter("userId", 1);
		verify(insertQuery).executeUpdate();
	}

	@Test
	void testFindByEmail_WhenUserExists() {
		// Mocking
		Query query = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).thenReturn(query);
		when(query.setParameter(anyString(), any())).thenReturn(query);
		when(query.getResultStream().findFirst())
				.thenReturn(Optional.of(new Object[] { 1, "test@email.com", "password" }));

		// Test
		
		LoginEmail loginEmail = new LoginEmail();
		loginEmail.setEmail("test@email.com");
		loginEmail.setPassword("password");
		Object[] result = emailLoginRepository.findByEmail(loginEmail);

		// Verify
		assertNotNull(result);
		assertEquals(1, result[0]);
		assertEquals("test@email.com", result[1]);
		assertEquals("password", result[2]);
	}

	@Test
	void testFindByEmail_WhenUserDoesNotExist() {
		// Mocking
		Query query = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).thenReturn(query);
		when(query.setParameter(anyString(), any())).thenReturn(query);
		when(query.getResultStream().findFirst()).thenReturn(Optional.empty());

		LoginEmail loginEmail = new LoginEmail();
		loginEmail.setEmail("nonexistent@email.com");
		loginEmail.setPassword("password");
		// Test
		assertThrows(UserNotFoundException.class, () -> {
			
			emailLoginRepository.findByEmail(loginEmail);
		});
	}

	@Test
	void testFetchSubscriptionDetails_WhenSubscriptionExists() {
		// Mocking
		TypedQuery<Subscriptions> query = mock(TypedQuery.class);
		when(entityManager.createQuery(anyString(), eq(Subscriptions.class))).thenReturn(query);
		when(query.setParameter(anyString(), any())).thenReturn(query);
		when(query.getSingleResult()).thenReturn(new Subscriptions());

		// Test
		Subscriptions result = emailLoginRepository.fetchSubscriptionDetails(1);

		// Verify
		assertNotNull(result);
	}

	@Test
	void testFetchSubscriptionDetails_WhenSubscriptionDoesNotExist() {
		// Mocking
		TypedQuery<Subscriptions> query = mock(TypedQuery.class);
		when(entityManager.createQuery(anyString(), eq(Subscriptions.class))).thenReturn(query);
		when(query.setParameter(anyString(), any())).thenReturn(query);
		when(query.getSingleResult()).thenThrow(new NoResultException());

		// Test
		Subscriptions result = emailLoginRepository.fetchSubscriptionDetails(1);

		// Verify
		assertNull(result);
	}

	@Test
	void testCheckIfUserExists_WhenUserExists() {
		// Mocking
		Query query = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).thenReturn(query);
		when(query.setParameter(anyString(), any())).thenReturn(query);
		when(query.getSingleResult()).thenReturn(1);

		// Test
		boolean result = emailLoginRepository.checkIfUserExists("test@email.com");

		// Verify
		assertTrue(result);
	}

	@Test
	void testCheckIfUserExists_WhenUserDoesNotExist() {
		// Mocking
		Query query = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).thenReturn(query);
		when(query.setParameter(anyString(), any())).thenReturn(query);
		when(query.getSingleResult()).thenReturn(0);

		// Test
		boolean result = emailLoginRepository.checkIfUserExists("nonexistent@email.com");

		// Verify
		assertFalse(result);
	}

	@Test
	void testCheckIfUserExistsWithUserId_WhenUserExistsWithDifferentUserId() {
		// Mocking
		Query query = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).thenReturn(query);
		when(query.setParameter(anyString(), any())).thenReturn(query);
		when(query.getSingleResult()).thenReturn(1);

		// Test
		boolean result = emailLoginRepository.checkIfUserExistsWIthuserId("test@email.com", 2);

		// Verify
		assertTrue(result);
	}

	@Test
	void testCheckIfUserExistsWithUserId_WhenUserDoesNotExist() {
		// Mocking
		Query query = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).thenReturn(query);
		when(query.setParameter(anyString(), any())).thenReturn(query);
		when(query.getSingleResult()).thenReturn(0);

		// Test
		boolean result = emailLoginRepository.checkIfUserExistsWIthuserId("nonexistent@email.com", 2);

		// Verify
		assertFalse(result);
	}

//	@Test
//	void testUpdateDeviceToken_WhenUpdateSucceeds() {
//		// Mocking
//		Query updateQuery = mock(Query.class);
//		when(entityManager.createNativeQuery(anyString())).thenReturn(updateQuery);
//		when(updateQuery.executeUpdate()).thenReturn(1);
//
//		// Test
//		int result = emailLoginRepository.updateDeviceToken("details", "newToken");
//
//		// Verify
//		assertEquals(1, result);
//	}
//
//	@Test
//	void testUpdateDeviceToken_WhenUpdateFails() {
//		// Mocking
//		Query updateQuery = mock(Query.class);
//		when(entityManager.createNativeQuery(anyString())).thenReturn(updateQuery);
//		when(updateQuery.executeUpdate()).thenReturn(0);
//
//		// Test
//		assertThrows(CommonException.class, () -> {
//			emailLoginRepository.updateDeviceToken("details", "newToken");
//		});
//	}

	@Test
	void testDeleteLoginDetails_WhenDeleteSucceeds() {
		// Mocking
		Query query = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).thenReturn(query);
		when(query.executeUpdate()).thenReturn(1);

		// Test
		int result = emailLoginRepository.deleteLoginDetails("details");

		// Verify
		assertEquals(1, result);
	}

	@Test
	void testDeleteLoginDetails_WhenDeleteFails() {
		// Mocking
		Query query = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).thenReturn(query);
		when(query.executeUpdate()).thenReturn(0);

		// Test
		assertThrows(CommonException.class, () -> {
			emailLoginRepository.deleteLoginDetails("details");
		});
	}

	@Test
	void testGetPassword_WhenUserExists() {
		// Mocking
		Query query = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).thenReturn(query);
		when(query.setParameter(anyString(), any())).thenReturn(query);
		when(query.getResultStream().findFirst()).thenReturn(Optional.of("password"));

		// Test
		String result = emailLoginRepository.getPassword("test@email.com");

		// Verify
		assertEquals("password", result);
	}

	@Test
	void testGetPassword_WhenUserDoesNotExist() {
		// Mocking
		Query query = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).thenReturn(query);
		when(query.setParameter(anyString(), any())).thenReturn(query);
		when(query.getResultStream().findFirst()).thenReturn(Optional.empty());

		// Test
		assertThrows(UserNotFoundException.class, () -> {
			emailLoginRepository.getPassword("nonexistent@email.com");
		});
	}
}
