package com.ares.danet.service;

import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class RetryService {

    @Retryable(value= RetryException.class, maxAttempts = 3, backoff = @Backoff(delay = 100L, multiplier = 1))
    public void retry() {
        System.out.println("retry...");
        throw new RetryException("xxx");
    }

    @Recover
    public void recover(RetryException e) {
        System.out.println("retryed...");
        e.printStackTrace();
    }
}
