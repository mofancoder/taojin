package com.tj.bet.dao;

import com.tj.bet.domain.RaceOddsInfo;
import com.tj.bet.domain.RaceOddsInfoExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface RaceOddsInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_odds_info
     *
     * @mbg.generated
     */
    long countByExample(RaceOddsInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_odds_info
     *
     * @mbg.generated
     */
    int deleteByExample(RaceOddsInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_odds_info
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_odds_info
     *
     * @mbg.generated
     */
    int insert(RaceOddsInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_odds_info
     *
     * @mbg.generated
     */
    int insertSelective(RaceOddsInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_odds_info
     *
     * @mbg.generated
     */
    List<RaceOddsInfo> selectByExampleWithRowbounds(RaceOddsInfoExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_odds_info
     *
     * @mbg.generated
     */
    List<RaceOddsInfo> selectByExample(RaceOddsInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_odds_info
     *
     * @mbg.generated
     */
    RaceOddsInfo selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_odds_info
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") RaceOddsInfo record, @Param("example") RaceOddsInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_odds_info
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") RaceOddsInfo record, @Param("example") RaceOddsInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_odds_info
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(RaceOddsInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_odds_info
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(RaceOddsInfo record);
}