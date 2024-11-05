package com.example.demo.kafka;

import com.example.demo.model.Message;
import com.example.demo.module.MessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class MessageListener {

    private final MessageProcessor messageProcessor;

    public MessageListener(final RabbitProperties rabbitProperties, final MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

   @RabbitListener(queues = "message-outbound", concurrency = "100")
    public void receiveMessage(final Message message) throws ExecutionException, InterruptedException {
        messageProcessor.processMessage(message);
    }

}
