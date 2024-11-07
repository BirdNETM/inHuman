package org.inhuman.smartplatform.service.impl;

import org.inhuman.smartplatform.mapper.LoginMapper;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    LoginMapper loginMapper;

    @Override
    public User LogIn(User user) {
        User user1 = loginMapper.LogIn(user);
        System.out.println("拿到用户" + user1);
        return user1;
    }
}
