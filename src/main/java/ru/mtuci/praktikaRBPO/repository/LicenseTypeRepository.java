package ru.mtuci.praktikaRBPO.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.praktikaRBPO.model.LicenseType;

import java.util.Optional;

@Repository
public interface LicenseTypeRepository extends JpaRepository<LicenseType, Long> {
    boolean existsByName(String name);
    Optional<LicenseType> findById(Long id);
}
