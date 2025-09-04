package com.hmd.learnredis.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void put(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void put(String key, Object value, Long ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public Optional<Object> get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value == null ? Optional.empty() : Optional.of(value);
    }

    public <T> Optional<T> get(String key, Class<T> clazz) {
        Object o = redisTemplate.opsForValue().get(key);
        if (o == null)
            return Optional.empty();
        if (clazz.isInstance(o))
            return Optional.of(clazz.cast(o));
        try {
            T converted = objectMapper.convertValue(o, clazz);
            return Optional.of(converted);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
