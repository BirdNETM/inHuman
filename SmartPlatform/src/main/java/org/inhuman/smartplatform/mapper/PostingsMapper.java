package org.inhuman.smartplatform.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.inhuman.smartplatform.pojo.Postings;

import java.util.List;

@Mapper
public interface PostingsMapper {

    @Insert("insert into postings (lessonId,posterId, title, content, pictureCounts) values (#{lessonId}, #{id}, #{postings.title}, #{postings.content},#{postings.pictureCounts} )")
    @Options(useGeneratedKeys = true, keyProperty = "postings.id")
    void setPosting(int id, int lessonId, Postings postings);

    @Select("select last_insert_id()")
    int getPostingId();

    @Insert("insert into postings_pictures (pictureId,postingId,pictureUrl) values (#{pictureId},#{postingId},#{filePath})")
    void insertPostingPicture(int id, int postingId, int pictureId, String filePath);

    @Select("select homepage.username, postings.* from postings join homepage on postings.posterId = homepage.id where postings.id = #{postingId}")
    Postings getPostingDetailById(int id, int postingId);

    @Select("SELECT postings_pictures.pictureUrl from postings_pictures where postingId = #{postingId} and pictureId = #{pictureId}")
    String getPostingPicturesById(int id, int postingId, int pictureId);

    @Select("select postings.* ,homepage.username from postings join homepage on postings.posterId = homepage.id where lessonId = #{lessonId}")
    List<Postings> getPostings(int id, int lessonId);
}
