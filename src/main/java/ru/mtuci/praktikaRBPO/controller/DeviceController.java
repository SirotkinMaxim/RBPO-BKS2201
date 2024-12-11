package ru.mtuci.praktikaRBPO.controller;

import org.springframework.web.bind.annotation.*;
import ru.mtuci.praktikaRBPO.dto.DeviceAddRequest;
import ru.mtuci.praktikaRBPO.model.ApplicationUser;
import ru.mtuci.praktikaRBPO.model.Device;
import ru.mtuci.praktikaRBPO.services.DeviceService;
import ru.mtuci.praktikaRBPO.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.net.SocketException;

@RequiredArgsConstructor
@RequestMapping("/device")
@RestController
public class DeviceController {

    private final DeviceService deviceService;
    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<String> addDevice(@RequestBody DeviceAddRequest deviceAddRequest) {
        try {
            ApplicationUser applicationUser = userService.getAuthenticatedUser();
            deviceService.addDevice(deviceAddRequest.getName(), applicationUser);
            return ResponseEntity.ok("Устройство успешно добавлено");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/view/{id}")
    public ResponseEntity<?> viewDevice(@PathVariable Long id) {
        try {
            Device device = deviceService.getDeviceById(id);
            return ResponseEntity.ok(device);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDevice(@PathVariable Long id) {
        try {
            deviceService.deleteDevice(id);
            return ResponseEntity.ok("Устройство удалено");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/find")
    public ResponseEntity<?> findDeviceIdByMac(@RequestParam String mac) {
        try {
            Long deviceId = deviceService.findDeviceIdByMac(mac);
            return ResponseEntity.ok(deviceId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

}
