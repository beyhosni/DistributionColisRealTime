package com.distribution.colis.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @Size(max = 20)
    private String phone;

    @Size(max = 255)
    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 20)
    private String postalCode;

    @Size(max = 100)
    private String country;
}
