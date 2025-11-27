package com.distribution.colis.repository;

import com.distribution.colis.model.entity.DeliveryZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryZoneRepository extends JpaRepository<DeliveryZone, Long> {

    List<DeliveryZone> findByActive(Boolean active);

    @Query("SELECT z FROM DeliveryZone z WHERE :postalCode = ANY(z.postalCodes)")
    Optional<DeliveryZone> findByPostalCode(@Param("postalCode") String postalCode);

    Boolean existsByName(String name);
}
