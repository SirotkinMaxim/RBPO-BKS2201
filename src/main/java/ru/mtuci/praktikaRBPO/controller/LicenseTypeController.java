package ru.mtuci.praktikaRBPO.controller;

import ru.mtuci.praktikaRBPO.dto.LicenseTypeAddRequest;
import ru.mtuci.praktikaRBPO.model.LicenseType;
import ru.mtuci.praktikaRBPO.services.LicenseService;
import ru.mtuci.praktikaRBPO.services.LicenseTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@RestController
@RequestMapping("/license-type")
public class LicenseTypeController {

    private final LicenseTypeService licenseTypeService;
    private final LicenseService licenseService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<String> addLicenseType(@RequestBody LicenseTypeAddRequest licenseTypeAddRequest) {
        try {
            LicenseType createdLicenseType = licenseTypeService.addLicenseType(licenseTypeAddRequest.getName(),licenseTypeAddRequest.getDefaultDuration(),licenseTypeAddRequest.getDescription());
            return ResponseEntity.ok("Тип лицензии создан с ID: " + createdLicenseType.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeLicenseType(@PathVariable Long id) {
        try {
            if (licenseService.existsByLicenseTypeId(id)) {
                return ResponseEntity.badRequest().body("Невозможно удалить LicenseType.");
            }
            licenseTypeService.deleteById(id);
            return ResponseEntity.ok("LicenseType удалён.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/view")
    public ResponseEntity<List<LicenseType>> getAllLicenseTypes() {
        try {
            List<LicenseType> licenseTypes = licenseTypeService.findAll();
            if (licenseTypes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(licenseTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
