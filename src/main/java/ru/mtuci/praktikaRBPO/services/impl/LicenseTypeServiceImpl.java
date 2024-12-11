package ru.mtuci.praktikaRBPO.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.praktikaRBPO.model.LicenseType;
import ru.mtuci.praktikaRBPO.repository.LicenseTypeRepository;
import ru.mtuci.praktikaRBPO.services.LicenseTypeService;

import java.util.List;
import java.util.Optional;

@Service
public class LicenseTypeServiceImpl implements LicenseTypeService {

    @Autowired
    private LicenseTypeRepository licenseTypeRepository;

    public void deleteById(Long id) {
        licenseTypeRepository.deleteById(id);
    }

    @Override
    public LicenseType getLicenseTypeById(Long id) {
        return licenseTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Тип лицензии с ID " + id + " не найден"));
    }

    @Override
    public LicenseType addLicenseType(String name,Integer defaultDuration,String description) {
        if (licenseTypeRepository.existsByName(name)) {
            throw new IllegalArgumentException("Тип лицензии с таким именем уже существует");
        }
        LicenseType licenseType = new LicenseType();
        licenseType.setName(name);
        licenseType.setDefaultDuration(defaultDuration);
        licenseType.setDescription(description);

        return licenseTypeRepository.save(licenseType);
    }

    @Override
    public List<LicenseType> findAll(){
        return licenseTypeRepository.findAll();
    }

}
