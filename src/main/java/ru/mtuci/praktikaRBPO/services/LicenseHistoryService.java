package ru.mtuci.praktikaRBPO.services;

import ru.mtuci.praktikaRBPO.model.License;
import ru.mtuci.praktikaRBPO.model.ApplicationUser;
import ru.mtuci.praktikaRBPO.model.LicenseHistory;

import java.util.List;

public interface LicenseHistoryService {
    void recordLicenseChange(License license, ApplicationUser applicationUser, String status, String description);
    List<LicenseHistory> getAllHistory();
    void clearAllHistory();
}
