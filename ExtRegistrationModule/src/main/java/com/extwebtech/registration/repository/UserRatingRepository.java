package com.extwebtech.registration.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.extwebtech.registration.configuration.ApiResponseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

/**
 * Repository class for handling database operations related to user ratings.
 */
@Repository
public class UserRatingRepository {

	@Autowired
	EntityManager entityManager;

	@Autowired
	private ApiResponseConfig apiResponseConfig;

	/**
	 * Retrieves user information by user ID.
	 *
	 * @param userId The ID of the user
	 * @return An array containing user information, or null if not found
	 */
	public Object[] getUserInfoByUserId(Integer userId) {
		try {
			String userInfoQuery = "SELECT u." + apiResponseConfig.getUserIdColumn() + ", " + "u."
					+ apiResponseConfig.getNameColumn() + ", " + "u." + apiResponseConfig.getMobileColumn() + ", "
					+ "u." + apiResponseConfig.getEmailColumn() + ", " + "u."
					+ apiResponseConfig.getProfilePhotoColumn() + ", " + "u."
					+ apiResponseConfig.getBusinessNameColumn() + ", " + "u." + apiResponseConfig.getOwnerNameColumn()
					+ ", " + "u." + apiResponseConfig.getGstColumn() + ", " + "u."
					+ apiResponseConfig.getAccountHolderNameColumn() + ", " + "u."
					+ apiResponseConfig.getAccountNameColumn() + ", " + "u." + apiResponseConfig.getIfscCodeColumn()
					+ ", " + "u." + apiResponseConfig.getBankUpiColumn() + ", " + "u."
					+ apiResponseConfig.getRoleIdColumn() + ", " + "u." + apiResponseConfig.getLanguageIdColumn() + ", "
					+ "u." + apiResponseConfig.getCreatedDateColumn() + ", " + "u."
					+ apiResponseConfig.getUpdatedDateColumn() + ", " + "u." + apiResponseConfig.getCountryIdColumn()
					+ ", " + "u." + apiResponseConfig.getBusinessAddressColumn() + ", " + "u."
					+ apiResponseConfig.getExtraFieldColumn() + ", " + "u."
					+ apiResponseConfig.getOfficialDocumentsColumn() + ", " + "u." + apiResponseConfig.getActiveColumn()
					+ ", " + "u." + apiResponseConfig.getSubscriptionIdColumn() + ", " + "u."
					+ apiResponseConfig.getSubscriptionStartDateColumn() + ", " + "u."
					+ apiResponseConfig.getSubscriptionDaysRemainingColumn() + " , u.referral_code " + " FROM "
					+ apiResponseConfig.getUsersTable() + " u " + "WHERE u." + apiResponseConfig.getUserIdColumn()
					+ " = :userId";

			return (Object[]) entityManager.createNativeQuery(userInfoQuery).setParameter("userId", userId)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Retrieves a list of users with their average ratings.
	 *
	 * @return A list of objects containing user information and average ratings
	 */
	public List<Object[]> getAllUsersWithAverageRatings() {
		String sql = "SELECT u.*, AVG(rr.ratings) AS average_rating " + "FROM " + apiResponseConfig.getUsersTable()
				+ " u " + "LEFT JOIN " + apiResponseConfig.getRatingsReviewsTable() + " rr " + "ON u."
				+ apiResponseConfig.getUserIdColumn() + " = rr." + apiResponseConfig.getSellerIdColumn() + " "
				+ "GROUP BY u." + apiResponseConfig.getUserIdColumn();

		Query query = entityManager.createNativeQuery(sql);

		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();

		return results;
	}

}
