package com.tj.user.service;

import com.github.pagehelper.PageInfo;
import com.tj.dto.InviteInfoDto;
import com.tj.dto.RedisUserInfo;
import com.tj.dto.UserInfoDto;
import com.tj.util.Results;

import java.util.List;

/**
 * @program: tj-core
 * @description: 用户服务接口
 * @author: liang.song
 * @create: 2018-11-21 18:13
 **/
public interface UserService {
    /**
     * 用户注册
     *
     * @param account 账号
     * @param password 密码
     * @return
     */
    Results.Result<RedisUserInfo> register(String account, String password, String rePassword, String phone, String area, Integer captcha, String inviteCode);
    Results.Result<RedisUserInfo> registerAdmin(String account, String password, String rePassword, String phone, String area);

    /**
     * 用户登录
     *
     * @param account 账号
     * @param password 密码
     * @return
     */
    Results.Result<RedisUserInfo> login(String account, String password);

    /**
     * 获取验证码
     *
     * @param phone 电话
     * @param type  类型 {@link com.tj.util.enums.CaptchaType}
     * @return
     */
    Results.Result<String> captcha(String area, String phone, Integer type, String msg);

    /**
     * 忘记密码-修改密码
     *
     * @param area       手机号所在的区域
     * @param phone      用户手机号
     * @param captcha    验证码
     * @param password   密码
     * @param rePassword 重复密码
     * @return 修改是否成功
     */
    Results.Result<RedisUserInfo> forgetPassword(String area, String phone, String captcha, String password, String rePassword);

    /**
     * 设置资金密码
     *
     * @param password   资金密码
     * @param rePassword 重复资金密码
     * @return
     */
    Results.Result<Void> fundPassword(String password, String rePassword);

    Results.Result<PageInfo<UserInfoDto>> listUser(String account, String phone, String inviteCode, Integer proxy, Integer curPage, Integer pageSize);

    Results.Result<Void> freezeUser(Integer userId, Integer optStatus);

    Results.Result<Void> userStatus(Integer userId, Integer sysStatus);

    Results.Result<List<InviteInfoDto>> getInviteInfo(Integer userId);

    Results.Result<RedisUserInfo> adminLogin(String account, String password);

    Results.Result<Void> adminLogout(String token);

    Results.Result<Void> userLogout(String token);
}
