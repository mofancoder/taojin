package com.tj.bet.dao;

import com.tj.bet.domain.RaceInfo;
import com.tj.bet.domain.RaceInfoExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface RaceInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_info
     *
     * @mbg.generated
     */
    long countByExample(RaceInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_info
     *
     * @mbg.generated
     */
    int deleteByExample(RaceInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_info
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_info
     *
     * @mbg.generated
     */
    int insert(RaceInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_info
     *
     * @mbg.generated
     */
    int insertSelective(RaceInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_info
     *
     * @mbg.generated
     */
    List<RaceInfo> selectByExampleWithRowbounds(RaceInfoExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_info
     *
     * @mbg.generated
     */
    List<RaceInfo> selectByExample(RaceInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_info
     *
     * @mbg.generated
     */
    RaceInfo selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_info
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") RaceInfo record, @Param("example") RaceInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_info
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") RaceInfo record, @Param("example") RaceInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_info
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(RaceInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_race_info
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(RaceInfo record);
}