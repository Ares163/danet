package com.ares.danet.service;


import com.ares.danet.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RetryServiceTest extends BaseTest{

    @Autowired
    private RetryService retryService;

    @Test
    public void testRetry() {

        retryService.retry();
    }
}
