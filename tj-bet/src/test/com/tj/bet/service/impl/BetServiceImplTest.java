package com.tj.bet.service.impl;

import com.google.common.collect.Lists;
import com.tj.bet.service.BetService;
import com.tj.dto.BetRequest;
import com.tj.dto.BetRequestDto;
import com.tj.dto.UnbetReasonDto;
import com.tj.util.Results;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"com.tj"})
@MapperScan(basePackages = {"com.tj.bet.dao"})
@ActiveProfiles(value = {"dev"})
public class BetServiceImplTest {
    @Autowired
    private BetService betService;

    @Test
    public void add() {
        BetRequest betRequest = BetRequest.builder()
                .amount(new BigDecimal(9000))
                .raceId("910952")
                .rebateId(5150)
                .build();
        List<BetRequest> betRequests = Lists.newLinkedList();
        betRequests.add(betRequest);

        BetRequestDto betRequestDto = BetRequestDto.builder()
                .betRequests(betRequests)
                .build();
        int code = betService.add(100033, betRequestDto).getCode();
        System.out.println(String.valueOf(code).toString());
        if (String.valueOf(code).equals(String.valueOf(Results.SUCCESS.getCode()))) {
            System.out.println(123);
        }
//            autoRebateDive(null, null);//自动跳水
    }

    @Test
    public void addTTT() {

        betService.selectAllCancelRaceAndRollback();
    }
}