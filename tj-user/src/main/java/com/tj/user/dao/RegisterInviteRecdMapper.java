package com.tj.user.dao;

import com.tj.user.domain.RegisterInviteRecd;
import com.tj.user.domain.RegisterInviteRecdExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface RegisterInviteRecdMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_regist_invite_recd
     *
     * @mbg.generated
     */
    long countByExample(RegisterInviteRecdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_regist_invite_recd
     *
     * @mbg.generated
     */
    int deleteByExample(RegisterInviteRecdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_regist_invite_recd
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_regist_invite_recd
     *
     * @mbg.generated
     */
    int insert(RegisterInviteRecd record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_regist_invite_recd
     *
     * @mbg.generated
     */
    int insertSelective(RegisterInviteRecd record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_regist_invite_recd
     *
     * @mbg.generated
     */
    List<RegisterInviteRecd> selectByExampleWithBLOBsWithRowbounds(RegisterInviteRecdExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_regist_invite_recd
     *
     * @mbg.generated
     */
    List<RegisterInviteRecd> selectByExampleWithBLOBs(RegisterInviteRecdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_regist_invite_recd
     *
     * @mbg.generated
     */
    List<RegisterInviteRecd> selectByExampleWithRowbounds(RegisterInviteRecdExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_regist_invite_recd
     *
     * @mbg.generated
     */
    List<RegisterInviteRecd> selectByExample(RegisterInviteRecdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_regist_invite_recd
     *
     * @mbg.generated
     */
    RegisterInviteRecd selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_regist_invite_recd
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") RegisterInviteRecd record, @Param("example") RegisterInviteRecdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_regist_invite_recd
     *
     * @mbg.generated
     */
    int updateByExampleWithBLOBs(@Param("record") RegisterInviteRecd record, @Param("example") RegisterInviteRecdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_regist_invite_recd
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") RegisterInviteRecd record, @Param("example") RegisterInviteRecdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_regist_invite_recd
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(RegisterInviteRecd record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_regist_invite_recd
     *
     * @mbg.generated
     */
    int updateByPrimaryKeyWithBLOBs(RegisterInviteRecd record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_regist_invite_recd
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(RegisterInviteRecd record);
}