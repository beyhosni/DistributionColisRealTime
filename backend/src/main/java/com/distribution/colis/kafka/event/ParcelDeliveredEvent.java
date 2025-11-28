package com.distribution.colis.kafka.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParcelDeliveredEvent extends ParcelEvent {

    public ParcelDeliveredEvent() {
        setEventType("COLIS_LIVRE");
    }

    @Override
    public void setData(ParcelEventData data) {
        super.setData(new ParcelDeliveredData(data));
    }

    @Data
    static class ParcelDeliveredData extends ParcelEventData {
        private Long courierId;
        private LocalDateTime deliveryTime;
        private String recipientName;
        private String signature;
    }
}