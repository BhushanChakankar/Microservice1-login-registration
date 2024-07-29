//package com.extwebtech.repository;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import com.extwebtech.registration.bean.CountryCode;
//import com.extwebtech.registration.repository.CountryCodeRepository;
//
//@DataJpaTest
//class CountryCodeRepositoryTest {
//
//	@Autowired
//	private CountryCodeRepository countryCodeRepository;
//
//	@Test
//	void testFindAll() {
//		CountryCode countryCode1 = createCountryCode(1, "US", "USA");
//		CountryCode countryCode2 = createCountryCode(2, "CA", "Canada");
//		countryCodeRepository.save(countryCode1);
//		countryCodeRepository.save(countryCode2);
//		List<CountryCode> countryCodes = countryCodeRepository.findAll();
//		assertEquals(2, countryCodes.size());
//		assertTrue(countryCodes.contains(countryCode1));
//		assertTrue(countryCodes.contains(countryCode2));
//	}
//
//	@Test
//	void testFindById() {
//		CountryCode countryCode = createCountryCode(1, "US", "USA");
//		countryCodeRepository.save(countryCode);
//		Optional<CountryCode> foundCountryCode = countryCodeRepository.findById(1);
//		assertTrue(foundCountryCode.isPresent());
//		assertEquals(countryCode, foundCountryCode.get());
//	}
//
//	@Test
//	void testSave() {
//		CountryCode countryCode = createCountryCode(1, "US", "USA");
//		CountryCode savedCountryCode = countryCodeRepository.save(countryCode);
//		assertNotNull(savedCountryCode.getCountryId());
//		assertEquals(countryCode.getCountryCode(), savedCountryCode.getCountryCode());
//		assertEquals(countryCode.getCountryName(), savedCountryCode.getCountryName());
//	}
//
//	@Test
//	void testDeleteById() {
//		CountryCode countryCode = createCountryCode(1, "US", "USA");
//		countryCodeRepository.save(countryCode);
//		countryCodeRepository.deleteById(1);
//		assertFalse(countryCodeRepository.findById(1).isPresent());
//	}
//
//	private CountryCode createCountryCode(Integer countryId, String countryCode, String countryName) {
//		CountryCode country = new CountryCode();
//		country.setCountryId(countryId);
//		country.setCountryCode(countryCode);
//		country.setCountryName(countryName);
//		return country;
//	}
//}
