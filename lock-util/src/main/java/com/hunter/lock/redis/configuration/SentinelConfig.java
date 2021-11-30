package com.hunter.lock.redis.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class SentinelConfig {

    @Bean
    public RedissonClient redisson() throws IOException {
        Config config = Config.fromYAML(new ClassPathResource("redisson-sentinel.yml").getInputStream());
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
