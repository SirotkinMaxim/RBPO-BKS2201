package ru.mtuci.praktikaRBPO.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.praktikaRBPO.model.License;

import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
    void delete(License license);
    boolean existsByKey(String key);
    boolean existsByProductId(Long productId);
    boolean existsByLicenseTypeId(Long licenseTypeId);
    boolean existsByApplicationUserId(Long userId);
    Optional<License> findByKey(String key);
}
