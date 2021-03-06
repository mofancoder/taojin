package com.tj.bet.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UserBalanceChangeRecd implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_balance_change_recd.id
     *
     * @mbg.generated
     */
    private Long id;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_balance_change_recd.related_recd_id
     *
     * @mbg.generated
     */
    private Long relatedRecdId;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_balance_change_recd.opt_type
     *
     * @mbg.generated
     */
    private Integer optType;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_balance_change_recd.amount
     *
     * @mbg.generated
     */
    private BigDecimal amount;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_balance_change_recd.user_id
     *
     * @mbg.generated
     */
    private Integer userId;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_balance_change_recd.sub_or_add
     *
     * @mbg.generated
     */
    private Integer subOrAdd;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_balance_change_recd.version
     *
     * @mbg.generated
     */
    private Integer version;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_balance_change_recd.update_time
     *
     * @mbg.generated
     */
    private Date updateTime;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_balance_change_recd.create_time
     *
     * @mbg.generated
     */
    private Date createTime;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_balance_change_recd.sys_remark
     *
     * @mbg.generated
     */
    private String sysRemark;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_balance_change_recd.id
     *
     * @return the value of t_user_balance_change_recd.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_balance_change_recd.id
     *
     * @param id the value for t_user_balance_change_recd.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_balance_change_recd.related_recd_id
     *
     * @return the value of t_user_balance_change_recd.related_recd_id
     *
     * @mbg.generated
     */
    public Long getRelatedRecdId() {
        return relatedRecdId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_balance_change_recd.related_recd_id
     *
     * @param relatedRecdId the value for t_user_balance_change_recd.related_recd_id
     *
     * @mbg.generated
     */
    public void setRelatedRecdId(Long relatedRecdId) {
        this.relatedRecdId = relatedRecdId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_balance_change_recd.opt_type
     *
     * @return the value of t_user_balance_change_recd.opt_type
     *
     * @mbg.generated
     */
    public Integer getOptType() {
        return optType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_balance_change_recd.opt_type
     *
     * @param optType the value for t_user_balance_change_recd.opt_type
     *
     * @mbg.generated
     */
    public void setOptType(Integer optType) {
        this.optType = optType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_balance_change_recd.amount
     *
     * @return the value of t_user_balance_change_recd.amount
     *
     * @mbg.generated
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_balance_change_recd.amount
     *
     * @param amount the value for t_user_balance_change_recd.amount
     *
     * @mbg.generated
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_balance_change_recd.user_id
     *
     * @return the value of t_user_balance_change_recd.user_id
     *
     * @mbg.generated
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_balance_change_recd.user_id
     *
     * @param userId the value for t_user_balance_change_recd.user_id
     *
     * @mbg.generated
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_balance_change_recd.sub_or_add
     *
     * @return the value of t_user_balance_change_recd.sub_or_add
     *
     * @mbg.generated
     */
    public Integer getSubOrAdd() {
        return subOrAdd;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_balance_change_recd.sub_or_add
     *
     * @param subOrAdd the value for t_user_balance_change_recd.sub_or_add
     *
     * @mbg.generated
     */
    public void setSubOrAdd(Integer subOrAdd) {
        this.subOrAdd = subOrAdd;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_balance_change_recd.version
     *
     * @return the value of t_user_balance_change_recd.version
     *
     * @mbg.generated
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_balance_change_recd.version
     *
     * @param version the value for t_user_balance_change_recd.version
     *
     * @mbg.generated
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_balance_change_recd.update_time
     *
     * @return the value of t_user_balance_change_recd.update_time
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_balance_change_recd.update_time
     *
     * @param updateTime the value for t_user_balance_change_recd.update_time
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_balance_change_recd.create_time
     *
     * @return the value of t_user_balance_change_recd.create_time
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_balance_change_recd.create_time
     *
     * @param createTime the value for t_user_balance_change_recd.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_balance_change_recd.sys_remark
     *
     * @return the value of t_user_balance_change_recd.sys_remark
     *
     * @mbg.generated
     */
    public String getSysRemark() {
        return sysRemark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_balance_change_recd.sys_remark
     *
     * @param sysRemark the value for t_user_balance_change_recd.sys_remark
     *
     * @mbg.generated
     */
    public void setSysRemark(String sysRemark) {
        this.sysRemark = sysRemark == null ? null : sysRemark.trim();
    }
}