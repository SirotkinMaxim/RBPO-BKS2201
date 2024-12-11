package ru.mtuci.praktikaRBPO.services;

import ru.mtuci.praktikaRBPO.model.License;
import ru.mtuci.praktikaRBPO.model.ApplicationUser;

public interface LicenseHistoryService {
    void recordLicenseChange(License license, ApplicationUser applicationUser, String status, String description);
}
