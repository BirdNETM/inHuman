package org.inhuman.smartplatform.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

@Mapper
public interface LikeMapper {
    @Insert("insert into liked values(#{userId}, #{postingId}, #{likeTime})")
    void insertLike(int userId, int postingId, Date likeTime);

    @Delete("delete from liked where userId = #{userId} and postingId = #{postingId}")
    void deleteLike(int userId, int postingId);
}
