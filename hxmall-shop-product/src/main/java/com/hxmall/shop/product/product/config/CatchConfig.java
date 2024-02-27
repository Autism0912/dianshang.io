package com.hxmall.shop.product.product.config;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * author:黄龙强
 * time:{2023/12/29}
 * version:1.0
 */
@Configuration
@EnableConfigurationProperties(CacheProperties.class)
@EnableCaching
public class CatchConfig {
    @Bean
    RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties){
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        //设置序列化机制
        redisCacheConfiguration = redisCacheConfiguration.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        CacheProperties.Redis  redis= cacheProperties.getRedis();
        if (redis.getTimeToLive() !=null){
            redisCacheConfiguration = redisCacheConfiguration.entryTtl(redis.getTimeToLive());
        }
        if (redis.getKeyPrefix() !=null){
            redisCacheConfiguration = redisCacheConfiguration.prefixCacheNameWith(redis.getKeyPrefix());
        }
        if (!redis.isCacheNullValues()){
            redisCacheConfiguration = redisCacheConfiguration.disableCachingNullValues();
        }

        if (!redis.isUseKeyPrefix()){
        redisCacheConfiguration=redisCacheConfiguration.disableKeyPrefix();
        }
        return redisCacheConfiguration;
    }
}
