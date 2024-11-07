package org.inhuman.smartplatform.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.mapper.FavoritesMapper;
import org.inhuman.smartplatform.pojo.FavoritiesType;
import org.inhuman.smartplatform.pojo.Postings;
import org.inhuman.smartplatform.service.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@Slf4j
public class FavoritesServiceImpl implements FavoritesService {

    @Autowired
    FavoritesMapper favoritesMapper;


    @Override
    public List<Postings> getFavorites(int id) {
        return favoritesMapper.getFavorites(id);
    }

    @Override
    public void addFavorites(int id, int postingId, String type) {
        if (type == null || type.isEmpty()) {
            type = "默认收藏夹";
        }
        if(favoritesMapper.getTypeCount(id,type) ==0) {
            favoritesMapper.addFavoritesType(id,type);
        }
        int typeId = favoritesMapper.getTypeId(id,type);
        favoritesMapper.addFavorites(id,postingId,typeId);
    }

    @Override
    public void deleteFavorites(int id, int postingId) {
        favoritesMapper.deleteFavorites(id,postingId);
    }

    @Override
    public void updateFavoritesType(int id, int postingId, String type) {
        deleteFavorites(id,postingId);
        addFavorites(id,postingId,type);
    }

    @Override
    public List<FavoritiesType> getFavoritesOrderByType(int id) {

        List<String> types = favoritesMapper.getFavoritesTypes(id);
        log.info(String.valueOf(types));
        List<FavoritiesType> results = new ArrayList<FavoritiesType>();
        for (String type : types) {
            FavoritiesType ft = new FavoritiesType();
            ft.setType(type);
            List<Postings> postings = favoritesMapper.getFavoritesByType(id,type);
            ft.setPostingsList(postings);
            results.add(ft);
        }
        return results;

    }

    @Override
    public void addFavoritesType(int id, String type) {
        favoritesMapper.addFavoritesType(id,type);
    }

    @Override
    public void deleteFavoritesType(int id, String type) {
        int typeId = favoritesMapper.getTypeId(id,type);
        favoritesMapper.deleteFavoritesType(id,type);
        favoritesMapper.deleteFavoritesByType(id,typeId);
    }

    @Override
    public void updateFavoritesTypeName(int id, String type, String newType) {
        favoritesMapper.updateFavoritesTypeName(id,type,newType);
    }
}
