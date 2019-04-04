package com.tj.util.enums;

import com.tj.util.SacRecdStatusEnum;
import lombok.Getter;

/**
 * Created by ldh on 2018-02-06.
 */
@Getter
public enum RecdStatusEnum {
    //交易状态,0-提取失败,1-提取成功(定期去区块链轮询),2-待审核,3-审核不通过,4-审核通过,5-处理中
    // 需要审核的订单(比如提币): 审核中(钱已经冻结)->已支付->成功
    //                                             ->审核不通过(回滚,解冻金额)
    FAIL(0, SacRecdStatusEnum.fail),//失败 0
    SUCCESS(1, SacRecdStatusEnum.success),//成功 1
    AUDITING(2, SacRecdStatusEnum.processing),//审核中 2
    AUDIT_FAIL(3, SacRecdStatusEnum.fail),//审核不通过 3
    AUDIT_SUC(4, SacRecdStatusEnum.processing),//审核通过,废弃不用,使用 SUCCESS  4
    PROCESSING(5, SacRecdStatusEnum.processing),//处理中-5
    PAYED(6, SacRecdStatusEnum.success),//已经支付，代发货 6
    DISPATCHED(7, SacRecdStatusEnum.success),//已经发货  上链用到 7
    WAITING(8, SacRecdStatusEnum.processing),// 等待支付 8
    TIMEOUT(9, SacRecdStatusEnum.timeout);//超时未支付 9

    private Integer code;
    private SacRecdStatusEnum sacRecdStatusEnum;

    RecdStatusEnum(Integer code, SacRecdStatusEnum sacRecdStatusEnum) {
        this.code = code;
        this.sacRecdStatusEnum = sacRecdStatusEnum;
    }

    public static RecdStatusEnum codeOf(Integer code) {
        for (RecdStatusEnum v : values()) {
            if (v.getCode().equals(code)) {
                return v;
            }
        }
        throw new RuntimeException("不支持的订单状态:" + code);
    }
}
