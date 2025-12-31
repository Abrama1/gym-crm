package com.example.workload.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/api/workload/ping")
    public String ping() {
        return "ok";
    }
}
