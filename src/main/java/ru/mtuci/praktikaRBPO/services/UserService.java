package ru.mtuci.praktikaRBPO.services;


import ru.mtuci.praktikaRBPO.model.ApplicationUser;

import java.util.Optional;


public interface UserService {
    void create(String email, String name, String password) ;
    Optional<ApplicationUser> findByEmail(String email);
    ApplicationUser getAuthenticatedUser();
    ApplicationUser getById(Long id);
    void renameUserByEmail(String email, String newName, ApplicationUser authenticatedUser);
}
