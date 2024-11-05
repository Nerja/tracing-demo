package com.example.demo.module;

import com.example.demo.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.lang.Math;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class MessageProcessor {

    @Autowired private KafkaTemplate<String, Message> kafkaTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void processMessage(Message message) throws ExecutionException, InterruptedException {
        log.info("Received message with id {}", message.getMessageId());
        try {
            redisTemplate.opsForValue().set("hello", "there", Duration.ofSeconds(10));
        } catch (Exception e) {
            log.error("Error setting value in redis", e);
            throw e;
        }
        String query = "SELECT 1";
        if (Math.random() < 0) { // Change to non-zero to simulate a slow query
            query = "SELECT SLEEP(1)";
        }
        template.getDatabaseClient()
                .sql(query)
                .fetch()
                .one()
                .block();
        rabbitTemplate.convertAndSend("message-outbound", message);
        log.info("Processed message with id {}", message.getMessageId());
    }

}
