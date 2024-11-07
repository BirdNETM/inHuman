package org.inhuman.smartplatform.controller;


import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.pojo.Result;
import org.inhuman.smartplatform.pojo.User;
import org.inhuman.smartplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public Result list(){
        log.info("list all users");

        List<User> userList = userService.list();
        return Result.success(userList);
    }
}
