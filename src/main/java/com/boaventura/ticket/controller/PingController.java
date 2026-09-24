package com.boaventura.ticket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/api/ticket")
    public String ping() {
        return "pong";
    }

}
