package com.tj.transaction.dao;

import com.tj.dto.AdminTransactionRecdDto;
import com.tj.dto.AdminTransactionSumDto;
import com.tj.dto.UserTransactionRecdDto;
import com.tj.dto.UserTransactionSumDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @program: tj-core
 * @description: 交易记录dao 扩展接口
 * @author: liang.song
 * @create: 2018-11-28-15:20
 **/
public interface UserTransactionRecdMapperEx {

    List<UserTransactionRecdDto> list(@Param("userId") Integer userId,
                                      @Param("transactionType") Integer transactionType,
                                      @Param("platform") Integer platform,
                                      @Param("recdStatus") Integer recdStatus,
                                      @Param("startTime") Date startTime,
                                      @Param("endTime") Date endTime);

    List<AdminTransactionRecdDto> listTransaction(@Param("type") Integer type,
                                                  @Param("recdStatus") Integer recdStatus,
                                                  @Param("auditStatus") Integer auditStatus,
                                                  @Param("username") String username,
                                                  @Param("phone") String phone,
                                                  @Param("transactionId") String transactionId,
                                                  @Param("startTime") Date startTime,
                                                  @Param("endTime") Date endTime);

    AdminTransactionSumDto sumTransaction(@Param("type") Integer type,
                                          @Param("recdStatus") Integer recdStatus,
                                          @Param("auditStatus") Integer auditStatus,
                                          @Param("username") String username,
                                          @Param("phone") String phone,
                                          @Param("transactionId") String transactionId,
                                          @Param("startTime") Date startTime,
                                          @Param("endTime") Date endTime);

    UserTransactionSumDto sumUserTransaction(@Param("userId") Integer userId,
                                             @Param("transactionType") Integer transactionType,
                                             @Param("platform") Integer platform,
                                             @Param("recdStatus") Integer recdStatus,
                                             @Param("startTime") Date startTime,
                                             @Param("endTime") Date endTime);
}
