package com.tj.bet.dao;

import com.tj.dto.AdminBetRecdDto;
import com.tj.dto.AdminBetRecdInfo;
import com.tj.dto.RebateBetInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-13-11:31
 **/
public interface BetRecdMapperEx {
    List<AdminBetRecdDto> adminList(@Param("betId") String betId,
                                    @Param("phone") String phone,
                                    @Param("account") String account,
                                    @Param("raceId") String raceId,
                                    @Param("score") String score,
                                    @Param("betType") Integer betType,
                                    @Param("betResult") Integer betResult,
                                    @Param("rebateStatus") Integer rebateStatus,
                                    @Param("startTime") Date startTime,
                                    @Param("endTime") Date endTime);

    AdminBetRecdInfo sumBetRecd(@Param("betId") String betId,
                                @Param("phone") String phone,
                                @Param("account") String account,
                                @Param("raceId") String raceId,
                                @Param("score") String score,
                                @Param("betType") Integer betType,
                                @Param("betResult") Integer betResult,
                                @Param("rebateStatus") Integer rebateStatus,
                                @Param("startTime") Date startTime,
                                @Param("endTime") Date endTime);

    AdminBetRecdInfo userSumBetRecd(@Param("userId") Integer userId, @Param("type") Integer type, @Param("startTime") Date finalStartTime, @Param("endTime") Date endTime);

    List<RebateBetInfo> getRebateBetInfo(@Param("rebateIds") List<Integer> rebateIds);
}
