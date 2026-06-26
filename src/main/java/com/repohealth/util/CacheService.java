package com.repohealth.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 简单本地缓存，使用 ConcurrentHashMap，缓存时间 10 分钟。
 * 用于减少重复请求 GitHub API。
 */
@Component
public class CacheService {

    private static final long CACHE_TTL_MS = 10 * 60 * 1000L; // 10 分钟

    private final Map<String, CacheEntry<?>> cache = new ConcurrentHashMap<>();

    /**
     * 从缓存获取值，如果不存在或已过期，则通过 supplier 获取并缓存。
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Supplier<T> supplier) {
        CacheEntry<?> entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return (T) entry.getValue();
        }
        T value = supplier.get();
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + CACHE_TTL_MS));
        return value;
    }

    /**
     * 从缓存获取值，如果不存在或已过期返回 null。
     */
    @SuppressWarnings("unchecked")
    public <T> T getIfPresent(String key) {
        CacheEntry<?> entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return (T) entry.getValue();
        }
        return null;
    }

    /**
     * 放入缓存。
     */
    public <T> void put(String key, T value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + CACHE_TTL_MS));
    }

    /**
     * 清除指定缓存。
     */
    public void evict(String key) {
        cache.remove(key);
    }

    /**
     * 清除所有缓存。
     */
    public void clear() {
        cache.clear();
    }

    /**
     * 清除所有已过期的缓存条目。
     */
    public void cleanup() {
        long now = System.currentTimeMillis();
        cache.values().removeIf(entry -> entry.isExpired(now));
    }

    /**
     * 获取当前缓存大小。
     */
    public int size() {
        return cache.size();
    }

    private static class CacheEntry<T> {
        private final T value;
        private final long expireAt;

        CacheEntry(T value, long expireAt) {
            this.value = value;
            this.expireAt = expireAt;
        }

        T getValue() {
            return value;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expireAt;
        }

        boolean isExpired(long now) {
            return now > expireAt;
        }
    }
}