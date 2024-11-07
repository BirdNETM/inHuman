package org.inhuman.smartplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.inhuman.smartplatform.pojo.User;

@Mapper
public interface LoginMapper {

    @Select(value="select * from user where username = #{userName} and password = #{password}")
    User LogIn(User user);
}
