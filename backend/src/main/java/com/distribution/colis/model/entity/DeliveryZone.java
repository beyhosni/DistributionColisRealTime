package com.distribution.colis.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "delivery_zone")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private String description;

    @Column(columnDefinition = "TEXT[]")
    private List<String> postalCodes;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "zone")
    private List<PricingRule> pricingRules;

    @OneToMany(mappedBy = "zone")
    private List<DeliveryRoute> deliveryRoutes;
}
