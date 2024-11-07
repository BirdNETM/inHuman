package org.inhuman.smartplatform.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Select;

import org.inhuman.smartplatform.pojo.Postings;

@Mapper
public interface PostingsMapper {

    @Insert("insert into postings (id, lessonId,posterId, title, content, time) values (NULL, #{lessonId}, #{id},#{postings.title}, #{postings.content}, current_time)")
    void setPosting(@Param("id") int id, @Param("lessonId") int lessonId, @Param("postings") Postings postings);

    @Insert("insert into postings_pictures (pictureId,postingId,pictureUrl) values (#{pictureId},#{postingId},#{filePath})")
    void insertPostingPicture(int id, int postingId, int pictureId, String filePath);


    @Select("select * from postings where id = #{postingId}")
    Postings getPostingDetailById(int id, int postingId);

    @Select("SELECT postings_pictures.pictureUrl from postings_pictures where postingId = #{postingId} and pictureId = #{pictureId}")
    String getPostingPicturesById(int id, int postingId, int pictureId);

}
