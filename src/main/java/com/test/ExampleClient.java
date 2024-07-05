package com.test;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "client-name", url = "http://127.0.0.1:8081", configuration = {OAuth2ClientSecurityConfig.class})
public interface ExampleClient {

    @GetMapping( value = "principal",  consumes = "application/json")
    void getInfo();
}
