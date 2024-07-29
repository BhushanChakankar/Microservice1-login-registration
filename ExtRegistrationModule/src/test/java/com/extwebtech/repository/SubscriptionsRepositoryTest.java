package com.extwebtech.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.extwebtech.registration.bean.Subscriptions;
import com.extwebtech.registration.repository.SubscriptionsRepository;

@SpringBootTest
public class SubscriptionsRepositoryTest {

	@MockBean
	private SubscriptionsRepository subscriptionsRepository;

	@Test
	void testFindByPlanName() {
		Subscriptions dummySubscription = createDummySubscription();
		when(subscriptionsRepository.findByPlanName("Basic")).thenReturn(dummySubscription);
		Subscriptions foundSubscription = subscriptionsRepository.findByPlanName("Basic");
		assertEquals(dummySubscription, foundSubscription);
	}

	@Test
    void testExistsByPlanNameIgnoreCase() {
        when(subscriptionsRepository.existsByPlanNameIgnoreCase("Basic")).thenReturn(true);
        boolean exists = subscriptionsRepository.existsByPlanNameIgnoreCase("Basic");
        assertTrue(exists);
    }

	private Subscriptions createDummySubscription() {
		Subscriptions subscription = new Subscriptions();
		subscription.setId(1); // Assuming you want to set the id property
		subscription.setPlanName("Basic");
		subscription.setPrice(new BigDecimal("19.99"));
		subscription.setDurations("1 month");
		subscription.setRecommended(true);
		subscription.setFeatures("Basic features");
		subscription.setPlanDetails(Arrays.asList("Detail 1", "Detail 2"));
		subscription.setRoleId(1);
		subscription.setCreatedBy(123);
		subscription.setUpdatedBy(123);

		return subscription;
	}
}
