package com.example.eventmanagement.service;

import com.example.eventmanagement.dto.UserDTO;
import com.example.eventmanagement.util.CacheableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private static final String CACHE_KEY_PREFIX = "cache:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public <T extends CacheableResource> void cacheResource(T resource) {
        String cacheKey = CACHE_KEY_PREFIX + resource.getClass().getSimpleName() + ":" + resource.getId();
        redisTemplate.opsForValue().set(cacheKey, resource, 1, TimeUnit.HOURS);
    }

    public <T extends CacheableResource> void cacheAllResources(List<T> resources) {
        if (!resources.isEmpty()) {
            String cacheKey = CACHE_KEY_PREFIX + resources.getFirst().getClass().getSimpleName() + ":all";
            redisTemplate.opsForValue().set(cacheKey, resources, 1L, TimeUnit.HOURS);
        }
    }

    public <T extends CacheableResource> T getCachedResource(Long id, Class<T> resourceClass) {
        String cacheKey = CACHE_KEY_PREFIX + resourceClass.getSimpleName() + ":" + id;
        return (T) redisTemplate.opsForValue().get(cacheKey);
    }

    public <T extends CacheableResource> List<T> getCachedAllResources(Class<T> resourceClass) {
        String cacheKey = CACHE_KEY_PREFIX + resourceClass.getSimpleName() + ":all";
        return (List<T>) redisTemplate.opsForValue().get(cacheKey);
    }

    public void invalidateCacheForResource(Long id, Class<?> resourceClass) {
        String cacheKey = CACHE_KEY_PREFIX + resourceClass.getSimpleName() + ":" + id;
        redisTemplate.delete(cacheKey);
    }

    public void invalidateCacheForAllResources(Class<?> resourceClass) {
        String cacheKey = CACHE_KEY_PREFIX + resourceClass.getSimpleName() + ":all";
        redisTemplate.delete(cacheKey);
    }
}
