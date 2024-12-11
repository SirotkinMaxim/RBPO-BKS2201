package ru.mtuci.praktikaRBPO.services.impl;

import ru.mtuci.praktikaRBPO.model.*;
import ru.mtuci.praktikaRBPO.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.praktikaRBPO.dto.LicenseActivationRequest;
import ru.mtuci.praktikaRBPO.dto.UpdateLicenseRequest;
import ru.mtuci.praktikaRBPO.repository.DeviceLicenseRepository;
import ru.mtuci.praktikaRBPO.repository.LicenseRepository;
import ru.mtuci.praktikaRBPO.services.*;
import ru.mtuci.praktikaRBPO.ticket.Ticket;
import ru.mtuci.praktikaRBPO.ticket.TicketService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

//TODO: 1. Как пользователю активировать лицензию на новом устройстве, если вы кидаете исключение?
//TODO: 2. Ошибки в подсчетах дат
//TODO: 3. Возвращать информацию нужно о текущей лицензии (один тикет), а не список тикетов

@RequiredArgsConstructor
@Service
public class LicenseServiceImpl implements LicenseService {

    private final LicenseRepository licenseRepository;
    private final ProductService productService;
    private final UserService userService;
    private final DeviceService deviceService;
    private final LicenseTypeService licenseTypeService;
    private final LicenseHistoryService licenseHistoryService;
    private final DeviceLicenseRepository deviceLicenseRepository;
    private final DeviceRepository deviceRepository;
    private final TicketService ticketService;

    @Override
    public License createLicense(Long productId, Long ownerId, Long licenseTypeId, Integer maxDevice) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Такого продукта не существует");
        }
        ApplicationUser applicationUser = userService.getById(ownerId);
        if (applicationUser == null) {
            throw new NoSuchElementException("Пользователя не существует");
        }
        LicenseType licenseType = licenseTypeService.getLicenseTypeById(licenseTypeId);
        if (licenseType == null) {
            throw new NoSuchElementException("Такого типа лицензии не существует");
        }
        License license = new License();
        license.setProduct(product);
        license.setOwner(applicationUser);
        license.setLicenseType(licenseType);
        license.setMaxDevices(maxDevice);
        String activationCode;
        do {
            activationCode = UUID.randomUUID().toString();
        } while (licenseRepository.existsByKey(activationCode));
        license.setKey(activationCode);
        licenseRepository.save(license);
        licenseHistoryService.recordLicenseChange(license, applicationUser, "Создание", "Лицензия успешно создана и готова к активации");
        return license;
    }

    @Override
    public License getByKey(String key) {
        return licenseRepository.findByKey(key).orElse(null);
    }

    public long countActiveDevicesForLicense(License license) {
        return deviceLicenseRepository.countByLicenseAndActivationDateIsNotNull(license);
    }

    @Override
    public Ticket activateLicense(LicenseActivationRequest request, ApplicationUser authenticatedApplicationUser) {
        validateLicenseActivation(request, authenticatedApplicationUser);

        Device device = deviceRepository.findById(request.getDeviceId()).get();
        License license = getByKey(request.getKey());

        DeviceLicense deviceLicense = new DeviceLicense();
        deviceLicense.setLicense(license);
        deviceLicense.setDevice(device);
        deviceLicense.setActivationDate(new Date());
        deviceLicenseRepository.save(deviceLicense);

        int defaultDuration = license.getLicenseType().getDefaultDuration();
        LocalDate expirationDate = LocalDate.now().plusMonths(defaultDuration);
        Date newExpiration = Date.from(expirationDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (license.getExpirationDate() == null) {
            license.setExpirationDate(newExpiration);
        }
        license.setActivationDate(new Date());
        license.setBlocked(false);

        licenseRepository.save(license);

        licenseHistoryService.recordLicenseChange(license, authenticatedApplicationUser, "Активация", "Лицензия успешно активирована");
        return ticketService.createTicket(license,device);
    }


    private void validateLicenseActivation(LicenseActivationRequest request, ApplicationUser authenticatedApplicationUser) {
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new IllegalArgumentException("Устройство не найдено"));

        License license = getByKey(request.getKey());
        if (license == null) {
            throw new IllegalArgumentException("Такого ключа не существует");
        }

        long activeDeviceCount = countActiveDevicesForLicense(license);
        if (activeDeviceCount >= license.getMaxDevices()) {
            throw new IllegalArgumentException("Слишком много устройств");
        }

        if (license.getApplicationUser() == null) {
            license.setApplicationUser(authenticatedApplicationUser);
        }

        if (!license.getApplicationUser().getId().equals(authenticatedApplicationUser.getId())) {
            throw new IllegalArgumentException("Лицензия не принадлежит текущему пользователю");
        }

        boolean isAlreadyLinked = deviceLicenseRepository
                .findByLicenseIdAndDeviceId(license.getId(), device.getId())
                .isPresent();
        if (isAlreadyLinked) {
            throw new IllegalArgumentException("Уже активировано");
        }
    }

    @Override
    public void add(License license) {
        licenseRepository.save(license);
    }

    public Ticket renewLicense(UpdateLicenseRequest updateLicenseRequest, ApplicationUser authenticatedApplicationUser) {
        License license = getByKey(updateLicenseRequest.getKey());
        if (license == null) {
            throw new IllegalArgumentException("Лицензия не найдена");
        }
        ApplicationUser licenseOwner = license.getApplicationUser();
        if (licenseOwner == null){
            throw new IllegalArgumentException("Сначала активируйте лицензию");
        }
        if (!licenseOwner.getEmail().equals(authenticatedApplicationUser.getEmail())) {
            throw new IllegalArgumentException("Это не ваша лицензия.");
        }

        Device device = deviceRepository.findByMac(updateLicenseRequest.getMac())
                .orElseThrow(() -> new IllegalArgumentException("Устройство не найдено."));

        boolean isLinked = deviceLicenseRepository.existsByLicenseIdAndDeviceId(license.getId(), device.getId());
        if (!isLinked) {
            throw new IllegalArgumentException("Устройство не связано с данной лицензией.");
        }

        Date newExpirationDate = Date.from(
                (license.getExpirationDate() != null
                        ? Instant.ofEpochMilli(license.getExpirationDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate()
                        : LocalDate.now()
                ).plusMonths(license.getLicenseType().getDefaultDuration())
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );

        license.setExpirationDate(newExpirationDate);
        licenseRepository.save(license);
        licenseHistoryService.recordLicenseChange(
                license, licenseOwner, "Продление", "Новая дата: " + newExpirationDate
        );
        return ticketService.createTicket(license,device);

    }

    public List<Ticket> getLicenseInfo(String mac) {
        Device device = deviceService.getByMac(mac);
        if (device == null) {
            throw new IllegalArgumentException("Устройство не найдено");
        }

        return deviceLicenseRepository.findByDeviceId(device.getId()).stream()
                .map(DeviceLicense::getLicense)
                .filter(Objects::nonNull)
                .filter(license -> license.getExpirationDate() == null || !license.getExpirationDate().before(new Date()))
                .map(license -> ticketService.createTicket(license,device))
                .collect(Collectors.toList());
    }

    public void changeLicenseStatus(Long licenseId, boolean isBlocked) {
        License license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new IllegalArgumentException("Лицензия не найдена"));

        if (license.getBlocked() != isBlocked) {
            license.setBlocked(isBlocked);
            licenseRepository.save(license);
        }
    }


    @Override
    public boolean existsByLicenseTypeId(Long licenseTypeId) {
        return licenseRepository.existsByLicenseTypeId(licenseTypeId);
    }
    @Override
    public boolean existsByProductId(Long productId) {
        return licenseRepository.existsByProductId(productId);
    }



}
