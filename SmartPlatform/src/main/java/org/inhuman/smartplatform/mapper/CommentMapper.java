package org.inhuman.smartplatform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.inhuman.smartplatform.pojo.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Select("select homepage.username,comment.* from comment join homepage on homepage.id = comment.senderId where postingId = #{postingId}")
    List<Comment> getComments(int id, int postingId);

    @Insert("""
            insert into comment(postingId, senderId, content) values (#{comment.postingId}, #{id}, #{comment.content})""")
    void setComments(int id, Comment comment);
}
