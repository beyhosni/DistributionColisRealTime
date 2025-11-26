package com.distribution.colis.dto;

public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        String phone,
        String address) {
}
