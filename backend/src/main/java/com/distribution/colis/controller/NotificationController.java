package com.distribution.colis.controller;

import com.distribution.colis.model.entity.Notification;
import com.distribution.colis.model.entity.NotificationPreference;
import com.distribution.colis.service.AuthService;
import com.distribution.colis.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;

    public NotificationController(NotificationService notificationService, AuthService authService) {
        this.notificationService = notificationService;
        this.authService = authService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notification>> getNotificationsForUser() {
        Long currentUserId = authService.getCurrentUser().getId();
        List<Notification> notifications = notificationService.getNotificationsForUser(currentUserId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notification>> getUnreadNotificationsForUser() {
        Long currentUserId = authService.getCurrentUser().getId();
        List<Notification> notifications = notificationService.getUnreadNotificationsForUser(currentUserId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/preferences")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationPreference> getNotificationPreferences() {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PutMapping("/preferences")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationPreference> updateNotificationPreferences(@RequestBody NotificationPreference preferences) {
        // Implémentation à développer
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
