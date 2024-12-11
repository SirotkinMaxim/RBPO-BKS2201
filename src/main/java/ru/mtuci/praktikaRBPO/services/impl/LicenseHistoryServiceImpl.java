package ru.mtuci.praktikaRBPO.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.praktikaRBPO.model.ApplicationUser;
import ru.mtuci.praktikaRBPO.model.License;
import ru.mtuci.praktikaRBPO.model.LicenseHistory;
import ru.mtuci.praktikaRBPO.repository.LicenseHistoryRepository;
import ru.mtuci.praktikaRBPO.services.LicenseHistoryService;

import java.util.Date;


@Service
public class LicenseHistoryServiceImpl implements LicenseHistoryService {

    @Autowired
    private LicenseHistoryRepository licenseHistoryRepository;

    @Override
    public void recordLicenseChange(License license, ApplicationUser applicationUser, String status, String description) {
        LicenseHistory history = new LicenseHistory();
        history.setLicense(license);
        history.setApplicationUser(applicationUser);
        history.setStatus(status);
        history.setDescription(description);
        history.setChangeDate(new Date());

        licenseHistoryRepository.save(history);
    }
}
