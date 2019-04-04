package com.tj.user.controller;

import com.github.pagehelper.PageInfo;
import com.tj.dto.InviteInfoDto;
import com.tj.dto.RedisUserInfo;
import com.tj.dto.UserInfoDto;
import com.tj.user.remote.EventService;
import com.tj.user.service.UserService;
import com.tj.util.CountryArea;
import com.tj.util.Results;
import com.tj.util.aspect.CommonLogAspect;
import com.tj.util.redis.CloudRedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-11-21 11:58
 **/
@RestController
@RequestMapping("/user")
@Api(tags = "user", description = "用户")
public class UserController {
    @Autowired
    private CloudRedisService cloudRedisService;
    @Autowired
    private EventService eventService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserService userService;
    @Autowired
    private CommonLogAspect commonLogAspect;
    @Autowired
    private CountryArea countryArea;

    @ApiOperation(value = "测试")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "用户名", required = true, dataType = "string", paramType = "query"),
    })
    @PostMapping("/open/hello")
    public Results.Result<RedisUserInfo> hello(String name) {
        return new Results.Result<>(Results.SUCCESS, RedisUserInfo.builder().build());
    }
    @ApiOperation(value ="用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name="account" ,value = "账号",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name="password" ,value = "密码",required = true,dataType = "string",paramType = "query")
    })
    @GetMapping("/open/login")
    public Results.Result<RedisUserInfo> login(@RequestParam("account") String account,
                                               @RequestParam("password") String password) {

        return userService.login(account, password);
    }

    @ApiOperation(value = "用户注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账号", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码(MD5)", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "rePassword", value = "重复密码(MD5)", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "电话", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "area", value = "区号", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "captcha", value = "验证码", required = true, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "inviteCode", value = "邀请码", required = false, dataType = "string", paramType = "query"),
    })
    @GetMapping("/open/register")
    public Results.Result<RedisUserInfo> register(@RequestParam("account") String account,
                                                  @RequestParam("password") String password,
                                                  @RequestParam("rePassword") String rePassword,
                                                  @RequestParam("phone") String phone,
                                                  @RequestParam("area") String area,
                                                  @RequestParam("captcha") Integer captcha,
                                                  @RequestParam(value = "inviteCode", required = false) String inviteCode) {
        return userService.register(account, password, rePassword, phone, area, captcha, inviteCode);
    }
    @ApiOperation(value = "管理员初始化")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账号", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码(MD5)", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "rePassword", value = "重复密码(MD5)", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "电话", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "area", value = "区号", required = true, dataType = "string", paramType = "query"),
    })
    @GetMapping("/open/registerAdmin")
    public Results.Result<RedisUserInfo> registerAdmin(@RequestParam("account") String account,
                                                  @RequestParam("password") String password,
                                                  @RequestParam("rePassword") String rePassword,
                                                  @RequestParam("phone") String phone,
                                                  @RequestParam("area") String area,
                                                  @RequestParam("captcha") Integer captcha) {
        return userService.registerAdmin(account, password, rePassword, phone, area);
    }


    @ApiOperation(value = "登录-获取验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "area", value = "区号", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "手机号", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "验证码用途(0:注册 1:忘记密码)", required = true, dataType = "long", paramType = "query", defaultValue = "0"),
    })
    @GetMapping("/open/captcha")
    public Results.Result<String> captcha(@RequestParam("area") String area, @RequestParam(value = "phone", required = true) String phone, @RequestParam(value = "type", defaultValue = "0") Integer type) {
        //TODO  这里的签名 需要改成正确的
        String message = messageSource.getMessage("【superSAC】您的验证码是{0}，{1}分钟内有效。若非本人操作，请忽略本短信。", null, LocaleContextHolder.getLocale());
        return userService.captcha(area, phone, type, message);
    }

    @ApiOperation(value = "手机号所在区域")
    @GetMapping("/open/phone/area")
    public Results.Result<List<Map>> phoneArea() {
        List<Map> maps = countryArea.ChinaPhonePrefix();
        return new Results.Result<>(Results.SUCCESS, maps);
    }


    @ApiOperation(value = "忘记密码(同安全设置登录密码)")
    @GetMapping("/open/forget/password")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "area", value = "区号", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "手机号", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "登录密码(MD5)", required = true, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "rePassword", value = "重复登录密码(MD5)", required = false, dataType = "long", paramType = "query"),
    })
    public Results.Result<RedisUserInfo> forgetPassword(@RequestParam String area,
                                                        @RequestParam String phone,
                                                        @Pattern(regexp = "^[0-9]{6}$", message = "请输入正确的验证码") @RequestParam String captcha,
                                                        @RequestParam String password,
                                                        @RequestParam String rePassword) {
        return userService.forgetPassword(area, phone, captcha, password, rePassword);

    }


    @ApiOperation(value = "设置资金密码")
    @GetMapping("/fund/password")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "用户令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "password", value = "资金密码(MD5)", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "rePassword", value = "重复资金密码(MD5)", required = true, dataType = "long", paramType = "query"),
    })
    public Results.Result<Void> fundPassword(
                                             @RequestParam("password") String password,
                                             @RequestParam("rePassword") String rePassword) {
        return userService.fundPassword(password, rePassword);
    }

    @ApiOperation(value = "管理员查看用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "account", value = "用户账号", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "用户手机号", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "inviteCode", value = "用户邀请码", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "proxy", value = "是否是代理", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = false, dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = false, dataType = "long", paramType = "query", defaultValue = "20"),
    })
    @GetMapping("/admin/list")
    public Results.Result<PageInfo<UserInfoDto>> listUser(@RequestParam(value = "account", required = false) String account,
                                                          @RequestParam(value = "phone", required = false) String phone,
                                                          @RequestParam(value = "inviteCode", required = false) String inviteCode,
                                                          @RequestParam(value = "proxy", required = false) Integer proxy,
                                                          @RequestParam(value = "curPage", required = false, defaultValue = "1") Integer curPage,
                                                          @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        return userService.listUser(account, phone, inviteCode, proxy, curPage, pageSize);
    }

    @ApiOperation(value = "管理员冻结用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "optStatus", value = "冻结状态(0:非冻结 1:冻结)", required = true, dataType = "long", paramType = "query", defaultValue = "1"),
    })
    @GetMapping("/admin/freeze")
    public Results.Result<Void> freezeUser(@RequestParam("userId") Integer userId, @RequestParam("optStatus") Integer optStatus) {
        return userService.freezeUser(userId, optStatus);
    }

    @ApiOperation(value = "管理员删除/启用用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "sysStatus", value = "删除状态(0:删除 1:正常)", required = true, dataType = "long", paramType = "query", defaultValue = "1"),
    })
    @GetMapping("/admin/userStatus")
    public Results.Result<Void> userStatus(@RequestParam("userId") Integer userId, @RequestParam("sysStatus") Integer sysStatus) {
        return userService.userStatus(userId, sysStatus);
    }


    @ApiOperation(value = "管理员查看邀请关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "userId", value = "用户Id", required = false, dataType = "long", paramType = "query"),
    })
    @GetMapping("/admin/inviteInfo")
    public Results.Result<List<InviteInfoDto>> inviteInfo(@RequestParam(value = "userId", required = false) Integer userId) {
        return userService.getInviteInfo(userId);
    }

    @ApiOperation(value = "管理员登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账号", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping("/open/admin/login")
    public Results.Result<RedisUserInfo> adminLogin(@RequestParam("account") String account,
                                                    @RequestParam("password") String password) {

        return userService.adminLogin(account, password);
    }

    @ApiOperation(value = "管理员登出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "query"),
    })
    @GetMapping("/admin/logout")
    public Results.Result<Void> adminLogout() {
        RedisUserInfo redisUserInfo = commonLogAspect.currentUser();
        return userService.adminLogout(redisUserInfo.getToken());
    }

    @ApiOperation(value = "用户登出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "用户令牌", required = true, dataType = "string", paramType = "query"),
    })
    @GetMapping("/logout")
    public Results.Result<Void> userLogout() {
        RedisUserInfo redisUserInfo = commonLogAspect.currentUser();
        return userService.userLogout(redisUserInfo.getToken());
    }
}
