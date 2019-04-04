package com.tj.util.aspect;

import com.alibaba.fastjson.JSON;
import com.tj.dto.RedisUserInfo;
import com.tj.util.Results;
import com.tj.util.enums.RedisKeys;
import com.tj.util.redis.CloudRedisService;
import lombok.Data;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-11-21 13:55
 **/
@Component
@Aspect
@Data
public class CommonLogAspect {
    private Logger fLog = LoggerFactory.getLogger("flog");
    private Logger rLog = LoggerFactory.getLogger("rlog");
    private Logger sqlLog = LoggerFactory.getLogger("sqlLog");
    private ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(ConcurrentHashMap::new);
    @Autowired
    private CloudRedisService cloudRedisService;

    @Pointcut("execution(public * com.*.*.controller.*.*(..))")
    public void execAct() {
    }

    @Before("execAct()")
    public void before(JoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String uri = request.getRequestURI();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String methodName = methodSignature.getMethod().getName();
        if (this.threadLocal.get() == null) {
            this.threadLocal.set(new HashMap<>());
        }
        String token = request.getHeader("token");
        if (!StringUtils.isEmpty(token)) {
            this.threadLocal.get().put("token", token);
            RedisUserInfo user = null;
            if (uri.contains("admin")) {
                user = cloudRedisService.select(RedisKeys.SYS_USER_TOKEN + token, RedisUserInfo.class);

            } else {
                user = cloudRedisService.select(RedisKeys.USER_TOKEN_INFO + token, RedisUserInfo.class);
            }
            if (user != null) {
                this.threadLocal.get().put("userId", String.valueOf(user.getUserId()));
                this.threadLocal.get().put("user", user);
            }

        }
        this.threadLocal.get().put("startTime", LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        this.threadLocal.get().put("uri", uri);
        this.threadLocal.get().put("args", JSON.toJSONString(parameterMap, false));
        this.threadLocal.get().put("message", new StringBuilder());
        this.threadLocal.get().put("methodName", methodName);
    }

    @AfterReturning(
            value = "execAct()",
            returning = "ret"
    )
    public void after(Object ret) {
        this.timeConsume();
        this.response(ret);
        this.asyncLog();
    }

    @AfterThrowing(
            value = "execAct()",
            throwing = "throwing"
    )
    public void afterThrowing(Throwable throwing) {
        rLog.error("exception:", throwing);
        this.threadLocal.get().put("exception", throwing.getMessage() == null ? "" : throwing.getMessage());
        this.timeConsume();
        this.asyncLog();
    }


    private void asyncLog() {
        StringBuilder fLogSb = new StringBuilder();
        Map<String, Object> map = this.threadLocal.get();
        String timeConsume = (String) map.getOrDefault("timeConsume", "");
        String uri = (String) map.getOrDefault("uri", "");
        String args = (String) map.getOrDefault("args", "");
        String code = (String) map.getOrDefault("code", "");
        String retMsg = (String) map.getOrDefault("retMsg", "");
        String exception = (String) map.getOrDefault("exception", "");
        String methodName = (String) map.getOrDefault("methodName", "");
        String token = (String) map.getOrDefault("token", "");
        String userId = (String) map.getOrDefault("userId", "");
        fLogSb.append("|").append("timeout").append(":").append(timeConsume).append("|").append("uri").append(":").append(uri).append("|").append("token").append(":").append(token).append("|").append("userId").append(":").append(userId).append("|").append("methodName").append(":").append(methodName).append("|").append("args").append(":").append(args).append("|").append("code").append(":").append(code).append("|").append("retMsg").append(":").append(retMsg).append("|").append("exception").append(":").append(exception);
        this.fLog.info(fLogSb.toString());
        this.threadLocal.remove();
    }

    private void timeConsume() {
        long endTime = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        this.threadLocal.get().put("endTime", endTime);
        this.threadLocal.get().putIfAbsent("startTime", endTime);
        Long startTime = (Long) this.threadLocal.get().get("startTime");
        long timeConsume = endTime - startTime;
        this.threadLocal.get().put("timeConsume", Long.valueOf(timeConsume).toString());
    }

    private void response(Object ret) {
        if (ret instanceof Results.Result) {
            Results.Result result = (Results.Result) ret;
            int code = result.getCode();
            String msg = result.getMsg();
            this.threadLocal.get().putIfAbsent("code", Integer.valueOf(code).toString());
            this.threadLocal.get().putIfAbsent("retMsg", msg == null ? "" : msg);
        }
    }

    public RedisUserInfo currentUser() {
        return (RedisUserInfo) threadLocal.get().get("user");
    }
}
