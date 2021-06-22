package com.singfung.blackfriday.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.PostConstruct;

/**
 * Redis Configuration
 *
 * @author sing-fung
 */
@Configuration
public class RedisConfiguration
{
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * configure RedisSerializer
     */
    @PostConstruct
    public void initRedisTemplate()
    {
        RedisSerializer stringRedisSerializer = redisTemplate.getStringSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(stringRedisSerializer);
    }
}
