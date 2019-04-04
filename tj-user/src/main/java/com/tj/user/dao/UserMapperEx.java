package com.tj.user.dao;

import com.tj.dto.InviteInfoDto;
import com.tj.dto.UserInfoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-11-21 18:41
 **/
public interface UserMapperEx {

    List<InviteInfoDto> getUserInviteInfo(@Param("userId") Integer userId);

    List<UserInfoDto> listUser(@Param("account") String account, @Param("phone") String phone, @Param("inviteCode") String inviteCode, @Param("proxy") Integer proxy);
}
