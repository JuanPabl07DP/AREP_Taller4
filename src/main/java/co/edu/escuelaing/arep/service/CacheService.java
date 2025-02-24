package co.edu.escuelaing.arep.service;

import co.edu.escuelaing.arep.model.Conversiones;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;

public class CacheService {
    private static final long CACHE_DURATION = 3600; // 1 hora en segundos
    private final Map<String, CacheEntry> cache;

    public CacheService() {
        this.cache = new ConcurrentHashMap<>();
    }

    public void put(String key, Conversiones conversion) {
        cache.put(key, new CacheEntry(conversion, Instant.now()));
    }

    public Conversiones get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null && !isExpired(entry)) {
            return entry.conversion();
        }
        cache.remove(key);
        return null;
    }

    private boolean isExpired(CacheEntry entry) {
        return Instant.now().getEpochSecond() - entry.timestamp().getEpochSecond() > CACHE_DURATION;
    }

    private record CacheEntry(Conversiones conversion, Instant timestamp) {}

    public String generateKey(String fromCurrency, String toCurrency) {
        return fromCurrency + "-" + toCurrency;
    }
}