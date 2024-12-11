package ru.mtuci.praktikaRBPO.controller;

import ru.mtuci.praktikaRBPO.model.ApplicationUser;
import ru.mtuci.praktikaRBPO.repository.LicenseRepository;
import ru.mtuci.praktikaRBPO.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserRepository userRepository;
    private final LicenseRepository licenseRepository;

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> removeUser(@PathVariable Long userId) {
        try {
            Optional<ApplicationUser> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
            }
            ApplicationUser applicationUser = userOptional.get();
            boolean hasLicenses = licenseRepository.existsByApplicationUserId(userId);
            if (hasLicenses) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Нельзя удалить пользователя.");
            }
            userRepository.delete(applicationUser);
            return ResponseEntity.ok("Пользователь удален");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }
}
