package com.distribution.colis.kafka.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParcelAssignedEvent extends ParcelEvent {

    public ParcelAssignedEvent() {
        setEventType("COLIS_ASSIGNE");
    }

    @Override
    public void setData(ParcelEventData data) {
        super.setData(new ParcelAssignedData(data));
    }

    @Data
    static class ParcelAssignedData extends ParcelEventData {
        private Long routeId;
        private Long courierId;
        private LocalDateTime estimatedDelivery;
    }
}