package ru.mtuci.praktikaRBPO.repository;

import ru.mtuci.praktikaRBPO.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByMac(String mac);

}
