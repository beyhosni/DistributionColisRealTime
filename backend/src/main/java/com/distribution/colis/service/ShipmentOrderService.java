package com.distribution.colis.service;

import com.distribution.colis.model.dto.request.ShipmentOrderRequest;
import com.distribution.colis.model.dto.response.ShipmentOrderResponse;
import com.distribution.colis.enums.ServiceType;
import com.distribution.colis.model.entity.*;
import com.distribution.colis.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShipmentOrderService {

    private final ShipmentOrderRepository shipmentOrderRepository;
    private final ParcelRepository parcelRepository;
    private final UserRepository userRepository;
    private final DeliveryZoneRepository deliveryZoneRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final ParcelStatusRepository parcelStatusRepository;

    public ShipmentOrderService(ShipmentOrderRepository shipmentOrderRepository,
                                ParcelRepository parcelRepository,
                                UserRepository userRepository,
                                DeliveryZoneRepository deliveryZoneRepository,
                                PricingRuleRepository pricingRuleRepository,
                                ParcelStatusRepository parcelStatusRepository) {
        this.shipmentOrderRepository = shipmentOrderRepository;
        this.parcelRepository = parcelRepository;
        this.userRepository = userRepository;
        this.deliveryZoneRepository = deliveryZoneRepository;
        this.pricingRuleRepository = pricingRuleRepository;
        this.parcelStatusRepository = parcelStatusRepository;
    }

    @Transactional
    public ShipmentOrderResponse createOrder(ShipmentOrderRequest request, Long senderId) {
        // Vérifier que l'expéditeur existe
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // Créer ou récupérer le colis
        Parcel parcel;
        if (request.getParcelId() != null) {
            parcel = parcelRepository.findById(request.getParcelId())
                    .orElseThrow(() -> new RuntimeException("Parcel not found"));
        } else {
            // Créer un nouveau colis si non fourni
            parcel = new Parcel();
            parcel.setWeight(request.getWeight());
            parcel.setLength(request.getLength());
            parcel.setWidth(request.getWidth());
            parcel.setHeight(request.getHeight());
            parcel.setContentDescription(request.getContentDescription());
            parcel.setDeclaredValue(request.getDeclaredValue());
            parcel.setTrackingNumber(generateTrackingNumber());
            parcel = parcelRepository.save(parcel);
        }

        // Déterminer la zone de livraison
        Optional<DeliveryZone> zoneOpt = deliveryZoneRepository.findByPostalCode(request.getRecipientPostalCode());
        DeliveryZone zone = zoneOpt.orElseThrow(() -> new RuntimeException("Delivery zone not found for postal code"));

        // Calculer le prix
        BigDecimal price = calculatePrice(parcel, zone, request.getServiceType());

        // Créer la commande
        ShipmentOrder order = new ShipmentOrder();
        order.setParcel(parcel);
        order.setSender(sender);
        order.setRecipientName(request.getRecipientName());
        order.setRecipientPhone(request.getRecipientPhone());
        order.setRecipientAddressLine1(request.getRecipientAddressLine1());
        order.setRecipientAddressLine2(request.getRecipientAddressLine2());
        order.setRecipientCity(request.getRecipientCity());
        order.setRecipientPostalCode(request.getRecipientPostalCode());
        order.setRecipientCountry(request.getRecipientCountry());
        order.setServiceType(request.getServiceType());
        order.setStatus(OrderStatus.BORNE);
        order.setPrice(price);
        order.setInsurance(request.getInsurance());
        order.setSignatureRequired(request.getSignatureRequired());

        order = shipmentOrderRepository.save(order);

        // Ajouter un statut initial
        ParcelStatus status = new ParcelStatus();
        status.setOrder(order);
        status.setParcel(parcel);
        status.setStatus("BORNE");
        status.setDescription("Commande créée");
        status.setTimestamp(LocalDateTime.now());
        parcelStatusRepository.save(status);

        return convertToResponse(order);
    }

    @Transactional
    public ShipmentOrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        ShipmentOrder order = shipmentOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        order = shipmentOrderRepository.save(order);

        // Ajouter un statut à l'historique
        ParcelStatus status = new ParcelStatus();
        status.setOrder(order);
        status.setParcel(order.getParcel());
        status.setStatus(newStatus.toString());
        status.setDescription("Statut changé de " + oldStatus + " à " + newStatus);
        status.setTimestamp(LocalDateTime.now());
        parcelStatusRepository.save(status);

        return convertToResponse(order);
    }

    public ShipmentOrderResponse getOrderById(Long orderId) {
        ShipmentOrder order = shipmentOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToResponse(order);
    }

    public List<ShipmentOrderResponse> getOrdersBySender(Long senderId) {
        List<ShipmentOrder> orders = shipmentOrderRepository.findBySenderId(senderId);
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ShipmentOrderResponse> getOrdersByStatus(OrderStatus status) {
        List<ShipmentOrder> orders = shipmentOrderRepository.findByStatus(status);
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private BigDecimal calculatePrice(Parcel parcel, DeliveryZone zone, ServiceType serviceType) {
        Optional<PricingRule> ruleOpt = pricingRuleRepository.findByZoneAndServiceType(zone.getId(), serviceType);

        if (ruleOpt.isPresent()) {
            PricingRule rule = ruleOpt.get();
            // Prix de base + (poids en kg * prix par kg)
            return rule.getBasePrice().add(parcel.getWeight().multiply(rule.getPricePerKg()));
        }

        // Règle par défaut si aucune règle spécifique n'est trouvée
        return new BigDecimal("10.00").add(parcel.getWeight().multiply(new BigDecimal("2.00")));
    }

    private String generateTrackingNumber() {
        return "TRK" + System.currentTimeMillis();
    }

    private ShipmentOrderResponse convertToResponse(ShipmentOrder order) {
        ShipmentOrderResponse response = new ShipmentOrderResponse();
        response.setId(order.getId());
        response.setParcelId(order.getParcel().getId());
        response.setTrackingNumber(order.getParcel().getTrackingNumber());
        response.setSenderId(order.getSender().getId());
        response.setSenderName(order.getSender().getFirstName() + " " + order.getSender().getLastName());
        response.setRecipientName(order.getRecipientName());
        response.setRecipientPhone(order.getRecipientPhone());
        response.setRecipientAddressLine1(order.getRecipientAddressLine1());
        response.setRecipientAddressLine2(order.getRecipientAddressLine2());
        response.setRecipientCity(order.getRecipientCity());
        response.setRecipientPostalCode(order.getRecipientPostalCode());
        response.setRecipientCountry(order.getRecipientCountry());
        response.setServiceType(order.getServiceType());
        response.setStatus(order.getStatus());
        response.setPrice(order.getPrice());
        response.setInsurance(order.getInsurance());
        response.setSignatureRequired(order.getSignatureRequired());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }
}
