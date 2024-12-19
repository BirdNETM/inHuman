package org.inhuman.smartplatform.mapper;


import org.apache.ibatis.annotations.*;
import org.inhuman.smartplatform.pojo.Postings;

import java.util.List;

@Mapper
public interface PostingsMapper {

    @Insert("insert into postings (id, lessonId,posterId, title, content, time) values (NULL, #{lessonId}, #{id},#{postings.title}, #{postings.content}, current_time)")
    void setPosting(@Param("id") int id, @Param("lessonId") int lessonId, @Param("postings") Postings postings);

    @Insert("insert into postings_pictures (pictureId,postingId,pictureUrl) values (#{pictureId},#{postingId},#{filePath})")
    void insertPostingPicture(int id, int postingId, int pictureId, String filePath);

    @Select("select postings.*, user.userName from postings join user on user.id = postings.posterId where postings.id = #{postingId}")
    Postings getPostingDetailById(int id, int postingId);

    @Select("SELECT postings_pictures.pictureUrl from postings_pictures where postingId = #{postingId} and pictureId = #{pictureId}")
    String getPostingPicturesById(int id, int postingId, int pictureId);

    @Update("update postings set totalLikes = totalLikes + 1 where id = #{id}")
    void addTotalLikes(int id);

    @Update("update postings set totalLikes = totalLikes - 1 where id = #{id}")
    void decreaseTotalLikes(int id);

    @Select("select * from postings where title like CONCAT('%', #{topic}, '%')")
    List<Postings> getPostingsByTopic(String topic);


    @Select("select last_insert_id()")
    int getPostingId();

    @Select("select postings.* ,homepage.username from postings join homepage on postings.posterId = homepage.id where lessonId = #{lessonId}")
    List<Postings> getPostings(int id, int lessonId);

    @Select("SELECT COUNT(*) FROM favorites where userId = #{id} and postingsId = #{postingId}")
    int whetherFavorites(int id, int postingId);

    @Select("SELECT COUNT(*) FROM liked where userId = #{id} and postingId = #{postingId}")
    int whetherLiked(int id, int postingId);
}
