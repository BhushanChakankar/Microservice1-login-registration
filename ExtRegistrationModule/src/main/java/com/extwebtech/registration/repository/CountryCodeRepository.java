package com.extwebtech.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.extwebtech.registration.bean.CountryCode;
/**
 * Repository interface for accessing and managing CountryCode entities in the database.
 */
@Repository
public interface CountryCodeRepository extends JpaRepository<CountryCode, Integer> {
}