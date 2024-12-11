package ru.mtuci.praktikaRBPO.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.praktikaRBPO.model.LicenseHistory;

public interface LicenseHistoryRepository extends JpaRepository<LicenseHistory, Long> {
}
