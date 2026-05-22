package com.example.ratelimiter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Request allowed";
    }
    @GetMapping("/login")
    public String login() {
        return "login endpoint";
    }

    @GetMapping("/search")
    public String search() {
        return "search endpoint";
    }
}