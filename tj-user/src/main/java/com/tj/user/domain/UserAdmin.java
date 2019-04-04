package com.tj.user.domain;

import java.io.Serializable;
import java.util.Date;

public class UserAdmin implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.user_id
     *
     * @mbg.generated
     */
    private Integer userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.nick_name
     *
     * @mbg.generated
     */
    private String nickName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.account
     *
     * @mbg.generated
     */
    private String account;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.icon_url
     *
     * @mbg.generated
     */
    private String iconUrl;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.phone
     *
     * @mbg.generated
     */
    private String phone;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.email
     *
     * @mbg.generated
     */
    private String email;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.phone_area_code
     *
     * @mbg.generated
     */
    private String phoneAreaCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.invite_code
     *
     * @mbg.generated
     */
    private String inviteCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.nationality
     *
     * @mbg.generated
     */
    private String nationality;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.sys_status
     *
     * @mbg.generated
     */
    private Byte sysStatus;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.regist_time
     *
     * @mbg.generated
     */
    private Date registTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.login_pwd
     *
     * @mbg.generated
     */
    private String loginPwd;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.last_login_time
     *
     * @mbg.generated
     */
    private Date lastLoginTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.update_time
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.opt_status
     *
     * @mbg.generated
     */
    private Byte optStatus;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.opt_reason
     *
     * @mbg.generated
     */
    private String optReason;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.proxy
     *
     * @mbg.generated
     */
    private Integer proxy;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_user_admin.fund_password
     *
     * @mbg.generated
     */
    private String fundPassword;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_user_admin
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.user_id
     *
     * @return the value of t_user_admin.user_id
     *
     * @mbg.generated
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.user_id
     *
     * @param userId the value for t_user_admin.user_id
     *
     * @mbg.generated
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.nick_name
     *
     * @return the value of t_user_admin.nick_name
     *
     * @mbg.generated
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.nick_name
     *
     * @param nickName the value for t_user_admin.nick_name
     *
     * @mbg.generated
     */
    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.account
     *
     * @return the value of t_user_admin.account
     *
     * @mbg.generated
     */
    public String getAccount() {
        return account;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.account
     *
     * @param account the value for t_user_admin.account
     *
     * @mbg.generated
     */
    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.icon_url
     *
     * @return the value of t_user_admin.icon_url
     *
     * @mbg.generated
     */
    public String getIconUrl() {
        return iconUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.icon_url
     *
     * @param iconUrl the value for t_user_admin.icon_url
     *
     * @mbg.generated
     */
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl == null ? null : iconUrl.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.phone
     *
     * @return the value of t_user_admin.phone
     *
     * @mbg.generated
     */
    public String getPhone() {
        return phone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.phone
     *
     * @param phone the value for t_user_admin.phone
     *
     * @mbg.generated
     */
    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.email
     *
     * @return the value of t_user_admin.email
     *
     * @mbg.generated
     */
    public String getEmail() {
        return email;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.email
     *
     * @param email the value for t_user_admin.email
     *
     * @mbg.generated
     */
    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.phone_area_code
     *
     * @return the value of t_user_admin.phone_area_code
     *
     * @mbg.generated
     */
    public String getPhoneAreaCode() {
        return phoneAreaCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.phone_area_code
     *
     * @param phoneAreaCode the value for t_user_admin.phone_area_code
     *
     * @mbg.generated
     */
    public void setPhoneAreaCode(String phoneAreaCode) {
        this.phoneAreaCode = phoneAreaCode == null ? null : phoneAreaCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.invite_code
     *
     * @return the value of t_user_admin.invite_code
     *
     * @mbg.generated
     */
    public String getInviteCode() {
        return inviteCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.invite_code
     *
     * @param inviteCode the value for t_user_admin.invite_code
     *
     * @mbg.generated
     */
    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode == null ? null : inviteCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.nationality
     *
     * @return the value of t_user_admin.nationality
     *
     * @mbg.generated
     */
    public String getNationality() {
        return nationality;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.nationality
     *
     * @param nationality the value for t_user_admin.nationality
     *
     * @mbg.generated
     */
    public void setNationality(String nationality) {
        this.nationality = nationality == null ? null : nationality.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.sys_status
     *
     * @return the value of t_user_admin.sys_status
     *
     * @mbg.generated
     */
    public Byte getSysStatus() {
        return sysStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.sys_status
     *
     * @param sysStatus the value for t_user_admin.sys_status
     *
     * @mbg.generated
     */
    public void setSysStatus(Byte sysStatus) {
        this.sysStatus = sysStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.regist_time
     *
     * @return the value of t_user_admin.regist_time
     *
     * @mbg.generated
     */
    public Date getRegistTime() {
        return registTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.regist_time
     *
     * @param registTime the value for t_user_admin.regist_time
     *
     * @mbg.generated
     */
    public void setRegistTime(Date registTime) {
        this.registTime = registTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.login_pwd
     *
     * @return the value of t_user_admin.login_pwd
     *
     * @mbg.generated
     */
    public String getLoginPwd() {
        return loginPwd;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.login_pwd
     *
     * @param loginPwd the value for t_user_admin.login_pwd
     *
     * @mbg.generated
     */
    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd == null ? null : loginPwd.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.last_login_time
     *
     * @return the value of t_user_admin.last_login_time
     *
     * @mbg.generated
     */
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.last_login_time
     *
     * @param lastLoginTime the value for t_user_admin.last_login_time
     *
     * @mbg.generated
     */
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.update_time
     *
     * @return the value of t_user_admin.update_time
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.update_time
     *
     * @param updateTime the value for t_user_admin.update_time
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.opt_status
     *
     * @return the value of t_user_admin.opt_status
     *
     * @mbg.generated
     */
    public Byte getOptStatus() {
        return optStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.opt_status
     *
     * @param optStatus the value for t_user_admin.opt_status
     *
     * @mbg.generated
     */
    public void setOptStatus(Byte optStatus) {
        this.optStatus = optStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.opt_reason
     *
     * @return the value of t_user_admin.opt_reason
     *
     * @mbg.generated
     */
    public String getOptReason() {
        return optReason;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.opt_reason
     *
     * @param optReason the value for t_user_admin.opt_reason
     *
     * @mbg.generated
     */
    public void setOptReason(String optReason) {
        this.optReason = optReason == null ? null : optReason.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.proxy
     *
     * @return the value of t_user_admin.proxy
     *
     * @mbg.generated
     */
    public Integer getProxy() {
        return proxy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.proxy
     *
     * @param proxy the value for t_user_admin.proxy
     *
     * @mbg.generated
     */
    public void setProxy(Integer proxy) {
        this.proxy = proxy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_user_admin.fund_password
     *
     * @return the value of t_user_admin.fund_password
     *
     * @mbg.generated
     */
    public String getFundPassword() {
        return fundPassword;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_user_admin.fund_password
     *
     * @param fundPassword the value for t_user_admin.fund_password
     *
     * @mbg.generated
     */
    public void setFundPassword(String fundPassword) {
        this.fundPassword = fundPassword == null ? null : fundPassword.trim();
    }
}