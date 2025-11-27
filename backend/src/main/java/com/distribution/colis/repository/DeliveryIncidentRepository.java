package com.distribution.colis.repository;

import com.distribution.colis.model.entity.DeliveryIncident;
import com.distribution.colis.model.entity.IncidentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeliveryIncidentRepository extends JpaRepository<DeliveryIncident, Long> {

    List<DeliveryIncident> findByTaskId(Long taskId);

    List<DeliveryIncident> findByResolved(Boolean resolved);

    List<DeliveryIncident> findByIncidentType(IncidentType incidentType);

    @Query("SELECT i FROM DeliveryIncident i WHERE i.resolved = false AND i.createdAt < :thresholdDate")
    List<DeliveryIncident> findUnresolvedIncidentsOlderThan(@Param("thresholdDate") LocalDateTime thresholdDate);

    @Query("SELECT i FROM DeliveryIncident i WHERE i.task.route.id = :routeId")
    List<DeliveryIncident> findByRouteId(@Param("routeId") Long routeId);
}
