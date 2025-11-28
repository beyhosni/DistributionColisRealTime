package com.distribution.colis.kafka.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParcelInDeliveryEvent extends ParcelEvent {

    public ParcelInDeliveryEvent() {
        setEventType("COLIS_EN_LIVRAISON");
    }

    @Override
    public void setData(ParcelEventData data) {
        super.setData(new ParcelInDeliveryData(data));
    }

    @Data
    static class ParcelInDeliveryData extends ParcelEventData {
        private Long courierId;
        private String location;
    }
}