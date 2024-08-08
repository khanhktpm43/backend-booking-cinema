package com.dev.booking.Service;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BlackList {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void  addToken(String token, long timeout){
        redisTemplate.opsForValue().set(token, token, timeout, TimeUnit.HOURS);
    }
    public boolean isTokenExists(String token){
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));

    }
}
