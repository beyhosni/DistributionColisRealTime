package com.distribution.colis.controller;

import com.distribution.colis.model.dto.request.ParcelRequest;
import com.distribution.colis.model.dto.response.ParcelResponse;
import com.distribution.colis.service.AuthService;
import com.distribution.colis.service.ParcelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController {

    private final ParcelService parcelService;
    private final AuthService authService;

    public ParcelController(ParcelService parcelService, AuthService authService) {
        this.parcelService = parcelService;
        this.authService = authService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<ParcelResponse> createParcel(@Valid @RequestBody ParcelRequest parcelRequest) {
        Long currentUserId = authService.getCurrentUser().getId();
        ParcelResponse parcel = parcelService.createParcel(parcelRequest, currentUserId);
        return new ResponseEntity<>(parcel, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ParcelResponse>> getAllParcels() {
        List<ParcelResponse> parcels;
        String userRole = authService.getCurrentUser().getAuthorities().iterator().next().getAuthority();

        if (userRole.equals("ROLE_CLIENT")) {
            // Un client ne voit que ses propres colis
            Long currentUserId = authService.getCurrentUser().getId();
            parcels = parcelService.getParcelsForUser(currentUserId);
        } else {
            // Les autres rôles voient tous les colis
            parcels = parcelService.getAllParcels();
        }

        return ResponseEntity.ok(parcels);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ParcelResponse> getParcelById(@PathVariable Long id) {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<ParcelResponse> getParcelByTrackingNumber(@PathVariable String trackingNumber) {
        ParcelResponse parcel = parcelService.getParcelByTrackingNumber(trackingNumber);
        return ResponseEntity.ok(parcel);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<ParcelResponse> updateParcel(@PathVariable Long id, @Valid @RequestBody ParcelRequest parcelRequest) {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<Void> deleteParcel(@PathVariable Long id) {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
