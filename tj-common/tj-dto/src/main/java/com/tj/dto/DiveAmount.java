package com.tj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiveAmount {
//    private BigDecimal subBetAmount;//跳水后剩余金额
    private BigDecimal changeAmount;//上次投注金额

}
