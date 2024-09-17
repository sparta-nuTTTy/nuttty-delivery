package com.nuttty.eureka.order.config;

import com.nuttty.eureka.order.exception.FeginErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeginConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeginErrorDecoder();
    }
}
