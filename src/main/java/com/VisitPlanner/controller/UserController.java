package com.VisitPlanner.controller;

import com.VisitPlanner.entity.User;
import com.VisitPlanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService service;

    @GetMapping("/")
    public List<User> showUsers(){
        System.out.println(service.getAllUsers());
        return service.getAllUsers();
    }

    @PostMapping("/")
    public void addUsers(@RequestBody User user){
        service.save(user);
    }

}