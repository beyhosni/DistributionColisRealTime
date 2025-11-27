package com.distribution.colis.repository;

import com.distribution.colis.model.entity.ParcelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParcelStatusRepository extends JpaRepository<ParcelStatus, Long> {

    List<ParcelStatus> findByParcelId(Long parcelId);

    List<ParcelStatus> findByOrderId(Long orderId);

    List<ParcelStatus> findByParcelIdOrderByTimestampDesc(Long parcelId);
}
