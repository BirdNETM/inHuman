package org.inhuman.smartplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.inhuman.smartplatform.pojo.Notice;

import java.util.List;

@Mapper
public interface NoticeMapper {

    @Select("select notice.* from notice join notify where notify.user_id = #{id} and notify.notice_id = notice.id" )
    List<Notice> getNoticeByUserId(int id);


    @Select("SELECT notice.*, notify.state " +
            "FROM notice JOIN notify " +
            "ON notice.id = notify.notice_id " +
            "WHERE notify.user_id = #{id} " +
            "AND notice.title LIKE CONCAT('%', #{searchKey}, '%')")
    List<Notice> searchNotices(int id, String searchKey);


    @Select("SELECT notice.*, notify.state " +
            "FROM notice JOIN notify " +
            "ON notice.id = notify.notice_id " +
            "WHERE notify.user_id = #{id} " +
            "AND notice.id = #{noticeId}")
    List<Notice> getNoticeByNoticeId(int id, int noticeId);


    @Select("SELECT notice.*, notify.state " +
            "FROM notice JOIN notify " +
            "ON notice.id = notify.notice_id " +
            "WHERE notify.user_id = #{id} " +
            "AND notify.state = 0")
    List<Notice> getNoticeNew(int id);

    @Update("UPDATE notify SET state = 1 WHERE user_Id = #{id} AND notice_id = #{noticeId}")
    void setNoticeRead(int id, int noticeId);

}
