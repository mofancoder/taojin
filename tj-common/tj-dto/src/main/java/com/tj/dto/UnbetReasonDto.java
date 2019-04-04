package com.tj.dto;

import lombok.Data;

import java.util.List;

@Data
public class UnbetReasonDto {
    private List<UnbetReason> unbets;
    private String info;
    List<UnbetRebate> unbetRebates;
}
