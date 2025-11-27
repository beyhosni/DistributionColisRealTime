package com.distribution.colis.repository;

import com.distribution.colis.model.entity.PricingRule;
import com.distribution.colis.model.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {

    List<PricingRule> findByActive(Boolean active);

    List<PricingRule> findByZoneId(Long zoneId);

    List<PricingRule> findByServiceType(ServiceType serviceType);

    @Query("SELECT p FROM PricingRule p WHERE p.zone.id = :zoneId AND p.serviceType = :serviceType AND p.active = true")
    Optional<PricingRule> findByZoneAndServiceType(@Param("zoneId") Long zoneId, @Param("serviceType") ServiceType serviceType);
}
