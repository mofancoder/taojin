package com.tj.util.log;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestLog {
    private String uri;
    private String bean;
    private String method;
    private String args;
}
