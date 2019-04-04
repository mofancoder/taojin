package com.tj.transaction.aop;

import com.tj.dto.TransactionRequestDto;
import com.tj.transaction.criteria.Criteria;
import com.tj.util.A.SacException;
import com.tj.util.Results;
import lombok.Data;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-11-21 13:55
 **/
@Component
@Aspect
@Data
public class TransactionAspect {

    /**
     * 交易条件类:用于过滤交易
     */
    private List<Criteria> criterion;

    @Autowired
    TransactionAspect(List<Criteria> criterion) {
        this.criterion = criterion;
    }

    @Pointcut("execution(public * com.tj.transaction.service.TransactionService.charge())")
    public void execAct() {
    }

    @Before("execAct()")
    public void before(JoinPoint joinPoint) {
        Results.Result<Boolean> validRequest = validRequest((TransactionRequestDto) joinPoint.getArgs()[0]);
        if (validRequest.getCode() != Results.SUCCESS.getCode()) {
            throw new SacException(validRequest.getMsg());
        }
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
