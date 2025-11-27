package com.distribution.colis.repository;

import com.distribution.colis.model.entity.ShipmentOrder;
import com.distribution.colis.model.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentOrderRepository extends JpaRepository<ShipmentOrder, Long> {

    List<ShipmentOrder> findBySenderId(Long senderId);

    List<ShipmentOrder> findByStatus(OrderStatus status);

    @Query("SELECT o FROM ShipmentOrder o WHERE o.sender.id = :senderId AND o.status = :status")
    List<ShipmentOrder> findBySenderIdAndStatus(@Param("senderId") Long senderId, @Param("status") OrderStatus status);

    Optional<ShipmentOrder> findByParcelTrackingNumber(String trackingNumber);
}
