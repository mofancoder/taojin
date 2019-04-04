package com.tj.transaction.service.impl;

import com.tj.dto.AdminTransactionSumDto;
import com.tj.transaction.service.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"com.tj"})
@MapperScan(basePackages = {"com.tj.transaction.dao"})
@ActiveProfiles(value = {"dev"})
public class TransactionServiceImplTest {
    @Autowired
    TransactionService transactionService;


    @Test
    public void adminRecdList() {
        AdminTransactionSumDto adminTransactionSumDto = transactionService.adminRecdList(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                1,
                10).getResult();
        System.out.println(adminTransactionSumDto.toString());
    }
}