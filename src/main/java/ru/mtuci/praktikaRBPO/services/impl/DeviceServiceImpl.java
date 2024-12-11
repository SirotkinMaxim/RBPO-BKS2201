package ru.mtuci.praktikaRBPO.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.praktikaRBPO.model.Device;
import ru.mtuci.praktikaRBPO.model.ApplicationUser;
import ru.mtuci.praktikaRBPO.repository.DeviceLicenseRepository;
import ru.mtuci.praktikaRBPO.repository.DeviceRepository;
import ru.mtuci.praktikaRBPO.services.DeviceService;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceLicenseRepository deviceLicenseRepository;

    public void addDevice(String name, ApplicationUser applicationUser) throws SocketException {
        String mac = getMacAddress();
        Optional<Device> existingDevice = deviceRepository.findByMac(mac);
        if (existingDevice.isPresent()) {
            throw new IllegalArgumentException("Устройство с таким MAC-адресом уже существует");
        }

        Device device = new Device();
        device.setName(name);
        device.setMac(mac);
        device.setApplicationUser(applicationUser);
        deviceRepository.save(device);
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

    public static String getMacAddress() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            if (networkInterface == null || networkInterface.isLoopback() || networkInterface.getHardwareAddress() == null) {
                continue;
            }

            byte[] mac = networkInterface.getHardwareAddress();
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            return sb.toString();
        }
        throw new SocketException("Ошибка");
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