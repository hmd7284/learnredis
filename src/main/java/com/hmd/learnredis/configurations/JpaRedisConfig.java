package com.hmd.learnredis.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.hmd.learnredis.repositories.jpa")
public class JpaRedisConfig {
}
