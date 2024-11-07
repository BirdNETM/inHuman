package org.inhuman.smartplatform.service;

import org.inhuman.smartplatform.pojo.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    /**
     * get all users data
     *
     * @return
     */
    List<User> list();
}
