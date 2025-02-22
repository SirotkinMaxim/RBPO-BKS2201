package ru.mtuci.praktikaRBPO.services;

import ru.mtuci.praktikaRBPO.model.Device;
import ru.mtuci.praktikaRBPO.model.ApplicationUser;
import ru.mtuci.praktikaRBPO.model.License;

import java.net.SocketException;

public interface DeviceService {
    Device addDevice(String name, ApplicationUser applicationUser, License license) throws SocketException;
    void deleteDevice(Long id);
    Long findDeviceIdByMac(String mac);
    Device getByMac(String mac);
    Device getDeviceById(Long id);
    Device renameDeviceByMac(String mac, String newName);
}

