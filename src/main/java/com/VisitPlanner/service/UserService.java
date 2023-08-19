package com.VisitPlanner.service;

import com.VisitPlanner.entity.User;
import com.VisitPlanner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository repository;

    public List<User> getAllUsers(){
        return repository.findAll();
    }

    public void save(User user) {
        repository.save(user);
    }


    public User findByUserName(String userName) {
        return repository.findByName(userName);

    }
}
