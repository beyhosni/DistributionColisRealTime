package com.distribution.colis.controller;

import com.distribution.colis.model.entity.*;
import com.distribution.colis.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DeliveryZoneRepository deliveryZoneRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    public AdminController(UserRepository userRepository, 
                          RoleRepository roleRepository,
                          DeliveryZoneRepository deliveryZoneRepository,
                          PricingRuleRepository pricingRuleRepository,
                          NotificationPreferenceRepository notificationPreferenceRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.deliveryZoneRepository = deliveryZoneRepository;
        this.pricingRuleRepository = pricingRuleRepository;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
    }

    // Gestion des utilisateurs
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Implémentation à développer avec validation et hashage du mot de passe
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Gestion des zones de livraison
    @GetMapping("/zones")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<List<DeliveryZone>> getAllDeliveryZones() {
        List<DeliveryZone> zones = deliveryZoneRepository.findAll();
        return ResponseEntity.ok(zones);
    }

    @PostMapping("/zones")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<DeliveryZone> createDeliveryZone(@RequestBody DeliveryZone zone) {
        DeliveryZone savedZone = deliveryZoneRepository.save(zone);
        return new ResponseEntity<>(savedZone, HttpStatus.CREATED);
    }

    @GetMapping("/zones/{id}")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<DeliveryZone> getDeliveryZoneById(@PathVariable Long id) {
        return deliveryZoneRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/zones/{id}")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<DeliveryZone> updateDeliveryZone(@PathVariable Long id, @RequestBody DeliveryZone zone) {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/zones/{id}")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<Void> deleteDeliveryZone(@PathVariable Long id) {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Gestion des tarifs
    @GetMapping("/pricing")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<List<PricingRule>> getAllPricingRules() {
        List<PricingRule> rules = pricingRuleRepository.findAll();
        return ResponseEntity.ok(rules);
    }

    @PostMapping("/pricing")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<PricingRule> createPricingRule(@RequestBody PricingRule rule) {
        PricingRule savedRule = pricingRuleRepository.save(rule);
        return new ResponseEntity<>(savedRule, HttpStatus.CREATED);
    }

    @GetMapping("/pricing/{id}")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<PricingRule> getPricingRuleById(@PathVariable Long id) {
        return pricingRuleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/pricing/{id}")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<PricingRule> updatePricingRule(@PathVariable Long id, @RequestBody PricingRule rule) {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/pricing/{id}")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<Void> deletePricingRule(@PathVariable Long id) {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Rapports
    @GetMapping("/reports/delivery")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<Object> getDeliveryReports() {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/reports/incidents")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<Object> getIncidentReports() {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/reports/performance")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<Object> getPerformanceReports() {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
