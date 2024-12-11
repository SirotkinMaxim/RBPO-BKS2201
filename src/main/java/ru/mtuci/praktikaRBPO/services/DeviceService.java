package ru.mtuci.praktikaRBPO.services;

import ru.mtuci.praktikaRBPO.model.Device;
import ru.mtuci.praktikaRBPO.model.ApplicationUser;

import java.net.SocketException;

public interface DeviceService {
    void addDevice(String name, ApplicationUser applicationUser) throws SocketException;
    void deleteDevice(Long id);
    Long findDeviceIdByMac(String mac);
    Device getByMac(String mac);
    Device getDeviceById(Long id);
}

