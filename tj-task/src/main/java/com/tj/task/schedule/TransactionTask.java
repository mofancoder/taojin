package com.tj.task.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tj.dto.PollingData;
import com.tj.dto.PollingResultDto;
import com.tj.task.dao.UserTransactionRecdMapper;
import com.tj.task.domain.UserTransactionRecd;
import com.tj.task.domain.UserTransactionRecdExample;
import com.tj.task.service.TransactionService;
import com.tj.util.FBDSignUtil;
import com.tj.util.SacRecdStatusEnum;
import com.tj.util.TransactionTypeEnum;
import com.tj.util.enums.PlatformTypeEnum;
import com.tj.util.enums.PollingStatusEnum;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @program: tj-core
 * @description: 定时任务轮询订单状态, 进行回调
 * @author: liang.song
 * @create: 2018-12-04-18:19
 **/
@Component
@Slf4j
public class TransactionTask {
    private static final String cron = "0 0/2 * * * *";
    private final TransactionService transactionService;
    @Resource
    private UserTransactionRecdMapper userTransactionRecdMapper;
    private final RestTemplate restTemplate;
    @Value("${transaction.app-id}")
    private String appId;
    @Value("${transaction.app-secret}")
    private String appSecret;
    @Value("${transaction.polling-url}")
    private String pollingUrl;
    @Autowired
    public TransactionTask(TransactionService transactionService, RestTemplate restTemplate) {
        this.transactionService = transactionService;
        this.restTemplate = restTemplate;
    }

    /**
     * 1.查询所有的处理中的订单
     * 2.遍历/批量远程查询订单状态
     * 3.回调callback 函数
     */
    @Scheduled(cron = cron)
    @SchedulerLock(name = "SacTransactionTask", lockAtLeastForString = "${ts.least.time}", lockAtMostForString = "${ts.most.time}")
    public void exec() {
        int curPage = 1;
        Integer pageSize = 20;
        UserTransactionRecdExample example = new UserTransactionRecdExample();
        example.or().andRecdStatusEqualTo(SacRecdStatusEnum.processing.getCode()).andPlatformIn(new ArrayList<Integer>() {
            {
                add(PlatformTypeEnum.alipay.getCode());
                add(PlatformTypeEnum.wechat.getCode());
                add(PlatformTypeEnum.credit.getCode());
            }
        }).andRecdTypeEqualTo(TransactionTypeEnum.charge.getCode());//只轮询充值的订单
        while (true) {
            Page<UserTransactionRecd> info = PageHelper.startPage(curPage, pageSize).doSelectPage(() -> {
                userTransactionRecdMapper.selectByExample(example);
            });
            if (info.isEmpty()) {
                break;
            }
            List<UserTransactionRecd> result = info.getResult();
            for (UserTransactionRecd recd : result) {
                CompletableFuture.runAsync(() -> {
                    //package Param
                    HttpHeaders headers = new HttpHeaders();
                    MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
                    headers.setContentType(type);
                    headers.add("Accept", MediaType.APPLICATION_JSON.toString());
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("company_id", appId);
                    jsonObj.put("company_order_no", recd.getId().toString());
                    jsonObj.put("sign", FBDSignUtil.getSign(jsonObj.getInnerMap(), appSecret));
                    HttpEntity<String> formEntity = new HttpEntity<String>(jsonObj.toString(), headers);
                    log.info("polling request url:{}", pollingUrl);
                    log.info("polling request:" + JSON.toJSONString(jsonObj, false));
                    try {
                        //发起交易
                        ResponseEntity<PollingResultDto> chargeResponse = restTemplate.postForEntity(pollingUrl, formEntity, PollingResultDto.class);
                        log.info("polling response:{}", JSON.toJSONString(chargeResponse, false));
                        PollingResultDto body = chargeResponse.getBody();
                        Integer code = body.getCode();
                        if (code == null || !code.equals(200)) {
                            log.error("invoke polling url get error");
                            return;
                        }
                        PollingData data = body.getData();
                        String companyOrderNo = data.getCompany_order_no();//订单号
                        String errorMsg = data.getError_msg();//错误信息
                        Integer status = data.getStatus();//状态码
                        if (status.equals(PollingStatusEnum.PROCESS.getCode())) {
                            log.warn("order :{} status is process", recd.getId());
                            return;
                        }
                        transactionService.polling(companyOrderNo, status);
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        log.error("invoke schedule task transaction Callback get exception " + e);
                    }
                });
            }
            ;
            curPage++;
        }
    }

}
