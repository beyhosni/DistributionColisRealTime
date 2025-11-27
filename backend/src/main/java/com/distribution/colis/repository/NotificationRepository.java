package com.distribution.colis.repository;

import com.distribution.colis.model.entity.Notification;
import com.distribution.colis.model.entity.NotificationStatus;
import com.distribution.colis.model.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByType(NotificationType type);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.status = :status")
    List<Notification> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") NotificationStatus status);

    @Query("SELECT n FROM Notification n WHERE n.status = 'PENDING' AND n.createdAt < :thresholdDate")
    List<Notification> findPendingNotificationsOlderThan(@Param("thresholdDate") LocalDateTime thresholdDate);
}
