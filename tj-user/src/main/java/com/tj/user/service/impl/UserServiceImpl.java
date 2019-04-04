package com.tj.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tj.dto.InviteInfoDto;
import com.tj.dto.RedisUserInfo;
import com.tj.dto.UserInfoDto;
import com.tj.user.dao.*;
import com.tj.user.domain.*;
import com.tj.user.service.UserService;
import com.tj.util.CountryArea;
import com.tj.util.GraphValidCode;
import com.tj.util.Results;
import com.tj.util.aspect.CommonLogAspect;
import com.tj.util.captcha.UcpaasSendMsg;
import com.tj.util.enums.*;
import com.tj.util.log.Rlog;
import com.tj.util.redis.CloudRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @program: tj-core
 * @description: 用户服务
 * @author: liang.song
 * @create: 2018-11-21 18:44
 **/
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserAdminMapper userAdminMapper;
    ThreadLocalRandom random = ThreadLocalRandom.current();
    @Autowired
    private Rlog rlog;
    @Autowired
    private CloudRedisService cloudRedisService;
    @Autowired
    private CountryArea countryArea;
    @Resource
    private RegisterInviteRecdMapper registerInviteRecdMapper;
    @Autowired
    private CommonLogAspect commonLogAspect;
    @Value("${register.captcha.times}")
    private Integer times;
    @Value("${register.captcha.timeout}")
    private Integer timeout;
    @Value("${register.captcha.times-count}")
    private Integer timesCount;
    @Value("${register.timeout}")
    private Integer loginTimeout;
    @Resource
    private UserBalanceInfoMapper userBalanceInfoMapper;
    @Resource
    private UserMapperEx userMapperEx;
    /**
     * 用户注册
     * <p>
     * 1.校验用户账号是否合法
     * 1.1 用户账号是否重复
     * 重复->提示用户名重复
     * 不重复->2
     * 2.校验用户密码是否相等
     * 不相等->提示密码错误
     * 相等->3
     * 3.校验验证码是否有效
     * 失效->提示验证码失效
     * 有效->4
     * 4.校验邀请码是否有效
     * 根据邀请码获取父辈
     * 父辈为空->邀请码无效
     * 父辈存在->5
     * 5.插入用户
     * 6.插入邀请关系
     * 7. 5&6是一个事务
     * </p>
     *
     * @param account    账号
     * @param password   密码
     * @param rePassword 重复密码
     * @param phone      手机号
     * @param area       区域
     * @param captcha    验证码
     * @return redis缓存的用户
     */
    @Override
    @Transactional
    public Results.Result<RedisUserInfo> register(String account, String password, String rePassword, String phone, String area, Integer captcha, String inviteCode) {
        //校验账号合法性
        if (StringUtils.isEmpty(account)) {
            return Results.PARAMETER_INCORRENT;
        }
        UserInfoExample userInfoExample = new UserInfoExample();
        userInfoExample.or().andAccountEqualTo(account);
        userInfoExample.or().andPhoneEqualTo(phone);
        List<UserInfo> list = userInfoMapper.selectByExample(userInfoExample);
        if (!list.isEmpty()) {
            rlog.error("register account is already existed:{}", account);
            return Results.ACCOUNT_EXIST;
        }
        //校验密码
        if (!password.equals(rePassword)) {
            return Results.PASSWORD_WRONG;
        }
        //校验验证码是否有效
        Integer redisCaptcha = cloudRedisService.select(RedisKeys.USER_CAPTCHA_REGISTER + phone, Integer.class);
        if (redisCaptcha == null) {
            rlog.error("register captcha is expired");
            return Results.CAPTCHA_INVALID;
        }
        if (!redisCaptcha.equals(captcha)) {
            rlog.error("register captcha is not equal:{}->{}", redisCaptcha, captcha);
            return Results.CAPTCHA_INCORRENT;
        }
        Integer parentId = null;
        //校验邀请码
        if (!StringUtils.isEmpty(inviteCode)) {
            userInfoExample.clear();
            userInfoExample.or().andInviteCodeEqualTo(inviteCode);
            List<UserInfo> infos = userInfoMapper.selectByExample(userInfoExample);
            if (infos.isEmpty()) {
                rlog.error("register inviteCode does not existed:{}", inviteCode);
                return new Results.Result<>(Results.Result.ACCOUNT_NOT_EXIST, "邀请码错误");
            }
            UserInfo parent = infos.get(0);
            parentId = parent.getUserId();
        }
        return new Results.Result<>(Results.SUCCESS, insertUser(account, password, area, phone, inviteCode, parentId));
    }

    public Results.Result<RedisUserInfo> registerAdmin(String account, String password, String rePassword, String phone, String area) {
        //校验账号合法性
        if (StringUtils.isEmpty(account)) {
            return Results.PARAMETER_INCORRENT;
        }
        UserAdminExample userAdminExample = new UserAdminExample();
        userAdminExample.or().andAccountEqualTo(account);
        userAdminExample.or().andPhoneEqualTo(phone);
        List<UserAdmin> list = userAdminMapper.selectByExample(userAdminExample);
        if (!list.isEmpty()) {
            rlog.error("register account is already existed:{}", account);
            return Results.ACCOUNT_EXIST;
        }
        //校验密码
        if (!password.equals(rePassword)) {
            return Results.PASSWORD_WRONG;
        }
        //插入用户
        UserAdmin userAdmin = new UserAdmin();
        userAdmin.setAccount(account);
        userAdmin.setEmail(null);
        userAdmin.setIconUrl(null);
        userAdmin.setInviteCode(null);
        userAdmin.setLastLoginTime(new Date());
        userAdmin.setLoginPwd(password);
        //根据手机号获取用户的区域国度
        String nation = countryArea.getNation(area);
        userAdmin.setNationality(nation);
        userAdmin.setNickName(null);
        userAdmin.setOptReason(null);
        userAdmin.setOptStatus(OptStatusEnum.NO_LOCKED.getCode().byteValue());
        userAdmin.setPhone(phone);
        userAdmin.setPhoneAreaCode(area);
        userAdmin.setProxy(ProxyEnum.admin.getCode());
        userAdmin.setRegistTime(new Date());
        userAdmin.setSysStatus(Integer.valueOf(SysStatusEnum.VALID1.ordinal()).byteValue());
        userAdmin.setUpdateTime(new Date());
        userAdminMapper.insertSelective(userAdmin);

        return new Results.Result<>(Results.SUCCESS, null);
    }

    @Override
    public Results.Result<RedisUserInfo> login(String account, String password) {
        //校验账号合法性
        if (StringUtils.isEmpty(account)) {
            return Results.PARAMETER_INCORRENT;
        }
        //添加用户类型限制
        List<Integer> userList = new ArrayList<>();
        userList.add(ProxyEnum.not_proxy.getCode());
        userList.add(ProxyEnum.proxy.getCode());
        UserInfoExample userInfoExample = new UserInfoExample();
        userInfoExample.or().andAccountEqualTo(account).andProxyIn(userList);
        List<UserInfo> list = userInfoMapper.selectByExample(userInfoExample);
        if (list.isEmpty()) {
            rlog.error("Registered account does not exist:{}", account);
            return Results.ACCOUNT_NOT_EXIST;
        }
        UserInfo userInfo = list.get(0);
        String pwd = userInfo.getLoginPwd();
        int sys_status=userInfo.getSysStatus();
        int opt_status=userInfo.getOptStatus();

        //校验密码
        if (!password.equals(pwd)) {
            return Results.PASSWORD_WRONG;
        }
        if(sys_status==0){
            rlog.error("The account has expired:{}", account);
            return Results.ACCOUNT_INVALID;
        }
        if(opt_status==1){
            rlog.error("The account has been frozen:{}", account);
            return Results.ACCOUNT_IS_PULL_THE_BLACK;
        }
        Integer integer = userInfo.getUserId();
        UserInfo info=new UserInfo();
        info.setUserId(integer);
        info.setLastLoginTime(new Date());
        userInfoMapper.updateByPrimaryKeySelective(info);
        RedisUserInfo redisUserInfo = saveUserInfoRedis(integer, ProxyEnum.not_proxy.getCode());
        return new Results.Result<>(Results.SUCCESS, redisUserInfo);
    }

    private RedisUserInfo insertUser(String account, String password, String area, String phone, String inviteCode, Integer parentId) {
        //插入用户
        UserInfo userInfo = new UserInfo();
        userInfo.setAccount(account);
        userInfo.setEmail(null);
        userInfo.setIconUrl(null);
        userInfo.setInviteCode(null);
        userInfo.setLastLoginTime(new Date());
        userInfo.setLoginPwd(password);
        //根据手机号获取用户的区域国度
        String nation = countryArea.getNation(area);
        userInfo.setNationality(nation);
        userInfo.setNickName(null);
        userInfo.setOptReason(null);
        userInfo.setOptStatus(OptStatusEnum.NO_LOCKED.getCode().byteValue());
        userInfo.setPhone(phone);
        userInfo.setPhoneAreaCode(area);
        userInfo.setProxy(ProxyEnum.not_proxy.getCode());
        userInfo.setRegistTime(new Date());
        userInfo.setSysStatus(Integer.valueOf(SysStatusEnum.VALID1.ordinal()).byteValue());
        userInfo.setUpdateTime(new Date());
        userInfoMapper.insertSelective(userInfo);
        //更新用户的邀请码
        Integer userId = userInfo.getUserId();
        String userInviteCode = GraphValidCode.baseString(userId, 6).toLowerCase();
        UserInfo info = new UserInfo();
        info.setInviteCode(userInviteCode);
        info.setUserId(userId);
        userInfoMapper.updateByPrimaryKeySelective(info);
        //插入用户邀请关系
        if (parentId != null) {
            RegisterInviteRecd inviteRecd = new RegisterInviteRecd();
            inviteRecd.setCreateTime(new Date());
            inviteRecd.setInviteCode(inviteCode);
            RegisterInviteRecdExample example = new RegisterInviteRecdExample();
            example.or().andInvitedUserIdEqualTo(parentId);
            List<RegisterInviteRecd> registerInviteRecds = registerInviteRecdMapper.selectByExampleWithBLOBs(example);
            String ancestorIds = "";
            if (registerInviteRecds.isEmpty()) {
                rlog.warn("the parent does not have any ancestors:{}", parentId);
            } else {
                ancestorIds = registerInviteRecds.get(0).getInvitedAncestorIds() == null ? "" : registerInviteRecds.get(0).getInvitedAncestorIds();
            }
            inviteRecd.setInvitedAncestorIds(ancestorIds + "," + parentId);
            inviteRecd.setInvitedUserId(userId);
            inviteRecd.setInviteUserId(parentId);
            inviteRecd.setUpdateTime(new Date());
            registerInviteRecdMapper.insertSelective(inviteRecd);
        }
        UserBalanceInfo balanceInfo = new UserBalanceInfo();
        balanceInfo.setUserId(userId);
        balanceInfo.setAmount(BigDecimal.ZERO);
        balanceInfo.setFreazonAmount(BigDecimal.ZERO);
        balanceInfo.setCreateTime(new Date());
        userBalanceInfoMapper.insertSelective(balanceInfo);
        //保存用户信息到redis中
        return saveUserInfoRedis(userId, ProxyEnum.not_proxy.getCode());
    }

    private RedisUserInfo saveUserInfoRedis(Integer userId, Integer type) {
        String token = UUID.randomUUID().toString().replace("-", "");
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        if (userInfo == null) {
            rlog.error("user:{} does not existed", userId);
            throw new RuntimeException("user" + userId + "does not existed in ");
        }
        RedisUserInfo build = RedisUserInfo.builder()
                .email(userInfo.getEmail())
                .iconUrl(userInfo.getIconUrl())
                .inviteCode(userInfo.getInviteCode())
                .lastLoginTime(userInfo.getLastLoginTime())
                .nationality(userInfo.getNationality())
                .nickName(userInfo.getNickName())
                .phone(userInfo.getPhone())
                .phoneAreaCode(userInfo.getPhoneAreaCode())
                .proxy(userInfo.getProxy())
                .registTime(userInfo.getRegistTime())
                .sysStatus(userInfo.getSysStatus())
                .token(token)
                .updateTime(userInfo.getUpdateTime())
                .userId(userInfo.getUserId())
                .build();
        switch (ProxyEnum.codeOf(type)) {
            case admin:
                cloudRedisService.save(RedisKeys.SYS_USER_TOKEN + token, build, loginTimeout * 24 * 60 * 60L);
                break;
            case not_proxy:
            case proxy:
                //检索用户的旧token
                String oldToken = cloudRedisService.select(RedisKeys.USER_PHONE_TOKEN + userInfo.getPhone(), String.class);
                if (oldToken != null) {
                    //删除旧的token
                    cloudRedisService.delete(RedisKeys.USER_TOKEN_INFO + oldToken);
                }
                //登入
                cloudRedisService.save(RedisKeys.USER_TOKEN_INFO + token, build, loginTimeout * 24 * 60 * 60L);
                //在线
                cloudRedisService.save(RedisKeys.USER_PHONE_TOKEN + userInfo.getPhone(), token, loginTimeout * 24 * 60 * 60L);
                break;
        }
        return build;
    }


    /**
     * 获取验证码
     * 0.校验该手机号是否获取验证码次数超过了今天限制
     * 0.1 从redis中检索手机号24小时内接收验证码次数
     * 0.2 如果超过次数->无法获得验证码
     * 0.3 没有超过次数->开始 步骤1
     * 1.根据手机号从redis中检索未过期的验证码
     * 1.1 存在验证码->云片异步发送短信->直接返回
     * 1.2 不存在->创建新的验证码->保存在redis中,设置失效时间->云片异步发送短信->返回验证码
     *
     * @param phone 手机号
     * @return 验证码
     */
    @Override
    public Results.Result<String> captcha(String area, String phone, Integer type, String msg) {
        //确保区号格式
        if(!area.trim().substring(0,1).equals("+")){
            area = "+"+area;
        }
        Integer count = cloudRedisService.select(RedisKeys.USER_CAPTCHA_TIMES + phone, Integer.class);
        if (count == null || count < times) {
            String redisKey = RedisKeys.USER_CAPTCHA_REGISTER + phone;
            switch (CaptchaType.codeOf(type)) {
                case register:
                    redisKey = RedisKeys.USER_CAPTCHA_REGISTER + phone;
                    break;
                case forgetPassword:
                    redisKey = RedisKeys.USER_CAPTCHA_FORGET_PWD + phone;
                    break;
            }
            Integer redisCaptcha = cloudRedisService.select(redisKey, Integer.class);
            if (redisCaptcha != null) {
                rlog.info("redis exists phone" + phone + "captcha:" + redisCaptcha);
                return sendCaptcha(area, phone, msg, count, String.valueOf(redisCaptcha), redisCaptcha);
            }
            rlog.info("redis does not exits phone" + phone + "captcha,now going renew one!");
            int captcha = random.nextInt(100000, 999999);
            //save captcha in redis
            boolean save = cloudRedisService.save(redisKey, captcha, timeout * 60L);
            if (save) {
                return sendCaptcha(area, phone, msg, count, String.valueOf(captcha), captcha);
            }
        } else {
            rlog.warn("phone:" + phone + "get register captcha times already overflow!");
            return new Results.Result<>(Results.Result.SEND_FAIL, "您获取验证码次数已经超过限制,请稍后重试!", null);
        }
        return new Results.Result<>(Results.Result.SEND_FAIL, "发送验证码失败,请稍后重试!", null);
    }

    /**
     * 忘记密码
     * <p>
     * 1.判断两次密码是否一致
     * 不一致:返回密码错误
     * 一致:->2
     * 2.校验验证码是否有效
     * 无效: 提示验证码失效
     * 有效: 3
     * 3.根据手机号查找数据库中是否存在此用户
     * 不存在:提示用户不存在
     * 存在:4
     * 4.更新用户密码
     * 5.保存用户到redis中
     * </p>
     *
     * @param area       手机号所在的区域
     * @param phone      用户手机号
     * @param captcha    验证码
     * @param password   密码
     * @param rePassword 重复密码
     * @return 用户信息
     */
    @Override
    public Results.Result<RedisUserInfo> forgetPassword(String area, String phone, String captcha, String password, String rePassword) {
        //判断密码是否一致
        /*if (!password.equals(rePassword)) {
            return new Results.Result<>(Results.PASSWORD_WRONG, null);
        }*/
        //判断验证码是否有效
        Integer forgetPasswordCaptcha = cloudRedisService.select(RedisKeys.USER_CAPTCHA_FORGET_PWD + phone, Integer.class);
        if (forgetPasswordCaptcha == null) {
            return new Results.Result<>(Results.CAPTCHA_INVALID, null);
        }
        //判断用户合法
        UserInfoExample example = new UserInfoExample();
        example.or().andPhoneEqualTo(phone);
        List<UserInfo> userInfos = userInfoMapper.selectByExample(example);
        if (userInfos.isEmpty()) {
            return new Results.Result<>(Results.ACCOUNT_NOT_EXIST, null);
        }
        Integer userId = userInfos.get(0).getUserId();
        //更新密码
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setLoginPwd(password);
        userInfoMapper.updateByPrimaryKeySelective(userInfo);
        //保存用户到redis中
        RedisUserInfo redisUserInfo = saveUserInfoRedis(userId, ProxyEnum.not_proxy.getCode());
        return new Results.Result<>(Results.SUCCESS, redisUserInfo);
    }

    /**
     * 设置资金密码
     *
     * @param password   资金密码
     * @param rePassword 重复资金密码
     * @return 设置是否成功
     */
    @Override
    public Results.Result<Void> fundPassword(String password, String rePassword) {
        //校验密码
        if (!password.equals(rePassword)) {
            return new Results.Result<>(Results.PASSWORD_WRONG, null);
        }
        //校验用户
        RedisUserInfo userInfo = commonLogAspect.currentUser();
        Integer userId = userInfo.getUserId();
        UserInfo dbUser = userInfoMapper.selectByPrimaryKey(userId);
        if (dbUser == null) {
            return new Results.Result<>(Results.ACCOUNT_NOT_EXIST, null);
        }
        //更新密码
        UserInfo record = new UserInfo();
        record.setUserId(userId);
        record.setFundPassword(password);
        userInfoMapper.updateByPrimaryKeySelective(record);
        return new Results.Result<>(Results.SUCCESS, null);
    }

    @Override
    public Results.Result<PageInfo<UserInfoDto>> listUser(String account, String phone, String inviteCode, Integer proxy, Integer curPage, Integer pageSize) {

        PageInfo<UserInfoDto> pageInfo = PageHelper.startPage(curPage, pageSize).doSelectPageInfo(() -> {
            userMapperEx.listUser(account, phone, inviteCode, proxy);
        });

        List<UserInfoDto> userInfos = pageInfo.getList();
        if (userInfos.isEmpty()) {
            return new Results.Result<>(Results.SUCCESS, null);
        }
        return new Results.Result<>(Results.SUCCESS, pageInfo);
    }

    @Override
    public Results.Result<Void> freezeUser(Integer userId, Integer optStatus) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setOptStatus(OptStatusEnum.codeOf(optStatus).getCode().byteValue());
        userInfoMapper.updateByPrimaryKeySelective(userInfo);
        return new Results.Result<>(Results.Result.SUCCESS, null);
    }

    @Override
    public Results.Result<Void> userStatus(Integer userId, Integer sysStatus) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setSysStatus(sysStatus.byteValue());
        userInfoMapper.updateByPrimaryKeySelective(userInfo);
        return new Results.Result<>(Results.Result.SUCCESS, null);
    }

    @Override
    public Results.Result<List<InviteInfoDto>> getInviteInfo(Integer userId) {
        List<InviteInfoDto> userInviteInfo = userMapperEx.getUserInviteInfo(userId);
        if (userInviteInfo.isEmpty()) {
            return new Results.Result<>(Results.SUCCESS, null);
        }
        return new Results.Result<>(Results.SUCCESS, userInviteInfo);
    }

    @Override
    public Results.Result<RedisUserInfo> adminLogin(String account, String password) {
        //校验账号合法性
        if (StringUtils.isEmpty(account)) {
            return Results.PARAMETER_INCORRENT;
        }
        UserInfoExample userInfoExample = new UserInfoExample();
        userInfoExample.or().andAccountEqualTo(account).andProxyEqualTo(ProxyEnum.admin.getCode());
        List<UserInfo> list = userInfoMapper.selectByExample(userInfoExample);
        if (list.isEmpty()) {
            rlog.error("Registered account does not exist:{}", account);
            return Results.ACCOUNT_NOT_EXIST;
        }
        UserInfo userInfo = list.get(0);
        String pwd = userInfo.getLoginPwd();
        int sys_status = userInfo.getSysStatus();
        int opt_status = userInfo.getOptStatus();

        //校验密码
        if (!password.equals(pwd)) {
            return Results.PASSWORD_WRONG;
        }
        if (sys_status == 0) {
            rlog.error("The account has expired:{}", account);
            return Results.ACCOUNT_INVALID;
        }
        if (opt_status == 1) {
            rlog.error("The account has been frozen:{}", account);
            return Results.ACCOUNT_IS_PULL_THE_BLACK;
        }
        Integer integer = userInfo.getUserId();
        UserInfo info = new UserInfo();
        info.setUserId(integer);
        info.setLastLoginTime(new Date());
        userInfoMapper.updateByPrimaryKeySelective(info);
        RedisUserInfo redisUserInfo = saveUserInfoRedis(integer, ProxyEnum.admin.getCode());
        return new Results.Result<>(Results.SUCCESS, redisUserInfo);

    }

    @Override
    public Results.Result<Void> adminLogout(String token) {
        cloudRedisService.delete(RedisKeys.SYS_USER_TOKEN + token);
        return Results.SUCCESS;
    }

    @Override
    public Results.Result<Void> userLogout(String token) {
        RedisUserInfo info = cloudRedisService.select(RedisKeys.USER_TOKEN_INFO + token, RedisUserInfo.class);
        if (info == null) {
            return new Results.Result<>(Results.Result.SUCCESS, null);
        }
        String phone = info.getPhone();
        cloudRedisService.delete(RedisKeys.USER_PHONE_TOKEN + phone);
        cloudRedisService.delete(RedisKeys.USER_TOKEN_INFO + token);
        return new Results.Result<>(Results.SUCCESS, null);
    }

    private Results.Result<String> sendCaptcha(String area, String phone, String msg, Integer count, String s, int captcha) {
        CompletableFuture.runAsync(() -> {
            //TODO 这里需要改成使用淘金网的账号密码 以及签名模板
//            int send = YunPianSendMsg.sendSuperSacCaptcha(s, area + phone, timeout, msg);
            //淘金网的账号密码 以及签名模板
            int send = UcpaasSendMsg.sendSuperSacCaptcha(s, area + phone, timeout * 60, msg);
            rlog.info("send register captcha to " + phone + " result:" + send);

        });
        updatePhoneCaptchaTimes(count, phone);
        //返回帶驗證碼的信息到前台
        // return new Results.Result<>(Results.SUCCESS, s);
        //仅返回状态到前台
        return new Results.Result<>(Results.SUCCESS, null);
    }

    private void updatePhoneCaptchaTimes(Integer count, String phone) {
        if (count == null) {
            cloudRedisService.save(RedisKeys.USER_CAPTCHA_TIMES + phone, 0, timesCount * 60 * 60L);
        } else {
            count++;
            Long ttl = cloudRedisService.ttl(RedisKeys.USER_CAPTCHA_TIMES + phone);
            cloudRedisService.updateOutTime(RedisKeys.USER_CAPTCHA_TIMES + phone, count, ttl);
        }
    }

}
