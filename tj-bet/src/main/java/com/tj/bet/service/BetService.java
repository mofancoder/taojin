package com.tj.bet.service;

import com.tj.dto.*;
import com.tj.util.Results;

import java.util.List;

/**
 * @program: tj-core
 * @description: 下注服务
 * @author: liang.song
 * @create: 2018-12-12-14:40
 **/
public interface BetService {

    Results.Result<UnbetReasonDto> add(Integer userId, BetRequestDto betRequestDto);

    Results.Result<String> cancellations(Long userId,Long betId);

    Results.Result<AdminBetRecdInfo<BetRecdDto>> list(Integer userId, Integer type, Integer dateType, Integer curPage, Integer pageSize,String betResult,String rebateStatus,String betStatus);

    Results.Result<Void> settle();

    Results.Result<Void> newSettle();

    Results.Result<AdminBetRecdInfo> adminList(String betId, String phone, String account, String raceId, String score, Integer betType, Integer betResult, Integer rebateStatus, Long startTime, Long endTime, Integer curPage, Integer pageSize);

    Results.Result<List<RebateBetInfo>> rebateBetInfo(List<Integer> rebateIds);

    Results.Result<Void> cancelRaceInfo(String raceId);

    Results.Result<Void> rollbackForCancelRace(String raceId);

    Results.Result<Void> selectAllCancelRaceAndRollback();
}
