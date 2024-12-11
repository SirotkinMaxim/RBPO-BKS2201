package ru.mtuci.praktikaRBPO.controller;


import ru.mtuci.praktikaRBPO.dto.LicenseActivationRequest;
import ru.mtuci.praktikaRBPO.dto.LicenseRequest;
import ru.mtuci.praktikaRBPO.dto.UpdateLicenseRequest;
import ru.mtuci.praktikaRBPO.model.ApplicationUser;
import ru.mtuci.praktikaRBPO.model.License;
import ru.mtuci.praktikaRBPO.services.LicenseService;
import ru.mtuci.praktikaRBPO.services.UserService;
import ru.mtuci.praktikaRBPO.ticket.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;


@RequiredArgsConstructor
@RequestMapping("/licenses")
@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class LicenseController {
    private final LicenseService licenseService;
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestBody LicenseRequest licenseRequest) {
        try {
            License createdLicense = licenseService.createLicense(licenseRequest.getProductId(), licenseRequest.getOwnerId(), licenseRequest.getLicenseTypeId(), licenseRequest.getMaxDevice());
            return ResponseEntity.ok("Лицензия создана с ID: " + createdLicense.getId());
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка: " + e.getMessage());
        }
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateLicense(@RequestBody LicenseActivationRequest request) {
        try {
            ApplicationUser authenticatedApplicationUser = userService.getAuthenticatedUser();
            Ticket fullTicket = licenseService.activateLicense(request, authenticatedApplicationUser);
            return ResponseEntity.ok(fullTicket);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/renew")
    public ResponseEntity<?> renewLicense(@RequestBody UpdateLicenseRequest updateLicenseRequest) {
        try {
            ApplicationUser authenticatedApplicationUser = userService.getAuthenticatedUser();
            Ticket ticket = licenseService.renewLicense(updateLicenseRequest, authenticatedApplicationUser);
            return ResponseEntity.ok(ticket);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + ex.getMessage());
        }
    }



}




