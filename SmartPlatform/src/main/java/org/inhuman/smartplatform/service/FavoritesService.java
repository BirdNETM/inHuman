package org.inhuman.smartplatform.service;


import org.inhuman.smartplatform.pojo.Postings;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FavoritesService {
    List<Postings> getFavorites(int id);

    void addFavorites(int id, int postingId, String type);

    void deleteFavorites(int id, int postingId);

    void updateFavoritesType(int id, int postingId, String type);
}
