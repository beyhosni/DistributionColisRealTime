package com.distribution.colis.service;

import com.distribution.colis.model.entity.DeliveryZone;
import com.distribution.colis.model.entity.Parcel;
import com.distribution.colis.model.entity.PricingRule;
import com.distribution.colis.model.entity.ServiceType;
import com.distribution.colis.repository.DeliveryZoneRepository;
import com.distribution.colis.repository.PricingRuleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PricingService {

    private final PricingRuleRepository pricingRuleRepository;
    private final DeliveryZoneRepository deliveryZoneRepository;

    public PricingService(PricingRuleRepository pricingRuleRepository,
                         DeliveryZoneRepository deliveryZoneRepository) {
        this.pricingRuleRepository = pricingRuleRepository;
        this.deliveryZoneRepository = deliveryZoneRepository;
    }

    public BigDecimal calculatePrice(Parcel parcel, String postalCode, ServiceType serviceType, 
                                   boolean insurance, boolean signatureRequired) {
        // Trouver la zone de livraison
        Optional<DeliveryZone> zoneOpt = deliveryZoneRepository.findByPostalCode(postalCode);

        // Si la zone n'est pas trouvée, utiliser une règle par défaut
        if (zoneOpt.isEmpty()) {
            return getDefaultPrice(parcel, serviceType, insurance, signatureRequired);
        }

        DeliveryZone zone = zoneOpt.get();

        // Trouver la règle de tarification pour cette zone et ce type de service
        Optional<PricingRule> ruleOpt = pricingRuleRepository.findByZoneAndServiceType(zone.getId(), serviceType);

        if (ruleOpt.isEmpty()) {
            return getDefaultPrice(parcel, serviceType, insurance, signatureRequired);
        }

        PricingRule rule = ruleOpt.get();

        // Calculer le prix de base
        BigDecimal price = rule.getBasePrice().add(parcel.getWeight().multiply(rule.getPricePerKg()));

        // Ajouter les options supplémentaires
        if (insurance) {
            // Assurance: 1% de la valeur déclarée, minimum 2€
            BigDecimal insuranceFee = parcel.getDeclaredValue().multiply(new BigDecimal("0.01"));
            if (insuranceFee.compareTo(new BigDecimal("2.00")) < 0) {
                insuranceFee = new BigDecimal("2.00");
            }
            price = price.add(insuranceFee);
        }

        if (signatureRequired) {
            // Frais de signature: 1.50€
            price = price.add(new BigDecimal("1.50"));
        }

        return price;
    }

    private BigDecimal getDefaultPrice(Parcel parcel, ServiceType serviceType, 
                                     boolean insurance, boolean signatureRequired) {
        // Prix par défaut si aucune règle spécifique n'est trouvée
        BigDecimal basePrice = serviceType == ServiceType.EXPRESS ? 
                new BigDecimal("15.00") : new BigDecimal("10.00");

        BigDecimal price = basePrice.add(parcel.getWeight().multiply(new BigDecimal("2.00")));

        // Ajouter les options supplémentaires
        if (insurance) {
            // Assurance: 1% de la valeur déclarée, minimum 2€
            BigDecimal insuranceFee = parcel.getDeclaredValue().multiply(new BigDecimal("0.01"));
            if (insuranceFee.compareTo(new BigDecimal("2.00")) < 0) {
                insuranceFee = new BigDecimal("2.00");
            }
            price = price.add(insuranceFee);
        }

        if (signatureRequired) {
            // Frais de signature: 1.50€
            price = price.add(new BigDecimal("1.50"));
        }

        return price;
    }
}
