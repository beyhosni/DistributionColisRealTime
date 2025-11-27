package com.distribution.colis.service;

import com.distribution.colis.model.dto.request.ParcelRequest;
import com.distribution.colis.model.dto.response.ParcelResponse;
import com.distribution.colis.model.entity.Parcel;
import com.distribution.colis.model.entity.ParcelStatus;
import com.distribution.colis.model.entity.ShipmentOrder;
import com.distribution.colis.model.entity.User;
import com.distribution.colis.repository.ParcelRepository;
import com.distribution.colis.repository.ParcelStatusRepository;
import com.distribution.colis.repository.ShipmentOrderRepository;
import com.distribution.colis.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final ShipmentOrderRepository shipmentOrderRepository;
    private final ParcelStatusRepository parcelStatusRepository;
    private final UserRepository userRepository;

    public ParcelService(ParcelRepository parcelRepository, 
                        ShipmentOrderRepository shipmentOrderRepository,
                        ParcelStatusRepository parcelStatusRepository,
                        UserRepository userRepository) {
        this.parcelRepository = parcelRepository;
        this.shipmentOrderRepository = shipmentOrderRepository;
        this.parcelStatusRepository = parcelStatusRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ParcelResponse createParcel(ParcelRequest parcelRequest, Long senderId) {
        // Générer un numéro de suivi unique
        String trackingNumber = generateTrackingNumber();

        // Créer le colis
        Parcel parcel = new Parcel();
        parcel.setWeight(parcelRequest.getWeight());
        parcel.setLength(parcelRequest.getLength());
        parcel.setWidth(parcelRequest.getWidth());
        parcel.setHeight(parcelRequest.getHeight());
        parcel.setContentDescription(parcelRequest.getContentDescription());
        parcel.setDeclaredValue(parcelRequest.getDeclaredValue());
        parcel.setTrackingNumber(trackingNumber);

        parcel = parcelRepository.save(parcel);

        // Créer la commande de livraison associée
        ShipmentOrder order = new ShipmentOrder();
        order.setParcel(parcel);
        order.setSender(userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found")));
        order.setRecipientName(parcelRequest.getRecipientName());
        order.setRecipientPhone(parcelRequest.getRecipientPhone());
        order.setRecipientAddressLine1(parcelRequest.getRecipientAddressLine1());
        order.setRecipientAddressLine2(parcelRequest.getRecipientAddressLine2());
        order.setRecipientCity(parcelRequest.getRecipientCity());
        order.setRecipientPostalCode(parcelRequest.getRecipientPostalCode());
        order.setRecipientCountry(parcelRequest.getRecipientCountry());
        order.setServiceType(parcelRequest.getServiceType());
        order.setStatus(com.distribution.colis.model.entity.OrderStatus.BORNE);
        order.setPrice(parcelRequest.getPrice());
        order.setInsurance(parcelRequest.getInsurance());
        order.setSignatureRequired(parcelRequest.getSignatureRequired());

        shipmentOrderRepository.save(order);

        // Ajouter un statut initial
        ParcelStatus status = new ParcelStatus();
        status.setOrder(order);
        status.setParcel(parcel);
        status.setStatus("BORNE");
        status.setDescription("Commande créée");
        status.setTimestamp(LocalDateTime.now());

        parcelStatusRepository.save(status);

        return convertToResponse(parcel, order);
    }

    public ParcelResponse getParcelByTrackingNumber(String trackingNumber) {
        Parcel parcel = parcelRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Parcel not found with tracking number: " + trackingNumber));

        ShipmentOrder order = shipmentOrderRepository.findByParcelTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Order not found for parcel: " + trackingNumber));

        return convertToResponse(parcel, order);
    }

    public List<ParcelResponse> getParcelsForUser(Long userId) {
        List<ShipmentOrder> orders = shipmentOrderRepository.findBySenderId(userId);
        return orders.stream()
                .map(order -> convertToResponse(order.getParcel(), order))
                .collect(Collectors.toList());
    }

    public List<ParcelResponse> getAllParcels() {
        List<Parcel> parcels = parcelRepository.findAll();
        return parcels.stream()
                .map(parcel -> {
                    ShipmentOrder order = shipmentOrderRepository.findByParcelTrackingNumber(parcel.getTrackingNumber())
                            .orElse(null);
                    return convertToResponse(parcel, order);
                })
                .collect(Collectors.toList());
    }

    private String generateTrackingNumber() {
        // Format: TRK + timestamp + random string
        return "TRK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ParcelResponse convertToResponse(Parcel parcel, ShipmentOrder order) {
        ParcelResponse response = new ParcelResponse();
        response.setId(parcel.getId());
        response.setTrackingNumber(parcel.getTrackingNumber());
        response.setWeight(parcel.getWeight());
        response.setLength(parcel.getLength());
        response.setWidth(parcel.getWidth());
        response.setHeight(parcel.getHeight());
        response.setContentDescription(parcel.getContentDescription());
        response.setDeclaredValue(parcel.getDeclaredValue());
        response.setCreatedAt(parcel.getCreatedAt());

        if (order != null) {
            response.setOrderId(order.getId());
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
        }

        return response;
    }
}
