package com.interswitch.verveguardfraudservice.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimiterService {
    private ConcurrentHashMap<String, AtomicInteger> dataStore = new ConcurrentHashMap<>();

    public boolean isAllowed(String ipAddress) {
        if  (dataStore.containsKey(ipAddress)) {
            AtomicInteger counter = dataStore.get(ipAddress);
            if (counter.get() >= 5) {
                return false;
            }
            counter.incrementAndGet();
        } else {
            dataStore.put(ipAddress, new AtomicInteger(1));
        }
        return true;
    }

    @Scheduled(fixedRate = 60000)
    void clearDataStoreAfterOneMinute() {
        dataStore.clear();
    }
}
