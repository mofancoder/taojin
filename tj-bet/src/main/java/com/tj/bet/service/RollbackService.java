package com.tj.bet.service;
/**
 * @program: tj-core
 * @description: ${description}
 * @author: yangzhixin
 * @create: 2019-01-03
 **/
public interface RollbackService {
    void rollbackForCancelRace(String t);
}
