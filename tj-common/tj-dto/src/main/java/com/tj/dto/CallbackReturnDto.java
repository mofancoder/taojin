package com.tj.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CallbackReturnDto {
    private Integer code;
    private String msg;
    private Object data;
}