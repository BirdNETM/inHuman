package org.inhuman.smartplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.inhuman.smartplatform.pojo.HomePage;

@Mapper
public interface HomePageMapper {

    @Select("select homepage.* from homepage where homepage.id = #{id}")
    HomePage getHomePage(int id);

    @Select("select homepage.avatarUrl from homepage where homepage.id = #{id}")
    String getHomePageAvatarAsBase64(int id);

    @Update("update homepage set homepage.username = #{homePage.userName},homepage.bio = #{homePage.bio},homepage.gender = #{homePage.gender},homepage.privacy = #{homePage.privacy} where homepage.id = #{id}")
    void updateHomePage(int id, HomePage homePage);

    @Update("update homepage set homepage.avatarUrl = #{url} where homepage.id = #{id}")
    void updateHomePageAvatar(int id, String url);
}
