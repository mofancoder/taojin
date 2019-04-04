package com.tj.event.eventapi;

import lombok.Data;

import java.util.List;

/**
 * domain 层级关系
 * RecordApi
 * --RaceResult <T>: 赛果
 * --RaceApi    <T>
 * --OtherApi
 * --OddsApi
 *
 * @param <T>
 */
@Data
public class RecordApi<T> {

    private String code;
    private List<T> data;

}
