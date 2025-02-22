package ru.mtuci.praktikaRBPO.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.praktikaRBPO.services.LicenseService;

@RequiredArgsConstructor
@RequestMapping("/block")
@RestController
@PreAuthorize("hasAnyRole('ADMIN')")
public class BlockController {

    private final LicenseService licenseService;

    @PatchMapping("/changeStatus/{licenseId}")
    public ResponseEntity<String> changeLicenseStatus(@PathVariable Long licenseId, @RequestParam boolean isBlocked) {
        try {
            licenseService.changeLicenseStatus(licenseId, isBlocked);
            return ResponseEntity.ok("Статус обновлен.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Лицензия не найдена");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка: " + e.getMessage());
        }
    }

}
