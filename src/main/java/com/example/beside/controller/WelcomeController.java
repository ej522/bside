package com.example.beside.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

    @GetMapping("/")
    public String hello() {
        return "redirect:/swagger-ui/index.html";
    }
}
