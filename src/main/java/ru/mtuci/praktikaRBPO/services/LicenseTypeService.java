package ru.mtuci.praktikaRBPO.services;

import ru.mtuci.praktikaRBPO.model.LicenseType;

import java.util.List;
import java.util.Optional;

public interface LicenseTypeService {
    void deleteById(Long id);
    List<LicenseType> findAll();
    LicenseType getLicenseTypeById(Long id);
    LicenseType addLicenseType(String name,Integer defaultDuration,String description);
}
