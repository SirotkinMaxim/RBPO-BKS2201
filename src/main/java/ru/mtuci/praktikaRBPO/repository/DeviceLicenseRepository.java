package ru.mtuci.praktikaRBPO.repository;

import ru.mtuci.praktikaRBPO.model.Device;
import ru.mtuci.praktikaRBPO.model.DeviceLicense;
import ru.mtuci.praktikaRBPO.model.License;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface DeviceLicenseRepository extends JpaRepository<DeviceLicense, Long> {
    boolean existsByDeviceId(Long deviceId);
    boolean existsByLicenseIdAndDeviceId(Long licenseId, Long deviceId);
    long countByLicenseAndActivationDateIsNotNull(License license);
    List<DeviceLicense> findByDeviceId(Long deviceId);
    Optional<DeviceLicense> findByLicenseIdAndDeviceId(Long licenseId, Long deviceId);
    boolean existsByDeviceAndLicense(Device device, License license);
}
