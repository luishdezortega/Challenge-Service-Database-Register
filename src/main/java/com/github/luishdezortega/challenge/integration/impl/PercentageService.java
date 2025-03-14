package com.github.luishdezortega.challenge.integration.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.luishdezortega.challenge.exception.PercentageUnavailableException;
import com.github.luishdezortega.challenge.integration.IPercentageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PercentageService implements IPercentageService {

    private static final double DEFAULT_PERCENTAGE = 10.0;
    private static final String CACHE_KEY = "percentage";

    private final Cache<String, Double> cache;


    public PercentageService(
            @Value("${cache.percentage.expire-after-minutes}") long expireAfterMinutes,
            @Value("${cache.percentage.max-size}") int maxSize
    ) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(expireAfterMinutes, TimeUnit.MINUTES)
                .maximumSize(maxSize)
                .build();
    }

    @Override
    public double getPercentage() {
        var cachedValue = cache.getIfPresent(CACHE_KEY);
        if (cachedValue != null) {
            log.info("✅ Using cache: {}", cachedValue);
            return cachedValue;
        }
        return fetchAndCachePercentage();
    }

    private double fetchAndCachePercentage() {
        try {
            var percentage = fetchPercentageFromExternalService();
            cache.put(CACHE_KEY, percentage);
            return percentage;
        } catch (Exception e) {
            log.error("❌ Error retrieving percentage from the external service.{}", e.getMessage());
            var cachedValue = cache.getIfPresent(CACHE_KEY);
            if (cachedValue != null) {
                log.warn("⚠️ Using the last cached value due to service failure.");
                return cachedValue;
            }
            throw new PercentageUnavailableException("No cached percentage available and external service failed");
        }
    }

    /**
     * Fetches the percentage value from an external service.
     * If the service call fails, a {@link PercentageUnavailableException} is thrown.
     *
     * @return the percentage value retrieved from the external service
     * @throws PercentageUnavailableException if the external service is unavailable
     */
    public double fetchPercentageFromExternalService() {
        return DEFAULT_PERCENTAGE;
    }
}
