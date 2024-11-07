package org.inhuman.smartplatform.service;

import org.inhuman.smartplatform.pojo.User;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {
    /**
     * check user log in
     *
     * @param user
     * @return
     */
    User LogIn(User user);
}
