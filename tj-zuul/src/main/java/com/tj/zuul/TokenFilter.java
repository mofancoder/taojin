package com.tj.zuul;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.tj.dto.RedisUserInfo;
import com.tj.util.Results;
import com.tj.util.enums.OptStatusEnum;
import com.tj.util.enums.RedisKeys;
import com.tj.util.enums.SysStatusEnum;
import com.tj.util.log.Rlog;
import com.tj.util.properties.PropertiesUtil;
import com.tj.util.redis.CloudRedisService;
import com.tj.zuul.dao.UserInfoMapper;
import com.tj.zuul.domain.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * @program: tj-core
 * @description: token过滤器:<p>
 * 过滤来自前端的请求,增加跨域-拦截请求-过滤出带有token令牌的请求进行权限认证
 * </p>
 * @author: liang.song
 * @create: 2018-11-21 10:57
 **/
@Component
public class TokenFilter extends ZuulFilter {
    @Autowired
    private Rlog rlog;

    private Logger flog = LoggerFactory.getLogger("flog");
    @Autowired
    private CloudRedisService cloudRedisService;
    @Resource
    private UserInfoMapper userInfoMapper;

    private ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(ConcurrentHashMap::new);

    /***
     *
     * 前置过滤
     */
    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    /**
     * 序号越小-过滤优先级越高
     */
    @Override
    public int filterOrder() {
        return 4;
    }

    /**
     * 判断是否应该过滤
     * <p>
     * 过滤的条件就是:请求参数中是否包含了token,并且url并不包含/open
     * </p>
     */
    @Override
    public boolean shouldFilter() {

        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        //crossDomain(request, currentContext.getResponse());
        String uri = request.getRequestURI();
        Map<String, String[]> parameterMap = request.getParameterMap();
        threadLocal.get().put("args", JSON.toJSONString(parameterMap, false));
        if (uri.contains("/open")) {
            return false;
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        //crossDomain(request, currentContext.getResponse());
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            unAuthorized(currentContext);

        }
        String requestURI = request.getRequestURI();
        RedisUserInfo select = null;
        if (requestURI.contains("admin")) {
            select = cloudRedisService.select(RedisKeys.SYS_USER_TOKEN + token, RedisUserInfo.class);
        } else {
            select = cloudRedisService.select(RedisKeys.USER_TOKEN_INFO + token, RedisUserInfo.class);
        }
        if (select == null) {
            return unAuthorized(currentContext);
        }
        Integer userId = select.getUserId();
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        if (userInfo == null) {
            return unAuthorized(currentContext);
        }
        Byte sysStatus = userInfo.getSysStatus();
        Byte optStatus = userInfo.getOptStatus();
        if (sysStatus.intValue() == SysStatusEnum.INVALID0.ordinal() || optStatus.intValue() == OptStatusEnum.LOCKED.getCode()) {
            return unAuthorized(currentContext);
        }
        request.getSession().setAttribute("customer", select);
        threadLocal.get().put("customer", select);
        threadLocal.get().put("userId", select.getUserId());
        threadLocal.get().put("token", token);
        threadLocal.get().put("uri", request.getRequestURI());
        asyncLog();
        return token;
    }

    private Object unAuthorized(RequestContext currentContext) {
        rlog.debug("current request do not contain any token info");
        //如果token为空，则返回给客户端-验证失效
        currentContext.setSendZuulResponse(false);
        currentContext.setResponseStatusCode(HttpStatus.OK.value());//401
        Results.Result accountInvalid = Results.ACCOUNT_INVALID;
        currentContext.getResponse().setContentType("application/json;charset=UTF-8");
        currentContext.setResponseBody(JSON.toJSONString(accountInvalid));//响应体
        return null;
    }


    /**
     * 跨域
     */
    private void crossDomain(HttpServletRequest request, HttpServletResponse response) {
        String crossDomain = PropertiesUtil.getStringValue("cross_domain");
        String remoteHost = request.getRemoteHost().toLowerCase();
        String origin = null;
        if (crossDomain != null) {
            if (crossDomain.equals("*")) {
                origin = "*";
            } else {
                if (crossDomain.contains(remoteHost)) {
                    origin = remoteHost;
                }
            }
        }

        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }


    private void asyncLog() {
        StringBuilder fLogSb = new StringBuilder();
        Map<String, Object> map = threadLocal.get();
        String uri = (String) map.getOrDefault("uri", "");
        String args = (String) map.getOrDefault("args", "");
        String token = (String) map.getOrDefault("token", "");
        Integer userId = (Integer) map.getOrDefault("userId", null);
        fLogSb.
                append("|").append("uri").append(":").append(uri).
                append("|").append("token").append(":").append(token).
                append("|").append("userId").append(":").append(userId == null ? "" : userId).
                append("|").append("args").append(":").append(args);
        flog.debug(fLogSb.toString());
        threadLocal.remove();
    }


}
