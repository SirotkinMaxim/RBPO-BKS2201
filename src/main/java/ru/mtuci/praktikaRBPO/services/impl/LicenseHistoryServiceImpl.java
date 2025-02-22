package ru.mtuci.praktikaRBPO.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.praktikaRBPO.model.ApplicationUser;
import ru.mtuci.praktikaRBPO.model.License;
import ru.mtuci.praktikaRBPO.model.LicenseHistory;
import ru.mtuci.praktikaRBPO.repository.LicenseHistoryRepository;
import ru.mtuci.praktikaRBPO.services.LicenseHistoryService;

import java.util.Date;
import java.util.List;
//TODO: 1. Для каждой сущности должны быть реализованы все CRUD операции
//Добавил Update для Device,LicenseType,Product
//Добавил контроллер для истории - Можно смотреть историю, и удалять историю. Обновлять историю вручную нелогично, Create уже был сделан
//Для пользователя добавил обновление имени, зная почту. Read пароля, или почты - нелогично
//Добавил Delete Лицензии, если она еще не была активирована
@RequiredArgsConstructor
@Service
public class LicenseHistoryServiceImpl implements LicenseHistoryService {

    private final LicenseHistoryRepository licenseHistoryRepository;

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

    public List<LicenseHistory> getAllHistory() {
        return licenseHistoryRepository.findAll();
    }

    public void clearAllHistory() {
        licenseHistoryRepository.deleteAll();
    }
}
