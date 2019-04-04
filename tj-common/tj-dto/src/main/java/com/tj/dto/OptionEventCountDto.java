package com.tj.dto;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class OptionEventCountDto {
    @ApiModelProperty("总记录数")
    private Integer totalCount;
}
