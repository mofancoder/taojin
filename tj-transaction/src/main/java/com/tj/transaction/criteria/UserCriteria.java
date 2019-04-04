package com.tj.transaction.criteria;

import com.tj.dto.RedisUserInfo;
import com.tj.dto.TransactionRequestDto;
import com.tj.transaction.dao.UserInfoMapper;
import com.tj.transaction.domain.UserInfo;
import com.tj.util.Results;
import com.tj.util.aspect.CommonLogAspect;
import com.tj.util.enums.OptStatusEnum;
import com.tj.util.enums.SysStatusEnum;
import com.tj.util.log.Rlog;
import com.tj.util.redis.CloudRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: tj-core
 * @description: 用户过滤
 * @author: liang.song
 * @create: 2018-11-27-17:59
 **/
@Component
public class UserCriteria implements Criteria {

    private final CloudRedisService cloudRedisService;

    private final Rlog rlog;

    @Resource
    private UserInfoMapper userInfoMapper;
    @Autowired
    private CommonLogAspect commonLogAspect;
    @Autowired
    public UserCriteria(CloudRedisService cloudRedisService, Rlog rlog) {
        this.cloudRedisService = cloudRedisService;
        this.rlog = rlog;
    }

    @Override
    public String name() {
        return "用户过滤";
    }

    @Override
    public Results.Result<Boolean> meetCriteria(TransactionRequestDto requestDto) {
        RedisUserInfo redisUser = commonLogAspect.currentUser();
        if (redisUser == null) {
            rlog.error("user already expired or not existed in redis");
            return new Results.Result<>(Results.ACCOUNT_INVALID, false);
        }
        Integer userId = redisUser.getUserId();//用户ID
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        if (userInfo == null) {
            return new Results.Result<>(Results.ACCOUNT_NOT_EXIST, false);
        }
        Byte optStatus = userInfo.getOptStatus();//冻结状态
        Byte sysStatus = userInfo.getSysStatus();//有效状态
        if (optStatus.intValue() == OptStatusEnum.LOCKED.getCode() || sysStatus.intValue() == SysStatusEnum.INVALID0.ordinal()) {
            return new Results.Result<>(Results.ACCOUNT_INVALID, false);
        }
        return new Results.Result<>(Results.SUCCESS, true);
    }
}
