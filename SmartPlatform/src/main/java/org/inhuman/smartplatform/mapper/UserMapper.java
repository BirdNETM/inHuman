package org.inhuman.smartplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.inhuman.smartplatform.pojo.User;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select(value = "select * from user")
    List<User> list();
}
