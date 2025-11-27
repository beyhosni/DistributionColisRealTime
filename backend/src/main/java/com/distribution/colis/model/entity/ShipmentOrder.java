package com.distribution.colis.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipment_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcel_id", nullable = false)
    private Parcel parcel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName;

    @Column(name = "recipient_phone")
    private String recipientPhone;

    @Column(name = "recipient_address_line1", nullable = false)
    private String recipientAddressLine1;

    @Column(name = "recipient_address_line2")
    private String recipientAddressLine2;

    @Column(name = "recipient_city", nullable = false, length = 100)
    private String recipientCity;

    @Column(name = "recipient_postal_code", nullable = false, length = 20)
    private String recipientPostalCode;

    @Column(name = "recipient_country")
    private String recipientCountry;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 50)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrderStatus status = OrderStatus.BORNE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private Boolean insurance = false;

    @Column(name = "signature_required")
    private Boolean signatureRequired = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order")
    private List<DeliveryTask> deliveryTasks;

    @OneToMany(mappedBy = "order")
    private List<ParcelStatus> statusHistory;
}

enum OrderStatus {
    BORNE,
    VALIDEE,
    PAYEE,
    ASSIGNEE,
    EN_COURS,
    LIVREE,
    ANNULEE,
    RETOUR
}
