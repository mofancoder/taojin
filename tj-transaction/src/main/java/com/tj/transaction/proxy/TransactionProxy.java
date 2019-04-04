package com.tj.transaction.proxy;

import com.tj.dto.TransactionRequestDto;
import com.tj.transaction.criteria.Criteria;
import com.tj.transaction.service.TransactionService;
import com.tj.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @program: tj-core
 * @description: 充值代理
 * @author: liang.song
 * @create: 2018-11-27-17:16
 **/
@Component
public class TransactionProxy {
    /**
     * 维护的目标业务类:这里指交易服务类
     */
    private final TransactionService transactionService;
    /**
     * 交易条件类:用于过滤交易
     */
    private List<Criteria> criterion;

    @Autowired
    TransactionProxy(List<Criteria> criterion, TransactionService transactionService) {
        this.criterion = criterion;
        this.transactionService = transactionService;
    }

    /**
     * 采用jdk动态代理 生成代理类
     *
     * @return
     */
    public Object getProxyInstance() {
        return Proxy.newProxyInstance(
                transactionService.getClass().getClassLoader(),
                transactionService.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    Results.Result<Boolean> validRequest = validRequest((TransactionRequestDto) args[0]);
                    if (validRequest.getCode() != Results.SUCCESS.getCode()) {
                        return validRequest;
                    }
                    return method.invoke(transactionService, args);
                });
    }

    /**
     * 校验交易参数是否正确
     *
     * @param requestDto 交易请求
     * @return 是否通过校验
     */
    private Results.Result<Boolean> validRequest(TransactionRequestDto requestDto) {
        for (Criteria criteria : criterion) {
            Results.Result<Boolean> meetCriteria = criteria.meetCriteria(requestDto);
            if (meetCriteria.getCode() != Results.SUCCESS.getCode()) {
                return meetCriteria;
            }
        }
        return new Results.Result<>(Results.SUCCESS, null);
    }

}
