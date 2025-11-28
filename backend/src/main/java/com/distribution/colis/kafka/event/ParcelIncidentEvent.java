package com.distribution.colis.kafka.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParcelIncidentEvent extends ParcelEvent {

    public ParcelIncidentEvent() {
        setEventType("COLIS_INCIDENT");
    }

    @Override
    public void setData(ParcelEventData data) {
        super.setData(new ParcelIncidentData(data));
    }

    @Data
    static class ParcelIncidentData extends ParcelEventData {
        private Long courierId;
        private String incidentType;
        private String description;
        private String location;
        private LocalDateTime nextAttempt;
    }
}