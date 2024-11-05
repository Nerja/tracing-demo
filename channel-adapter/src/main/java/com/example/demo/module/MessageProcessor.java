package com.example.demo.module;

import com.example.demo.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class MessageProcessor {

    private final WebClient webClient;

    public MessageProcessor(WebClient.Builder builder) {
        webClient = builder.build();
    }

    public void processMessage(Message message) throws ExecutionException, InterruptedException {
        webClient
                .get()
                        .uri("http://tmp-tempo-query-frontend.monitoring:3100/metrics")
                                .retrieve()
                                        .toBodilessEntity()
                                                .block();
        log.info("Processed messageId: {}", message.getMessageId());
    }

}
