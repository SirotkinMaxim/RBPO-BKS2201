package ru.mtuci.praktikaRBPO.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LoginRequest {
    private String email;
    private String password;
}
