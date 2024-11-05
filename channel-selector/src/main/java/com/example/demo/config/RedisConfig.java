package com.example.demo.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.autoconfigure.data.redis.ClientResourcesBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.observability.MicrometerTracingAdapter;

@Configuration
public class RedisConfig {

    @Bean
    public ClientResourcesBuilderCustomizer tracingCustomizer(final ObservationRegistry observationRegistry) {
        return clientResourcesBuilder -> {
            clientResourcesBuilder.tracing(new MicrometerTracingAdapter(observationRegistry, "redis"));
        };
    }

}
