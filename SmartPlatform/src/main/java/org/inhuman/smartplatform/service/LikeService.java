package org.inhuman.smartplatform.service;

import org.springframework.stereotype.Service;

@Service
public interface LikeService {
    void like(int type, int userId, int postingId);
}
