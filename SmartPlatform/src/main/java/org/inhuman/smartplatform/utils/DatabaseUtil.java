package org.inhuman.smartplatform.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DatabaseUtil {

    private static JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseUtil(JdbcTemplate jdbcTemplate) {
        DatabaseUtil.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据studentCode查询id
     * @param studentCode 学生代码
     * @return 对应的id
     */
    public static Integer getIdByCode(String studentCode) {
        String sql = "SELECT id FROM homepage WHERE studentCode = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{studentCode}, Integer.class);
    }

    public static String getNameByCode(String studentCode) {
        String sql = "SELECT username FROM homepage WHERE studentCode = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{studentCode}, String.class);
    }

    public static String getLessonByHomeworkId(int homeworkId) {
        String sql = "SELECT lessonId FROM homework WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{homeworkId}, String.class);
    }

    public static Integer getLessonByDocId(int homeworkId) {
        String sql = "SELECT lessonId FROM docs WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{homeworkId}, Integer.class);
    }

    public static Integer getLessonByExamId(int homeworkId) {
        String sql = "SELECT lessonId FROM exam WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{homeworkId}, Integer.class);
    }

    public static Boolean getTutorPrivilege(int id,int lessonId, int type) {
        String sql = "SELECT position FROM user WHERE id = ?";
        int position = jdbcTemplate.queryForObject(sql, new Object[]{id}, Integer.class);
        if(position == 2){
            switch(type){
                case 1:
                    sql = "SELECT homework_control FROM tutor_permissions WHERE tutor_id = ? and lesson_id = ?";
                    return jdbcTemplate.queryForObject(sql, new Object[]{id, lessonId}, Boolean.class);
                case 2:
                    sql = "SELECT docs_control FROM tutor_permissions WHERE tutor_id = ? and lesson_id = ?";
                    return jdbcTemplate.queryForObject(sql, new Object[]{id, lessonId}, Boolean.class);
                case 3:
                    sql = "SELECT points_control FROM tutor_permissions WHERE tutor_id = ? and lesson_id = ?";
                    return jdbcTemplate.queryForObject(sql, new Object[]{id, lessonId}, Boolean.class);
                case 4:
                    sql = "SELECT exam_control FROM tutor_permissions WHERE tutor_id = ? and lesson_id = ?";
                    return jdbcTemplate.queryForObject(sql, new Object[]{id, lessonId}, Boolean.class);
            }
        }
        return true;
    }

    /**
     * 执行查询操作
     * @param sql SQL查询语句
     * @return 查询结果列表
     */
    public static List<Map<String, Object>> queryForList(String sql) {
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * 执行更新或插入操作
     * @param sql SQL语句
     * @return 影响的行数
     */
    public static int update(String sql) {
        return jdbcTemplate.update(sql);
    }
}

