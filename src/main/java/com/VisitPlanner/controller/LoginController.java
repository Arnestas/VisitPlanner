package com.VisitPlanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping("/showLoginPage")
    public String showLoginPage(){
        return "login/login";
    }

    @GetMapping("/accessDenied")
    public String showAccessDenied() {
        return "login/access-denied";
    }

    @GetMapping ("/logged")
    public String showLoggedPage(){
        return "login/logged";
    }
}
