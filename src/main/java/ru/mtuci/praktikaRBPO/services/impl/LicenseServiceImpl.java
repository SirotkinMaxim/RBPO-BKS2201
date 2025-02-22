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

import java.net.SocketException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
//TODO: 1. Как пользователю активировать лицензию на новом устройстве, если вы кидаете исключение? - Была мысль, чтобы пользователь сначала создавал устройство (DeviceController), а потом уже активировал лицензию
//переделаю чтобы устройство создавалось при активации, но тогда смысла выносить создание девайса в отдельный контроллер нет
//TODO: 2. Ошибки в подсчетах дат - Увидел ошибку в часовых поясах, теперь использую Calendar.Так же заметил что в тикете выводилось неправильно
//теперь в Тикете Date перевожу в String, и время выводится верно
//TODO: 3. Возвращать информацию нужно о текущей лицензии (один тикет), а не список тикетов - Так как на один МАК может быть несколько разных лицензий, то
//то добавлю, чтобы метод принимал еще и ключ лицензии. И тогда выводиться будет только один тикет
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
    public Ticket activateLicense(LicenseActivationRequest request, ApplicationUser authenticatedApplicationUser) throws SocketException {
        validateLicenseActivation(request, authenticatedApplicationUser);

        License license = getByKey(request.getKey());

        Device device = deviceService.addDevice(request.getDeviceName(), authenticatedApplicationUser, license);

        DeviceLicense deviceLicense = new DeviceLicense();
        deviceLicense.setLicense(license);
        deviceLicense.setDevice(device);
        deviceLicense.setActivationDate(new Date());
        deviceLicenseRepository.save(deviceLicense);



        if (license.getActivationDate() == null) {
            license.setActivationDate(new Date());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(license.getActivationDate());
            calendar.add(Calendar.MONTH, (int) license.getLicenseType().getDefaultDuration());
            Date expirationDate = calendar.getTime();

            license.setExpirationDate(expirationDate);
        }
        license.setBlocked(false);

        licenseRepository.save(license);

        licenseHistoryService.recordLicenseChange(license, authenticatedApplicationUser, "Активация", "Лицензия успешно активирована");

        return ticketService.createTicket(license, device);
    }


    private void validateLicenseActivation(LicenseActivationRequest request, ApplicationUser authenticatedApplicationUser) {
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

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(license.getExpirationDate());
        calendar.add(Calendar.MONTH, (int) license.getLicenseType().getDefaultDuration());
        Date newExpirationDate = calendar.getTime();

        license.setExpirationDate(newExpirationDate);
        licenseRepository.save(license);
        licenseHistoryService.recordLicenseChange(
                license, licenseOwner, "Продление", "Новая дата: " + newExpirationDate
        );
        return ticketService.createTicket(license,device);

    }

    public Ticket getLicenseInfo(String mac, String licenseKey) {

        Device device = deviceService.getByMac(mac);
        if (device == null) {
            throw new IllegalArgumentException("Устройство не найдено");
        }

        DeviceLicense deviceLicense = deviceLicenseRepository.findByDeviceId(device.getId()).stream()
                .filter(Objects::nonNull)
                .filter(dl -> dl.getLicense().getKey().equals(licenseKey))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Лицензия не найдена или истекла"));

        return ticketService.createTicket(deviceLicense.getLicense(), device);
    }



    public void changeLicenseStatus(Long licenseId, boolean isBlocked) {
        License license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new IllegalArgumentException("Лицензия не найдена"));

        if (license.getBlocked() != isBlocked) {
            license.setBlocked(isBlocked);
            licenseRepository.save(license);
        }
    }

    public void deleteLicense(Long licenseId) {
        License license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new IllegalArgumentException("Лицензия не найдена"));

        if (license.getActivationDate() == null) {
            licenseRepository.delete(license);
        } else {
            throw new IllegalArgumentException("Невозможно удалить лицензию, она уже активирована.");
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
