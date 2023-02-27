package com.example.beside.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class WelcomeController {

    @GetMapping("/")
    @ResponseBody
    public RedirectView hello() {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("http://localhost/swagger-ui/index.html");

        return redirectView;
    }
}
