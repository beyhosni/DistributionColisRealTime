package com.distribution.colis.repository;

import com.distribution.colis.model.entity.DeliveryTask;
import com.distribution.colis.model.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryTaskRepository extends JpaRepository<DeliveryTask, Long> {

    List<DeliveryTask> findByRouteId(Long routeId);

    List<DeliveryTask> findByOrderId(Long orderId);

    List<DeliveryTask> findByStatus(TaskStatus status);

    @Query("SELECT t FROM DeliveryTask t WHERE t.route.id = :routeId ORDER BY t.taskOrder")
    List<DeliveryTask> findByRouteIdOrderByTaskOrder(@Param("routeId") Long routeId);

    @Query("SELECT t FROM DeliveryTask t WHERE t.route.courier.id = :courierId AND t.status = :status")
    List<DeliveryTask> findByCourierIdAndStatus(@Param("courierId") Long courierId, @Param("status") TaskStatus status);
}
