package com.hmd.learnredis.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {
    @KafkaListener(topics = "user", groupId = "group-1")
    public void consume(String message) {
        System.out.println(message);
    }
}
