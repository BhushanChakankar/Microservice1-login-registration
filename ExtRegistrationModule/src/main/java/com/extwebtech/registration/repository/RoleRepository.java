package com.extwebtech.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.extwebtech.registration.bean.Role;

/**
 * Repository interface for handling database operations related to Role
 * entities.
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {
	/**
	 * Retrieves the permissions associated with a role by role ID.
	 *
	 * @param roleId The role ID
	 * @return Permissions as a JSON string associated with the role ID
	 */
	@Query(value = "SELECT permissions ->> 'TC' FROM roles WHERE id = :roleId", nativeQuery = true)
	String getPermissions(@Param("roleId") Integer roleId);
}