package com.distribution.colis.model.entity;

import com.distribution.colis.enums.IncidentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_incident")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private DeliveryTask task;

    @Enumerated(EnumType.STRING)
    @Column(name = "incident_type", nullable = false, length = 50)
    private IncidentType incidentType;

    private String description;

    @Column(nullable = false)
    private Boolean resolved = false;

    @Column(name = "resolution_notes")
    private String resolutionNotes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}
