package com.enterprise.pay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;

/**
 * Legacy Redis Distributed Lock & Token Bucket Service using Jedis 3.x direct pool
 * and manual raw Lua script execution (Anti-Pattern: manual connection lifecycle & raw Lua).
 */
@Service
public class RedisDistributedLockService {

    private final JedisPool jedisPool;

    // Lua script to safely release distributed lock (check requestId ownership before del)
    private static final String UNLOCK_LUA_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";

    // Lua script for sliding window rate limiter
    private static final String RATE_LIMIT_LUA_SCRIPT =
            "local key = KEYS[1] " +
            "local now = tonumber(ARGV[1]) " +
            "local window = tonumber(ARGV[2]) " +
            "local limit = tonumber(ARGV[3]) " +
            "local clearBefore = now - window " +
            "redis.call('ZREMRANGEBYSCORE', key, 0, clearBefore) " +
            "local currentRequests = redis.call('ZCARD', key) " +
            "if currentRequests < limit then " +
            "    redis.call('ZADD', key, now, now) " +
            "    redis.call('EXPIRE', key, math.ceil(window / 1000)) " +
            "    return 1 " +
            "else " +
            "    return 0 " +
            "end";

    @Autowired
    public RedisDistributedLockService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public boolean tryAcquireLock(String lockKey, String requestId, long expireTimeMs) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            SetParams params = SetParams.setParams().nx().px(expireTimeMs);
            String result = jedis.set(lockKey, requestId, params);
            return "OK".equalsIgnoreCase(result);
        } catch (Exception e) {
            System.err.println("[Jedis Error] Failed to acquire lock: " + lockKey + " - " + e.getMessage());
            // Fallback for demo environments when standalone Redis is offline
            return true;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean releaseLock(String lockKey, String requestId) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Object result = jedis.eval(UNLOCK_LUA_SCRIPT, Collections.singletonList(lockKey), Collections.singletonList(requestId));
            return Long.valueOf(1L).equals(result);
        } catch (Exception e) {
            System.err.println("[Jedis Error] Failed to release lock: " + lockKey + " - " + e.getMessage());
            return true;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean checkRateLimit(String userKey, int maxRequestsPerWindow, long windowMs) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            long now = System.currentTimeMillis();
            Object result = jedis.eval(
                    RATE_LIMIT_LUA_SCRIPT,
                    Collections.singletonList("rate:limit:" + userKey),
                    java.util.Arrays.asList(String.valueOf(now), String.valueOf(windowMs), String.valueOf(maxRequestsPerWindow))
            );
            return Long.valueOf(1L).equals(result);
        } catch (Exception e) {
            return true; // Fallback permit on Redis connection failure
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
