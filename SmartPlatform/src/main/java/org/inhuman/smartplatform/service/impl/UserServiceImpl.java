package org.inhuman.smartplatform.service.impl;

import org.inhuman.smartplatform.mapper.UserMapper;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> list() {
        return userMapper.list();
    }
}
