package com.tj.event.dao;

import com.tj.dto.OptionEventCountDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface RaceInfoMapperEx {

    OptionEventCountDto sumRaceInfo(@Param("type") String type,
                                    @Param("startTime") Date startTime,
                                    @Param("endTime") Date endTime);

    List<String> selectDistinctCategory();


}
