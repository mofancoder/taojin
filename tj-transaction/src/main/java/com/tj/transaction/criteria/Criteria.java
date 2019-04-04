package com.tj.transaction.criteria;

import com.tj.dto.TransactionRequestDto;
import com.tj.util.Results;

/**
 * @program: tj-core
 * @description: 条件过滤类
 * @author: liang.song
 * @create: 2018-11-27-17:40
 **/
public interface Criteria {
    /**
     * 过滤器的名称
     */
    String name();

    /**
     * 是否满足交易请求要求
     *
     * @param requestDto 交易请求
     * @return 满足结果
     */
    Results.Result<Boolean> meetCriteria(TransactionRequestDto requestDto);

}
