package com.ams.bankapp2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String showIndexPage() {
        return "index"; // index.html Thymeleaf template
    }
}

