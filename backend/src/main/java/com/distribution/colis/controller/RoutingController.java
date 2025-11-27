package com.distribution.colis.controller;

import com.distribution.colis.model.dto.request.DeliveryRouteRequest;
import com.distribution.colis.model.dto.response.DeliveryRouteResponse;
import com.distribution.colis.model.entity.TaskStatus;
import com.distribution.colis.service.AuthService;
import com.distribution.colis.service.RoutingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RoutingController {

    private final RoutingService routingService;
    private final AuthService authService;

    public RoutingController(RoutingService routingService, AuthService authService) {
        this.routingService = routingService;
        this.authService = authService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<DeliveryRouteResponse> createRoute(@Valid @RequestBody DeliveryRouteRequest routeRequest) {
        DeliveryRouteResponse route = routingService.createRoute(routeRequest);
        return new ResponseEntity<>(route, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DeliveryRouteResponse>> getAllRoutes() {
        // Implémentation à développer selon le rôle de l'utilisateur
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DeliveryRouteResponse> getRouteById(@PathVariable Long id) {
        DeliveryRouteResponse route = routingService.getRouteById(id);
        return ResponseEntity.ok(route);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<DeliveryRouteResponse> updateRoute(@PathVariable Long id, @Valid @RequestBody DeliveryRouteRequest routeRequest) {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/{id}/optimize")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<DeliveryRouteResponse> optimizeRoute(@PathVariable Long id) {
        DeliveryRouteResponse route = routingService.optimizeRoute(id);
        return ResponseEntity.ok(route);
    }

    @PostMapping("/{id}/tasks")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<DeliveryRouteResponse> addTaskToRoute(
            @PathVariable Long id, 
            @RequestParam Long orderId, 
            @RequestParam Integer taskOrder) {
        DeliveryRouteResponse route = routingService.addTaskToRoute(id, orderId, taskOrder);
        return ResponseEntity.ok(route);
    }

    @PutMapping("/{routeId}/tasks/{taskId}")
    @PreAuthorize("hasAnyRole('LIVREUR', 'GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<DeliveryRouteResponse> updateTaskStatus(
            @PathVariable Long routeId,
            @PathVariable Long taskId,
            @RequestParam TaskStatus status,
            @RequestParam(required = false) String notes) {
        DeliveryRouteResponse route = routingService.updateTaskStatus(taskId, status, notes);
        return ResponseEntity.ok(route);
    }

    @GetMapping("/courier/{courierId}/date/{date}")
    @PreAuthorize("hasAnyRole('LIVREUR', 'GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<List<DeliveryRouteResponse>> getRoutesForCourier(
            @PathVariable Long courierId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<DeliveryRouteResponse> routes = routingService.getRoutesForCourier(courierId, date);
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<List<DeliveryRouteResponse>> getRoutesByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<DeliveryRouteResponse> routes = routingService.getRoutesByDate(date);
        return ResponseEntity.ok(routes);
    }
}
