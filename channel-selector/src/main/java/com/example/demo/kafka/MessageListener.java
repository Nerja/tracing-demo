package com.example.demo.kafka;

import com.example.demo.model.Message;
import com.example.demo.module.MessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class MessageListener {

    @Autowired
    private MessageProcessor messageProcessor;

    @KafkaListener(topics="message-inbound", id="channel-selector")
    public void receiveMessage(final Message message) throws ExecutionException, InterruptedException {
        messageProcessor.processMessage(message);
    }

}
