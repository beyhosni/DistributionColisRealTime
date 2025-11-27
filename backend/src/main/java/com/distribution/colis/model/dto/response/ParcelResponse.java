package com.distribution.colis.model.dto.response;

import com.distribution.colis.model.entity.OrderStatus;
import com.distribution.colis.model.entity.ServiceType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ParcelResponse {

    private Long id;

    private Long orderId;

    private String trackingNumber;

    private BigDecimal weight;

    private BigDecimal length;

    private BigDecimal width;

    private BigDecimal height;

    private String contentDescription;

    private BigDecimal declaredValue;

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
