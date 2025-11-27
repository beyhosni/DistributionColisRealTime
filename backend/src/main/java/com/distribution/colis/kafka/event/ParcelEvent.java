package com.distribution.colis.kafka.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ParcelCreatedEvent.class, name = "COLIS_CREE"),
        @JsonSubTypes.Type(value = ParcelAssignedEvent.class, name = "COLIS_ASSIGNE"),
        @JsonSubTypes.Type(value = ParcelInDeliveryEvent.class, name = "COLIS_EN_LIVRAISON"),
        @JsonSubTypes.Type(value = ParcelDeliveredEvent.class, name = "COLIS_LIVRE"),
        @JsonSubTypes.Type(value = ParcelIncidentEvent.class, name = "COLIS_INCIDENT")
})
public abstract class ParcelEvent {

    private String eventId = UUID.randomUUID().toString();

    private String eventType;

    private LocalDateTime timestamp = LocalDateTime.now();

    private ParcelEventData data;
}

@Data
class ParcelEventData {
    private Long parcelId;
    private String trackingNumber;
}
