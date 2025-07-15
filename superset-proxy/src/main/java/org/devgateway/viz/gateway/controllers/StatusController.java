package org.devgateway.viz.gateway.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class StatusController {
    @GetMapping("/status")
    public Map<String, String> status() {
        return Map.of("status", "UP");
    }

}

