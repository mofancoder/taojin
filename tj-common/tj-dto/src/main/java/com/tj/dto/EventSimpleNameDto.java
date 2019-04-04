package com.tj.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventSimpleNameDto {
    private String simpleCategory;
    private String simpleHomeTeam;
    private String simpleVisitTeam;

}
