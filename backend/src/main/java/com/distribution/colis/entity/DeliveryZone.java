package com.distribution.colis.entity;

import com.distribution.colis.model.entity.DeliveryRoute;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "delivery_zone")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ElementCollection
    @CollectionTable(name = "delivery_zone_postal_codes", joinColumns = @JoinColumn(name = "zone_id"))
    @Column(name = "postal_code")
    private List<String> postalCodes;

    private boolean active;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PricingRule> pricingRules;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeliveryRoute> deliveryRoutes;
}
