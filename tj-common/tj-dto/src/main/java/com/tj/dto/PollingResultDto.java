package com.tj.dto;

import lombok.Data;

@Data
public class PollingResultDto {
    private Integer code;
    private String msg;
    private PollingData data;
}
