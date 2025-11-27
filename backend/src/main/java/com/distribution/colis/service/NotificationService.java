package com.distribution.colis.service;

import com.distribution.colis.model.entity.*;
import com.distribution.colis.repository.NotificationPreferenceRepository;
import com.distribution.colis.repository.NotificationRepository;
import com.distribution.colis.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository,
                             UserRepository userRepository,
                             NotificationPreferenceRepository notificationPreferenceRepository,
                             EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Notification createNotification(Long userId, String title, String message, NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setStatus(NotificationStatus.PENDING);

        notification = notificationRepository.save(notification);

        // Vérifier les préférences de notification de l'utilisateur
        Optional<NotificationPreference> prefOpt = notificationPreferenceRepository.findByUserId(userId);
        if (prefOpt.isPresent()) {
            NotificationPreference pref = prefOpt.get();

            // Envoyer selon les préférences
            if (type == NotificationType.EMAIL && pref.getEmailEnabled()) {
                sendEmailNotification(notification);
            } else if (type == NotificationType.SMS && pref.getSmsEnabled()) {
                // Implémenter l'envoi de SMS
                sendSmsNotification(notification);
            } else if (type == NotificationType.PUSH && pref.getPushEnabled()) {
                // Implémenter l'envoi de notification push
                sendPushNotification(notification);
            }
        } else {
            // Préférences par défaut : email activé
            if (type == NotificationType.EMAIL) {
                sendEmailNotification(notification);
            }
        }

        return notification;
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // Note: Le statut de lecture n'est pas dans notre modèle, mais pourrait être ajouté
        // Pour l'instant, nous allons simplement marquer la notification comme envoyée
        if (notification.getStatus() == NotificationStatus.PENDING) {
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdAndStatus(userId, NotificationStatus.PENDING);
    }

    private void sendEmailNotification(Notification notification) {
        try {
            emailService.sendEmail(
                    notification.getUser().getEmail(),
                    notification.getTitle(),
                    notification.getMessage()
            );

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
        }
    }

    private void sendSmsNotification(Notification notification) {
        // Implémentation de l'envoi de SMS
        // Utiliser un service comme Twilio ou autre
        try {
            // smsService.sendSms(notification.getUser().getPhone(), notification.getMessage());

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
        }
    }

    private void sendPushNotification(Notification notification) {
        // Implémentation de l'envoi de notification push
        // Utiliser un service comme Firebase Cloud Messaging ou autre
        try {
            // pushService.sendPush(notification.getUser().getId(), notification.getTitle(), notification.getMessage());

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
        }
    }
}
