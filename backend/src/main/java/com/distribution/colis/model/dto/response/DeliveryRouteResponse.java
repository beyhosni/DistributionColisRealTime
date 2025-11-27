package com.distribution.colis.model.dto.response;

import com.distribution.colis.model.entity.RouteStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class DeliveryRouteResponse {

    private Long id;

    private Long courierId;

    private String courierName;

    private Long zoneId;

    private String zoneName;

    private LocalDate routeDate;

    private RouteStatus status;

    private LocalTime startTime;

    private LocalTime endTime;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<Object> tasks; // Liste des tâches de livraison
}
