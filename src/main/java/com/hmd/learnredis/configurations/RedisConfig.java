package com.hmd.learnredis.configurations;

import io.lettuce.core.ReadFrom;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    private final ClusterConfigurationProperties clusterConfigurationProperties;

    public RedisConfig(ClusterConfigurationProperties clusterConfigurationProperties) {
        this.clusterConfigurationProperties = clusterConfigurationProperties;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterConfigurationProperties.getNodes());
        redisClusterConfiguration.setPassword(clusterConfigurationProperties.getPassword());
        redisClusterConfiguration.setUsername(clusterConfigurationProperties.getUsername());
        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .build();
        return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setHashKeySerializer(keySerializer);
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
