package com.distribution.colis.controller;

import com.distribution.colis.model.dto.request.ShipmentOrderRequest;
import com.distribution.colis.model.dto.response.ShipmentOrderResponse;
import com.distribution.colis.model.entity.OrderStatus;
import com.distribution.colis.service.AuthService;
import com.distribution.colis.service.ShipmentOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class ShipmentOrderController {

    private final ShipmentOrderService shipmentOrderService;
    private final AuthService authService;

    public ShipmentOrderController(ShipmentOrderService shipmentOrderService, AuthService authService) {
        this.shipmentOrderService = shipmentOrderService;
        this.authService = authService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<ShipmentOrderResponse> createOrder(@Valid @RequestBody ShipmentOrderRequest orderRequest) {
        Long currentUserId = authService.getCurrentUser().getId();
        ShipmentOrderResponse order = shipmentOrderService.createOrder(orderRequest, currentUserId);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ShipmentOrderResponse>> getAllOrders() {
        List<ShipmentOrderResponse> orders;
        String userRole = authService.getCurrentUser().getAuthorities().iterator().next().getAuthority();

        if (userRole.equals("ROLE_CLIENT")) {
            // Un client ne voit que ses propres commandes
            Long currentUserId = authService.getCurrentUser().getId();
            orders = shipmentOrderService.getOrdersBySender(currentUserId);
        } else {
            // Les autres rôles voient toutes les commandes
            orders = shipmentOrderService.getOrdersByStatus(null); // null pour toutes les commandes
        }

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ShipmentOrderResponse> getOrderById(@PathVariable Long id) {
        ShipmentOrderResponse order = shipmentOrderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<ShipmentOrderResponse> updateOrder(@PathVariable Long id, @Valid @RequestBody ShipmentOrderRequest orderRequest) {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ShipmentOrderResponse> payOrder(@PathVariable Long id) {
        // Implémentation du paiement à développer
        // Mettre à jour le statut de la commande à PAYEE
        ShipmentOrderResponse order = shipmentOrderService.updateOrderStatus(id, OrderStatus.PAYEE);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getOrderStatus(@PathVariable Long id) {
        ShipmentOrderResponse order = shipmentOrderService.getOrderById(id);
        return ResponseEntity.ok(order.getStatus().toString());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('LIVREUR', 'GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<ShipmentOrderResponse> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        ShipmentOrderResponse order = shipmentOrderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(order);
    }
}
