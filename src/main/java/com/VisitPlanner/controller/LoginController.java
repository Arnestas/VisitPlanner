package com.VisitPlanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/showLoginPage")
    public String showLoginPage(){
        return "/login/login";
    }

    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "/login/access-denied";
    }

    @GetMapping ("/logged")
    public String showLoggedPage(){
        return "/login/logged";
    }
}
