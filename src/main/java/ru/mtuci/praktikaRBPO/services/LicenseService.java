package ru.mtuci.praktikaRBPO.services;

import ru.mtuci.praktikaRBPO.dto.LicenseActivationRequest;
import ru.mtuci.praktikaRBPO.dto.UpdateLicenseRequest;
import ru.mtuci.praktikaRBPO.model.ApplicationUser;
import ru.mtuci.praktikaRBPO.model.License;
import ru.mtuci.praktikaRBPO.ticket.Ticket;

import java.net.SocketException;
import java.util.List;

public interface LicenseService {
    void add(License license);
    void changeLicenseStatus(Long licenseId, boolean isBlocked);
    boolean existsByProductId(Long id);
    boolean existsByLicenseTypeId(Long id);
    long countActiveDevicesForLicense(License license);
    Ticket activateLicense(LicenseActivationRequest request, ApplicationUser authenticatedApplicationUser) throws SocketException;
    Ticket renewLicense(UpdateLicenseRequest updateLicenseRequest, ApplicationUser authenticatedApplicationUser);
    Ticket getLicenseInfo(String mac, String licenseKey);
    License getByKey(String key);
    License createLicense(Long productId, Long ownerId, Long licenseTypeId, Integer maxDevice);
    void deleteLicense(Long licenseId);
}
