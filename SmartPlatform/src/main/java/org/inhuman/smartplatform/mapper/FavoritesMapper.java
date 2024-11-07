package org.inhuman.smartplatform.mapper;

import org.apache.ibatis.annotations.*;
import org.inhuman.smartplatform.pojo.Postings;

import java.util.List;

@Mapper
public interface FavoritesMapper {

    @Select("select postings.*,favoritestypes.type from postings join favorites on postings.id = favorites.postingsId join favoritestypes on favoritestypes.typeId = favorites.typeId where favorites.userId = #{id} order by favoritestypes.type")
    List<Postings> getFavorites(int id);

    @Insert("insert favorites value (#{id},#{postingId},#{typeId})")
    void addFavorites(int id, int postingId,int typeId);
    
    @Delete("delete from favorites where favorites.userId = #{id} and favorites.postingsId = #{postingId}")
    void deleteFavorites(int id, int postingId);

    @Update("update favorites set favorites.typeId = #{typeId} where favorites.userId = #{id} and favorites.postingsId = #{postingId}")
    void updateFavoritesType(int id, int postingId, String type);

    @Select(" select favoritestypes.type from favoritestypes where favoritestypes.userId = #{id}")
    List<String> getFavoritesTypes(int id);

    @Select("select postings.* from postings join favorites on favorites.postingsId = postings.id join favoritestypes on favoritestypes.typeId = favorites.typeId where favorites.userId = #{id} and favoritestypes.type = #{type}")
    List<Postings> getFavoritesByType(int id, String type);

    @Insert("insert favoritestypes value (null,#{id},#{type})")
    void addFavoritesType(int id, String type);
    
    @Delete("delete from favoritestypes where favoritestypes.userId = #{id} and favoritestypes.type = #{type}")
    void deleteFavoritesType(int id, String type);

    @Delete("delete from favorites where favorites.userId = #{id} and favorites.typeId = #{typeId}")
    void deleteFavoritesByType(int id, int typeId);

    @Select("select count(*) from favoritestypes where favoritestypes.userId = #{id} and favoritestypes.type = #{type}")
    int getTypeCount(int id, String type);

    @Update("update favoritesTypes set type = #{newType} where userId = #{id} and type = #{type}")
    void updateFavoritesTypeName(int id, String type, String newType);

    @Select("select favoritestypes.typeId from favoritestypes where userId  = #{id} and type = #{type}")
    int getTypeId(int id, String type);
}
