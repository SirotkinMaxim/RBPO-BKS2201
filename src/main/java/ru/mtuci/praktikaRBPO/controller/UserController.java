package ru.mtuci.praktikaRBPO.controller;

import org.springframework.web.bind.annotation.*;
import ru.mtuci.praktikaRBPO.model.ApplicationUser;
import ru.mtuci.praktikaRBPO.repository.LicenseRepository;
import ru.mtuci.praktikaRBPO.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.mtuci.praktikaRBPO.services.UserService;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@PreAuthorize("hasAnyRole('ADMIN','USER')")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final LicenseRepository licenseRepository;

    @PreAuthorize("hasRole('ADMIN')")
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
    @PatchMapping("/rename/{email}")
    public ResponseEntity<?> renameUserByEmail(
            @PathVariable String email,
            @RequestParam String newName) {
        try {
            ApplicationUser authenticatedUser = userService.getAuthenticatedUser();
            userService.renameUserByEmail(email, newName, authenticatedUser);
            return ResponseEntity.ok("Имя пользователя обновлено на: " + newName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }


}
