package ru.homecredit.microservice.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@Getter
public class CacheService {
    private String URL;
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    public CacheService(@Value("${dashboard.url}") String URL,
                        RestTemplate restTemplate, RedisTemplate<String, Object> redisTemplate) {
        this.URL = URL;
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Cacheable(cacheNames = "SERVICE", key = " #p0 + ':' + #p1 + '/' + #p2")
    public String getFromCache(String env, String service, String method) {
        return null;
    }

    @CachePut(cacheNames = "SERVICE", key = " #p0 + ':' + #p1 + '/' + #p2")
    public String populateCache(String env, String service, String method) {
        log.info("{} {} {}", env, service, method);
        String s = restTemplate.getForObject(URL, String.class, env, service, method);
        return s.replaceAll("\\s+", "");
    }

    @CacheEvict(value = "SERVICE", key = "#key")
    public void cleanCacheByKey(String key) {
        log.info("The cache was evicted by key: {}", key);
    }

    @CacheEvict(value = "SERVICE", allEntries = true)
    public void cleanAllCache() {
        log.info("Cache was cleared");
    }
}
