package com.hmd.learnredis.controllers;

import com.hmd.learnredis.dtos.requests.RedisRequest;
import com.hmd.learnredis.services.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {
    private final RedisService redisService;

    @GetMapping("/{key}")
    @ResponseStatus(HttpStatus.OK)
    public Object getValue(@PathVariable String key) {
        return redisService.get(key);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void putValue(@RequestBody RedisRequest request) {
        redisService.put(request.getKey(), request.getValue());
    }
}
