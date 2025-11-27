package com.distribution.colis.kafka.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ParcelCreatedEvent extends ParcelEvent {

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

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ParcelAssignedEvent extends ParcelEvent {

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

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ParcelInDeliveryEvent extends ParcelEvent {

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

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ParcelDeliveredEvent extends ParcelEvent {

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

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ParcelIncidentEvent extends ParcelEvent {

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
