package com.example.security_full.registration;


public record RegistrationRequest(
        String fistName,
        String lastName,
        String email,
        String password,
        String role
) {

}
