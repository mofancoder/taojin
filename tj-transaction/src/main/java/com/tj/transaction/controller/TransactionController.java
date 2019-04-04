package com.tj.transaction.controller;

import com.tj.dto.*;
import com.tj.transaction.proxy.TransactionProxy;
import com.tj.transaction.service.TransactionService;
import com.tj.util.A.FBDException;
import com.tj.util.Results;
import com.tj.util.aliyun.OssUtil;
import com.tj.util.sign.SignUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @program: tj-core
 * @description: 交易控制器
 * @author: liang.song
 * @create: 2018-11-27-16:59
 **/
@RestController
@RequestMapping("/transaction")
@Validated
@Api(tags = "交易模块")
public class TransactionController {

    private final TransactionProxy transactionProxy;

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionProxy transactionProxy, TransactionService transactionService) {
        this.transactionProxy = transactionProxy;
        this.transactionService = transactionService;
    }

    @ApiOperation(value = "提交充值请求")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "用户令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "requestDto", value = "交易请求", required = true, dataType = "TransactionRequestDto", paramType = "body"),
    })
    @PostMapping("/charge")
    public Results.Result<TransactionResultDto> charge(@RequestBody TransactionRequestDto requestDto) {

        return transactionService.charge(requestDto);
    }

    @ApiOperation(value = "支付回调")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "callbackDto", value = "CallbackDto", required = true, dataType = "CallbackDto", paramType = "body"),
    })
    @PostMapping("/open/callback")
    public CallbackReturnDto transactionCallback(@RequestBody CallbackDto callbackDto) {
        try {
            return transactionService.transactionCallback(callbackDto);
        } catch (Exception e) {
            throw new FBDException(e.getLocalizedMessage());
        }

    }

    @ApiOperation(value = "提交提现请求")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "用户令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "requestDto", value = "交易请求", required = true, dataType = "TransactionRequestDto", paramType = "body"),
    })
    @PostMapping("/withdraw")
    public Results.Result<TransactionResultDto> withdraw(@RequestBody TransactionRequestDto requestDto) {

        return transactionService.withdraw(requestDto);
    }

    @ApiOperation(value = "用户钱包信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "用户令牌", required = true, dataType = "string", paramType = "header"),
    })
    @GetMapping("/user/balance")
    public Results.Result<UserBalanceInfoDto> userBalance() {

        return transactionService.userBalance();
    }

    @ApiOperation(value = "用户充值提现记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "用户令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "type", value = "交易类型(1:充值 2:提现)", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "dateType", value = "日期类型(1:近一周 2:近一个月 3:近一年,4:全部)", required = false, defaultValue = "1", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "platform", value = "交易平台(1:支付宝 2:微信 3:银行卡,4:线下支付宝 5线下微信 6线下银行卡)", required = false,  dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "recdStatus", value = "0-失败,1-成功,2-处理中(正在与第三方交互/等待结果返回),3-交易超时", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, defaultValue = "1", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, defaultValue = "20", dataType = "long", paramType = "query"),
    })
    @GetMapping("/list")
    public Results.Result<UserTransactionSumDto> userTransactionList(
                                                                                @RequestParam(value = "type", required = false) Integer type,
                                                                                @RequestParam(value = "dateType", required = false) Integer dateType,
                                                                                @RequestParam(value = "platform", required = false) Integer platform,
                                                                                @RequestParam(value = "recdStatus", required = false) Integer recdStatus,
                                                                                @RequestParam(value = "curPage", defaultValue = "1") Integer curPage,
                                                                                @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return transactionService.list(type, dateType,platform,recdStatus, curPage, pageSize);
    }

    @ApiOperation(value = "管理员查看交易记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "type", value = "交易类型(1:充值 2:提现)", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "recdStatus", value = "订单状态(0-失败,1-成功,2-处理中(正在与第三方交互/等待结果返回),3-交易超时)", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "auditStatus", value = "审核状态(0：审核失败 1:审核成功  2：审核中)", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "username", value = "用户名", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "手机号", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "transactionId", value = "订单号", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, dataType = "long", paramType = "query"),
    })
    @GetMapping("/admin/list")
    public Results.Result<AdminTransactionSumDto> adminRecdList(
                                                                           @RequestParam(value = "type", required = false) Integer type,
                                                                           @RequestParam(value = "recdStatus", required = false) Integer recdStatus,
                                                                           @RequestParam(value = "auditStatus", required = false) Integer auditStatus,
                                                                           @RequestParam(value = "username", required = false) String username,
                                                                           @RequestParam(value = "phone", required = false) String phone,
                                                                           @RequestParam(value = "transactionId", required = false) String transactionId,
                                                                           @RequestParam(value = "startTime", required = false) Long startTime,
                                                                           @RequestParam(value = "endTime", required = false) Long endTime,
                                                                           @RequestParam(value = "curPage", required = false, defaultValue = "1") Integer curPage,
                                                                           @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        Date beginTime = startTime == null ? null : new Date(startTime);
        Date lastTime = endTime == null ? null : new Date(endTime);
        return transactionService.adminRecdList(type, recdStatus, auditStatus, username, phone, transactionId, beginTime, lastTime, curPage, pageSize);
    }
//
//    @ApiOperation(value = "管理员审核大额提现")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "token", value = "用户令牌", required = true, dataType = "string", paramType = "header"),
//            @ApiImplicitParam(name = "transactionId", value = "订单Id", required = true, dataType = "string", paramType = "query"),
//            @ApiImplicitParam(name = "auditStatus", value = "审核状态(0：审核失败 1:审核成功  2：审核中)", required = true, dataType = "long", paramType = "query"),
//            @ApiImplicitParam(name = "auditRemark", value = "审核备注", required = false, dataType = "string", paramType = "query"),
//    })
//    @PostMapping("/admin/audit")
//    @Deprecated
//    public Results.Result<Void> auditWithdraw(
//                                              @RequestParam("transactionId") String transactionId,
//                                              @RequestParam("auditStatus") Integer auditStatus,
//                                              @RequestParam(value = "auditRemark", required = false) String auditRemark) {
//        return transactionService.auditWithdraw(Long.valueOf(transactionId), auditStatus, auditRemark);
//    }

    @ApiOperation(value = "用户充值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "用户令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "requestDto", value = "线下订单", required = true, dataType = "OfflineTransactionRequestDto", paramType = "body"),

    })
    @PostMapping("/offline")
    public Results.Result<Void> offlineTransaction(@RequestBody OfflineTransactionRequestDto requestDto) {
        return transactionService.createOfflineTransaction(requestDto);
    }

    @ApiOperation(value = "管理员审核充值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "用户令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "request", value = "线下订单", required = true, dataType = "AdminAuditOfflineTransaction", paramType = "body"),

    })
    @PostMapping("/admin/offline/audit")
    public Results.Result<Void> auditOfflineTransaction(@RequestBody AdminAuditOfflineTransaction request) {
        return transactionService.auditOfflineTransaction(request);
    }

    @ApiOperation(value = "用户提交提现申请")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "用户令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "request", value = "提现申请", required = true, dataType = "UserWithdrawRequest", paramType = "body"),

    })
    @PostMapping("/withdraw/submit")
    public Results.Result<Void> submitWithdraw(@RequestBody UserWithdrawRequest request) {
        return transactionService.submitWithdraw(request);
    }

    @ApiOperation(value = "管理员审核提现")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "request", value = "提现申请", required = true, dataType = "AuditWithdrawRequest", paramType = "body"),

    })
    @PostMapping("/admin/withdraw/confirm")
    public Results.Result<Void> adminConfirmWithdraw(@RequestBody AuditWithdrawRequest request) {
        return transactionService.adminConfirmWithdraw(request);
    }

    @ApiOperation(value = "管理员上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "管理员令牌", required = true, dataType = "string", paramType = "header"),

    })
    @PostMapping("/admin/upload/snapshot")
    public Results.Result<String> adminUploadSnapshot(@RequestParam MultipartFile file) throws IOException {
        if (file == null) {
            return Results.PARAMETER_INCORRENT;
        }
        InputStream inputStream = file.getInputStream();
        String fileMD5 = SignUtil.getFileMD5(inputStream);
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String newName = fileMD5 + "." + extension;
        File fileToSave = new File(newName);
        FileCopyUtils.copy(file.getBytes(), fileToSave);
        String ossUrl = OssUtil.getOSSUrl(fileToSave, "FBD");
        return new Results.Result<>(Results.SUCCESS, ossUrl);
    }

    @ApiOperation(value = "上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "用户令牌", required = true, dataType = "string", paramType = "header"),

    })
    @PostMapping("/upload/snapshot")
    public Results.Result<String> uploadSnapshot(@RequestParam MultipartFile file) throws IOException {
        return this.adminUploadSnapshot(file);
    }

    @ApiOperation(value = "轮询")
    @GetMapping("/open/polling")
    public void polling(String transactionId, Integer status) {
        transactionService.polling(transactionId, status);
    }
}
