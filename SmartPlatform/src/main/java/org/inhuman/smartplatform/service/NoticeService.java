package org.inhuman.smartplatform.service;

import org.inhuman.smartplatform.pojo.Notice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NoticeService {
    List<Notice> getNoticeByUserId(int id);

    List<Notice> searchNotices(int id, String searchKey);

    List<Notice> getNoticeByNoticeId(int id, int noticeId);

    List<Notice> getNoticeNew(int id);
}
