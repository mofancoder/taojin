package com.tj.bet.dao;

import com.tj.bet.domain.UserBalanceInfo;
import com.tj.bet.domain.UserBalanceInfoExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface UserBalanceInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_info
     *
     * @mbg.generated
     */
    long countByExample(UserBalanceInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_info
     *
     * @mbg.generated
     */
    int deleteByExample(UserBalanceInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_info
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_info
     *
     * @mbg.generated
     */
    int insert(UserBalanceInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_info
     *
     * @mbg.generated
     */
    int insertSelective(UserBalanceInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_info
     *
     * @mbg.generated
     */
    List<UserBalanceInfo> selectByExampleWithRowbounds(UserBalanceInfoExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_info
     *
     * @mbg.generated
     */
    List<UserBalanceInfo> selectByExample(UserBalanceInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_info
     *
     * @mbg.generated
     */
    UserBalanceInfo selectByPrimaryKey(Integer userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_info
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") UserBalanceInfo record, @Param("example") UserBalanceInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_info
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") UserBalanceInfo record, @Param("example") UserBalanceInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_info
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(UserBalanceInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_info
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(UserBalanceInfo record);
}