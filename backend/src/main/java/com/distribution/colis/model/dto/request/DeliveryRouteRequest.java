package com.distribution.colis.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DeliveryRouteRequest {

    @NotNull(message = "L'ID du livreur est obligatoire")
    private Long courierId;

    private Long zoneId; // Optionnel

    @NotNull(message = "La date de la tournée est obligatoire")
    private LocalDate routeDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String notes;
}
