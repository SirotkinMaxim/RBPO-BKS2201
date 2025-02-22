package ru.mtuci.praktikaRBPO.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.praktikaRBPO.model.LicenseHistory;
import ru.mtuci.praktikaRBPO.services.LicenseHistoryService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/history")
@RestController
@PreAuthorize("hasRole('ADMIN')")
public class LicenseHistoryController {

    private final LicenseHistoryService licenseHistoryService;

    @GetMapping
    public ResponseEntity<?> getAllLicenseHistory() {
        try {
            List<LicenseHistory> historyList = licenseHistoryService.getAllHistory();
            return ResponseEntity.ok(historyList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка при получении истории: " + e.getMessage());
        }
    }


    @DeleteMapping("/del")
    public ResponseEntity<?> clearLicenseHistory() {
        try {
            licenseHistoryService.clearAllHistory();
            return ResponseEntity.ok("Вся история лицензий успешно удалена.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка при очистке истории: " + e.getMessage());
        }
    }
}
