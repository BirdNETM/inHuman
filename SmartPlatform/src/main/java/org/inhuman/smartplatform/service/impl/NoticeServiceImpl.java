package org.inhuman.smartplatform.service.impl;

import org.inhuman.smartplatform.mapper.NoticeMapper;
import org.inhuman.smartplatform.pojo.Notice;
import org.inhuman.smartplatform.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public List<Notice> getNoticeByUserId(int id){
        return noticeMapper.getNoticeByUserId(id);
    }

    @Override
    public List<Notice> searchNotices(int id, String searchKey){
        return noticeMapper.searchNotices(id,searchKey);
    }

    @Override
    public List<Notice> getNoticeByNoticeId(int id, int noticeId) {
            noticeMapper.setNoticeRead(id,noticeId);
        return noticeMapper.getNoticeByNoticeId(id,noticeId);
    }

    @Override
    public List<Notice> getNoticeNew(int id) {
        return noticeMapper.getNoticeNew(id);
    }
}
