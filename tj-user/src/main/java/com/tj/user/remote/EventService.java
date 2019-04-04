package com.tj.user.remote;

import com.tj.util.Results;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @program: tj-core
 * @description: 赛事feign<p>
 *     1.调用方式:feign,ribbon
 *     区别: feign 声明式模板
 *           ribbon: restTemplate/httpClient -->url,参数列表
 * </p>
 * @author: liang.song
 * @create: 2018-11-21 15:08
 **/
@FeignClient("tj-event/event")
public interface EventService {
    /**
     * 这里有个坑:<p>
     * 1.feign不支持@GetMapping @PostMapping 等这样的复合注解 必须使用@RequestMapping(method=XXXX)这样的
     * 2.feign 调用:当参数是复杂对象的时候,即使 生命是GET方法，feign依然会使用post方法传参
     * 3.feign的参数列表必须加上@RequestParam 等这样的注解
     * 4.如果碰到2,3需要传递复杂对象 请使用post方法,如果必须要使用GET方法,请拆分请求对象/使用@RequestParam Map<> 这种形式
     * </p>
     *
     * @param name
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    Results.Result<String> getEvent(@RequestParam("name") String name);
}
