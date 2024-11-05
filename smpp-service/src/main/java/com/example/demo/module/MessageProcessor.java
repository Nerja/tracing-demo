package com.example.demo.module;

import com.example.demo.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class MessageProcessor {

    @Autowired private KafkaTemplate<String, Message> kafkaTemplate;

    public void processMessage(Message message) throws ExecutionException, InterruptedException {
        //log.info("Processed message {} with id {}", message.getText(), message.getMessageId());
        kafkaTemplate.send("message-inbound", message).get();
    }

}
