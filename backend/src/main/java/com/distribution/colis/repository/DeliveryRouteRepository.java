package com.distribution.colis.repository;

import com.distribution.colis.model.entity.DeliveryRoute;
import com.distribution.colis.enums.RouteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRouteRepository extends JpaRepository<DeliveryRoute, Long> {

    List<DeliveryRoute> findByCourierId(Long courierId);

    List<DeliveryRoute> findByCourierIdAndRouteDate(Long courierId, LocalDate routeDate);

    List<DeliveryRoute> findByRouteDate(LocalDate routeDate);

    List<DeliveryRoute> findByStatus(RouteStatus status);

    @Query("SELECT r FROM DeliveryRoute r WHERE r.courier.id = :courierId AND r.routeDate = :routeDate")
    Optional<DeliveryRoute> findByCourierAndDate(@Param("courierId") Long courierId, @Param("routeDate") LocalDate routeDate);

    List<DeliveryRoute> findByZoneId(Long zoneId);
}
