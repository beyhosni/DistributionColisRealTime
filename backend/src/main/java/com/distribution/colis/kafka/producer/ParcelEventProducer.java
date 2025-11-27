package com.distribution.colis.kafka.producer;

import com.distribution.colis.kafka.event.ParcelEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ParcelEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(ParcelEventProducer.class);

    private static final String TOPIC = "parcel-events";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendParcelEvent(ParcelEvent event) {
        logger.info("Sending parcel event: {} to topic: {}", event.getEventType(), TOPIC);

        try {
            kafkaTemplate.send(TOPIC, event.getEventId(), event);
        } catch (Exception e) {
            logger.error("Error sending parcel event: {}", e.getMessage());
        }
    }
}
