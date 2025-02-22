package ru.mtuci.praktikaRBPO.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.praktikaRBPO.model.Device;
import ru.mtuci.praktikaRBPO.model.ApplicationUser;
import ru.mtuci.praktikaRBPO.model.License;
import ru.mtuci.praktikaRBPO.repository.DeviceLicenseRepository;
import ru.mtuci.praktikaRBPO.repository.DeviceRepository;
import ru.mtuci.praktikaRBPO.services.DeviceService;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Optional;
import java.util.Random;

//TODO: 1. Чей mac-адрес вы получаете?

@RequiredArgsConstructor
@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceLicenseRepository deviceLicenseRepository;

    public Device addDevice(String name, ApplicationUser applicationUser, License license) throws SocketException {
        String mac = getMacAddress();
        Optional<Device> existingDevice = deviceRepository.findByMac(mac);

        if (existingDevice.isPresent()) {
            Device device = existingDevice.get();
            boolean hasActiveLicense = deviceLicenseRepository.existsByDeviceAndLicense(device, license);
            if (hasActiveLicense) {
                throw new IllegalStateException("Устройство с таким MAC-адресом уже связано с данной лицензией.");
            }
            return device;
        }

        Device device = new Device();
        device.setName(name);
        device.setMac(mac);
        device.setApplicationUser(applicationUser);
        deviceRepository.save(device);
        return device;
    }

    public Device renameDeviceByMac(String mac, String newName) {
        Optional<Device> existingDevice = deviceRepository.findByMac(mac);
        if (existingDevice.isEmpty()) {
            throw new IllegalArgumentException("Устройство не найдено");
        }

        Device device = existingDevice.get();
        device.setName(newName);
        deviceRepository.save(device);
        return device;
    }


    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Устройство не найдено"));
    }

    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Устройство не найдено"));

        boolean isLinkedToLicense = deviceLicenseRepository.existsByDeviceId(id);
        if (isLinkedToLicense) {
            throw new IllegalArgumentException("Устройство нельзя удалить");
        }
        deviceRepository.delete(device);
    }

    public static String getMacAddress() {
        Random random = new Random();
        byte[] mac = new byte[6];

        random.nextBytes(mac);

        mac[0] = (byte) (mac[0] & (byte) 0xFE);
        mac[0] = (byte) (mac[0] | (byte) 0x02);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }

        return sb.toString();
    }

    public Device getByMac(String mac) {
        return deviceRepository.findByMac(mac)
                .orElseThrow(() -> new EntityNotFoundException("Устройство не найдено"));
    }

    public Long findDeviceIdByMac(String mac) {
        return deviceRepository.findByMac(mac)
                .map(Device::getId)
                .orElseThrow(() -> new IllegalArgumentException("Устройство не найдено"));
    }

}