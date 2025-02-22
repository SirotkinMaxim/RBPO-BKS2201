package ru.mtuci.praktikaRBPO.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mtuci.praktikaRBPO.model.ApplicationRole;
import ru.mtuci.praktikaRBPO.model.ApplicationUser;
import ru.mtuci.praktikaRBPO.repository.UserRepository;
import ru.mtuci.praktikaRBPO.services.UserService;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void create(String email, String name, String password)  {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Пользователь с таким email уже существует: " + email);
        }

        var user = new ApplicationUser();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(ApplicationRole.USER);
        userRepository.save(user);
    }

    @Override
    public ApplicationUser getById(Long id) {
        return userRepository.findById(id).orElse(new ApplicationUser());
    }

    @Override
    public Optional<ApplicationUser> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public void renameUserByEmail(String email, String newName, ApplicationUser authenticatedUser) {
        if (!authenticatedUser.getEmail().equals(email)) {
            throw new IllegalArgumentException("Вы можете переименовать только свой аккаунт");
        }
        ApplicationUser applicationUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким email не найден"));

        applicationUser.setName(newName);
        userRepository.save(applicationUser);
    }

    public ApplicationUser getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = (String) authentication.getPrincipal();
            return findByEmail(username).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        }
        return null;
    }
}
