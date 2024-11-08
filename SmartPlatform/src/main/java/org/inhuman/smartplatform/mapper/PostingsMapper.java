package org.inhuman.smartplatform.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.inhuman.smartplatform.pojo.Postings;

@Mapper
public interface PostingsMapper {

    @Insert("insert into postings (id, lessonId,posterId, title, content, time) values (NULL, #{lessonId}, #{id},#{postings.title}, #{postings.content}, current_time)")
    void setPosting(@Param("id") int id, @Param("lessonId") int lessonId, @Param("postings") Postings postings);

    @Insert("insert into postings_pictures (pictureId,postingId,pictureUrl) values (#{pictureId},#{postingId},#{filePath})")
    void insertPostingPicture(int id, int postingId, int pictureId, String filePath);
}
