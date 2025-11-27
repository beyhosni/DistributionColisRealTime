package com.distribution.colis.kafka.consumer;

import com.distribution.colis.kafka.event.*;
import com.distribution.colis.model.entity.Notification;
import com.distribution.colis.model.entity.NotificationType;
import com.distribution.colis.model.entity.ShipmentOrder;
import com.distribution.colis.model.entity.User;
import com.distribution.colis.repository.ShipmentOrderRepository;
import com.distribution.colis.repository.UserRepository;
import com.distribution.colis.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ShipmentOrderRepository shipmentOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @KafkaListener(topics = "parcel-events", groupId = "notification-group")
    public void handleParcelEvent(ParcelEvent event) {
        logger.info("Received parcel event: {}", event.getEventType());

        try {
            switch (event.getEventType()) {
                case "COLIS_CREE":
                    handleParcelCreatedEvent((ParcelCreatedEvent) event);
                    break;
                case "COLIS_ASSIGNE":
                    handleParcelAssignedEvent((ParcelAssignedEvent) event);
                    break;
                case "COLIS_EN_LIVRAISON":
                    handleParcelInDeliveryEvent((ParcelInDeliveryEvent) event);
                    break;
                case "COLIS_LIVRE":
                    handleParcelDeliveredEvent((ParcelDeliveredEvent) event);
                    break;
                case "COLIS_INCIDENT":
                    handleParcelIncidentEvent((ParcelIncidentEvent) event);
                    break;
                default:
                    logger.warn("Unknown parcel event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            logger.error("Error processing parcel event: {}", e.getMessage());
        }
    }

    private void handleParcelCreatedEvent(ParcelCreatedEvent event) {
        ParcelCreatedEvent.ParcelCreatedData data = 
                (ParcelCreatedEvent.ParcelCreatedData) event.getData();

        // Récupérer l'expéditeur
        ShipmentOrder order = shipmentOrderRepository.findByParcelTrackingNumber(data.getTrackingNumber())
                .orElse(null);

        if (order != null) {
            User sender = order.getSender();

            // Envoyer une notification à l'expéditeur
            notificationService.createNotification(
                    sender.getId(),
                    "Colis créé",
                    "Votre colis " + data.getTrackingNumber() + " a été créé avec succès.",
                    NotificationType.EMAIL
            );
        }
    }

    private void handleParcelAssignedEvent(ParcelAssignedEvent event) {
        ParcelAssignedEvent.ParcelAssignedData data = 
                (ParcelAssignedEvent.ParcelAssignedData) event.getData();

        // Récupérer l'expéditeur
        ShipmentOrder order = shipmentOrderRepository.findByParcelTrackingNumber(data.getTrackingNumber())
                .orElse(null);

        if (order != null) {
            User sender = order.getSender();

            // Envoyer une notification à l'expéditeur
            notificationService.createNotification(
                    sender.getId(),
                    "Colis assigné",
                    "Votre colis " + data.getTrackingNumber() + " a été assigné à un livreur.",
                    NotificationType.EMAIL
            );
        }
    }

    private void handleParcelInDeliveryEvent(ParcelInDeliveryEvent event) {
        ParcelInDeliveryEvent.ParcelInDeliveryData data = 
                (ParcelInDeliveryEvent.ParcelInDeliveryData) event.getData();

        // Récupérer l'expéditeur
        ShipmentOrder order = shipmentOrderRepository.findByParcelTrackingNumber(data.getTrackingNumber())
                .orElse(null);

        if (order != null) {
            User sender = order.getSender();

            // Envoyer une notification à l'expéditeur
            notificationService.createNotification(
                    sender.getId(),
                    "Colis en livraison",
                    "Votre colis " + data.getTrackingNumber() + " est en cours de livraison. " +
                            "Position actuelle: " + data.getLocation(),
                    NotificationType.EMAIL
            );
        }
    }

    private void handleParcelDeliveredEvent(ParcelDeliveredEvent event) {
        ParcelDeliveredEvent.ParcelDeliveredData data = 
                (ParcelDeliveredEvent.ParcelDeliveredData) event.getData();

        // Récupérer l'expéditeur
        ShipmentOrder order = shipmentOrderRepository.findByParcelTrackingNumber(data.getTrackingNumber())
                .orElse(null);

        if (order != null) {
            User sender = order.getSender();

            // Envoyer une notification à l'expéditeur
            notificationService.createNotification(
                    sender.getId(),
                    "Colis livré",
                    "Votre colis " + data.getTrackingNumber() + " a été livré avec succès à " +
                            data.getRecipientName() + " le " + data.getDeliveryTime(),
                    NotificationType.EMAIL
            );
        }
    }

    private void handleParcelIncidentEvent(ParcelIncidentEvent event) {
        ParcelIncidentEvent.ParcelIncidentData data = 
                (ParcelIncidentEvent.ParcelIncidentData) event.getData();

        // Récupérer l'expéditeur
        ShipmentOrder order = shipmentOrderRepository.findByParcelTrackingNumber(data.getTrackingNumber())
                .orElse(null);

        if (order != null) {
            User sender = order.getSender();

            // Envoyer une notification à l'expéditeur
            notificationService.createNotification(
                    sender.getId(),
                    "Incident de livraison",
                    "Un incident est survenu lors de la livraison de votre colis " + 
                            data.getTrackingNumber() + ": " + data.getDescription() +
                            ". Prochaine tentative: " + data.getNextAttempt(),
                    NotificationType.EMAIL
            );
        }
    }
}
