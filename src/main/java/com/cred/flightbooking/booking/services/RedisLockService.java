package com.cred.flightbooking.booking.services;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final RedissonClient redissonClient;

    public boolean acquireLocks(List<String> lockKeys) {
        // MultiLock to atomically acquire all locks
        RLock[] locks = new RLock[lockKeys.size()];
        for (int i = 0; i < lockKeys.size(); i++) {
            locks[i] = redissonClient.getLock(lockKeys.get(i));
        }

        RLock multiLock = redissonClient.getMultiLock(locks);
        try {
            // Try to acquire all locks with wait time 5s, lease time 60s
            return multiLock.tryLock(5, 60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void releaseLocks(List<String> lockKeys) {
        RLock[] locks = new RLock[lockKeys.size()];
        for (int i = 0; i < lockKeys.size(); i++) {
            locks[i] = redissonClient.getLock(lockKeys.get(i));
        }

        RLock multiLock = redissonClient.getMultiLock(locks);
        multiLock.unlock();
    }
}
