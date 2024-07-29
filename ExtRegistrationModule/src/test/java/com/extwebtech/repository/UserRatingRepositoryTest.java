package com.extwebtech.repository;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.extwebtech.registration.configuration.ApiResponseConfig;
import com.extwebtech.registration.repository.UserRatingRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

@RunWith(MockitoJUnitRunner.class)
public class UserRatingRepositoryTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ApiResponseConfig apiResponseConfig;

    @InjectMocks
    private UserRatingRepository userRatingRepository;

    @Before(value = "")
    public void setUp() {
    	 apiResponseConfig = mock(ApiResponseConfig.class);
        when(apiResponseConfig.getUserIdColumn()).thenReturn("userId");
    }

    @Test
    public void testGetUserInfoByUserId() {
        Integer userId = 1;
        Object[] expectedUserInfo = {/* Mocked user data */};
        Query queryMock = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(queryMock);
        when(queryMock.setParameter(anyString(), any())).thenReturn(queryMock);
        when(queryMock.getSingleResult()).thenReturn(expectedUserInfo);
        Object[] actualUserInfo = userRatingRepository.getUserInfoByUserId(userId);
        assertNotNull(actualUserInfo);
        assertArrayEquals(expectedUserInfo, actualUserInfo);
    }

    @Test
    public void testGetUserInfoByUserIdNoResultException() {
        Integer userId = 1;
        Query queryMock = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(queryMock);
        when(queryMock.setParameter(anyString(), any())).thenReturn(queryMock);
        doThrow(new NoResultException()).when(queryMock).getSingleResult();
        Object[] actualUserInfo = userRatingRepository.getUserInfoByUserId(userId);
        assertNull(actualUserInfo);
    }

    @Test
    public void testGetAllUsersWithAverageRatings() {
        String expectedSql = "SELECT u.*, AVG(rr.ratings) AS average_rating FROM users u LEFT JOIN ratings_reviews rr ON u.user_id = rr.seller_id GROUP BY u.user_id";
        List<Object[]> expectedResults = Arrays.asList(/* Mocked user data with average ratings */);
        Query queryMock = mock(Query.class);
        when(apiResponseConfig.getUsersTable()).thenReturn("users");
        when(apiResponseConfig.getRatingsReviewsTable()).thenReturn("ratings_reviews");
        when(apiResponseConfig.getUserIdColumn()).thenReturn("user_id");
        when(apiResponseConfig.getSellerIdColumn()).thenReturn("seller_id");
        when(entityManager.createNativeQuery(expectedSql)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(expectedResults);
        List<Object[]> actualResults = userRatingRepository.getAllUsersWithAverageRatings();
        assertNotNull(actualResults);
        assertEquals(expectedResults, actualResults);
    }
}
