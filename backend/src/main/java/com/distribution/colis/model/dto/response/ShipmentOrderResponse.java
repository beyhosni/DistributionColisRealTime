package com.distribution.colis.model.dto.response;

import com.distribution.colis.model.entity.OrderStatus;
import com.distribution.colis.enums.ServiceType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShipmentOrderResponse {

    private Long id;

    private Long parcelId;

    private String trackingNumber;

    private Long senderId;

    private String senderName;

    private String recipientName;

    private String recipientPhone;

    private String recipientAddressLine1;

    private String recipientAddressLine2;

    private String recipientCity;

    private String recipientPostalCode;

    private String recipientCountry;

    private ServiceType serviceType;

    private OrderStatus status;

    private BigDecimal price;

    private Boolean insurance;

    private Boolean signatureRequired;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
