package com.gao.service;

import com.gao.mapper.UserMapper;
import com.gao.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User login(String username, String password) {
        return userMapper.login(username, password);
    }
}
