package com.distribution.colis.repository;

import com.distribution.colis.model.entity.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, Long> {

    Optional<Parcel> findByTrackingNumber(String trackingNumber);

    Boolean existsByTrackingNumber(String trackingNumber);
}
