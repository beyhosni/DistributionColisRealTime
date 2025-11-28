package com.distribution.colis.kafka.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParcelCreatedEvent extends ParcelEvent {

    public ParcelCreatedEvent() {
        setEventType("COLIS_CREE");
    }

    @Override
    public void setData(ParcelEventData data) {
        super.setData(new ParcelCreatedData(data));
    }

    @Data
    static class ParcelCreatedData extends ParcelEventData {
        private Long senderId;
        private RecipientInfo recipientInfo;
        private String serviceType;
    }

    @Data
    static class RecipientInfo {
        private String name;
        private String address;
        private String city;
        private String postalCode;
    }
}