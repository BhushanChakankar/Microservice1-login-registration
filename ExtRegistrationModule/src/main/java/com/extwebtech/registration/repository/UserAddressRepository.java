package com.extwebtech.registration.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.extwebtech.registration.bean.UserAddress;

/**
 * Repository interface for handling database operations related to UserAddress
 * entities.
 */
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Integer> {
	/**
	 * Saves a UserAddress entity.
	 *
	 * @param userAddress The UserAddress entity to be saved
	 * @return The saved UserAddress entity
	 */
	UserAddress save(UserAddress userAddress);

	/**
	 * Retrieves a list of UserAddress entities by user ID.
	 *
	 * @param userId The ID of the user
	 * @return A list of UserAddress entities associated with the specified user ID
	 */
	List<UserAddress> findByUserId(Integer userId);
	

	/**
	 * Retrieves an optional UserAddress entity by user ID and address ID.
	 *
	 * @param userId    The ID of the user
	 * @param addressId The ID of the address
	 * @return An optional containing the UserAddress entity, or an empty optional
	 *         if not found
	 */
	Optional<UserAddress> findByUserIdAndAddressId(int userId, Integer addressId);

	/**
	 * Retrieves the default UserAddress entity by user ID.
	 *
	 * @param userId The ID of the user
	 * @return An optional containing the default UserAddress entity, or an empty
	 *         optional if not found
	 */
	Optional<UserAddress> findDefaultByUserId(int userId);

//	List<UserAddress> findByUserIdOrderByDefaultAddressDesc(int userId);

	List<UserAddress> findByUserIdOrderByIsDefaultAddressDesc(int userId);
}