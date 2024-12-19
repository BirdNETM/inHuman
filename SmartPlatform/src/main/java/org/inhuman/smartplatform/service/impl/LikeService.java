package org.inhuman.smartplatform.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.mapper.LikeMapper;
import org.inhuman.smartplatform.mapper.PostingsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
@Slf4j
public class LikeService implements org.inhuman.smartplatform.service.LikeService {


    @Autowired
    private LikeMapper LikeMapper;
    @Autowired
    private PostingsMapper PostingsMapper;

    @Override
    public void like(int type, int userId, int postingId) {
        if(type == 0) {
            java.util.Date utilDate = Calendar.getInstance().getTime(); // 获取java.util.Date
            Date sqlDate = new Date(utilDate.getTime()); // 转换为java.sql.Date
            LikeMapper.insertLike(userId, postingId, sqlDate);
            PostingsMapper.addTotalLikes(postingId);
        }
        else{
            LikeMapper.deleteLike(userId, postingId);
            PostingsMapper.decreaseTotalLikes(postingId);
        }
    }
}
