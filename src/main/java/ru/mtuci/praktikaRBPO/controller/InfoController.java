package ru.mtuci.praktikaRBPO.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.praktikaRBPO.dto.InfoRequest;
import ru.mtuci.praktikaRBPO.services.LicenseService;

@RequiredArgsConstructor
@RequestMapping("/info")
@RestController
public class InfoController {

    private final LicenseService licenseService;

    @GetMapping("/license")
    public ResponseEntity<?> getLicenseInfo(@RequestBody InfoRequest infoRequest) {
        try {
            return ResponseEntity.ok(licenseService.getLicenseInfo(infoRequest.getMac(),infoRequest.getKey()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }
}
