package com.tj.task.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserBalanceChangeRecdExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    public UserBalanceChangeRecdExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andRelatedRecdIdIsNull() {
            addCriterion("related_recd_id is null");
            return (Criteria) this;
        }

        public Criteria andRelatedRecdIdIsNotNull() {
            addCriterion("related_recd_id is not null");
            return (Criteria) this;
        }

        public Criteria andRelatedRecdIdEqualTo(Long value) {
            addCriterion("related_recd_id =", value, "relatedRecdId");
            return (Criteria) this;
        }

        public Criteria andRelatedRecdIdNotEqualTo(Long value) {
            addCriterion("related_recd_id <>", value, "relatedRecdId");
            return (Criteria) this;
        }

        public Criteria andRelatedRecdIdGreaterThan(Long value) {
            addCriterion("related_recd_id >", value, "relatedRecdId");
            return (Criteria) this;
        }

        public Criteria andRelatedRecdIdGreaterThanOrEqualTo(Long value) {
            addCriterion("related_recd_id >=", value, "relatedRecdId");
            return (Criteria) this;
        }

        public Criteria andRelatedRecdIdLessThan(Long value) {
            addCriterion("related_recd_id <", value, "relatedRecdId");
            return (Criteria) this;
        }

        public Criteria andRelatedRecdIdLessThanOrEqualTo(Long value) {
            addCriterion("related_recd_id <=", value, "relatedRecdId");
            return (Criteria) this;
        }

        public Criteria andRelatedRecdIdIn(List<Long> values) {
            addCriterion("related_recd_id in", values, "relatedRecdId");
            return (Criteria) this;
        }

        public Criteria andRelatedRecdIdNotIn(List<Long> values) {
            addCriterion("related_recd_id not in", values, "relatedRecdId");
            return (Criteria) this;
        }

        public Criteria andRelatedRecdIdBetween(Long value1, Long value2) {
            addCriterion("related_recd_id between", value1, value2, "relatedRecdId");
            return (Criteria) this;
        }

        public Criteria andRelatedRecdIdNotBetween(Long value1, Long value2) {
            addCriterion("related_recd_id not between", value1, value2, "relatedRecdId");
            return (Criteria) this;
        }

        public Criteria andOptTypeIsNull() {
            addCriterion("opt_type is null");
            return (Criteria) this;
        }

        public Criteria andOptTypeIsNotNull() {
            addCriterion("opt_type is not null");
            return (Criteria) this;
        }

        public Criteria andOptTypeEqualTo(Integer value) {
            addCriterion("opt_type =", value, "optType");
            return (Criteria) this;
        }

        public Criteria andOptTypeNotEqualTo(Integer value) {
            addCriterion("opt_type <>", value, "optType");
            return (Criteria) this;
        }

        public Criteria andOptTypeGreaterThan(Integer value) {
            addCriterion("opt_type >", value, "optType");
            return (Criteria) this;
        }

        public Criteria andOptTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("opt_type >=", value, "optType");
            return (Criteria) this;
        }

        public Criteria andOptTypeLessThan(Integer value) {
            addCriterion("opt_type <", value, "optType");
            return (Criteria) this;
        }

        public Criteria andOptTypeLessThanOrEqualTo(Integer value) {
            addCriterion("opt_type <=", value, "optType");
            return (Criteria) this;
        }

        public Criteria andOptTypeIn(List<Integer> values) {
            addCriterion("opt_type in", values, "optType");
            return (Criteria) this;
        }

        public Criteria andOptTypeNotIn(List<Integer> values) {
            addCriterion("opt_type not in", values, "optType");
            return (Criteria) this;
        }

        public Criteria andOptTypeBetween(Integer value1, Integer value2) {
            addCriterion("opt_type between", value1, value2, "optType");
            return (Criteria) this;
        }

        public Criteria andOptTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("opt_type not between", value1, value2, "optType");
            return (Criteria) this;
        }

        public Criteria andAmountIsNull() {
            addCriterion("amount is null");
            return (Criteria) this;
        }

        public Criteria andAmountIsNotNull() {
            addCriterion("amount is not null");
            return (Criteria) this;
        }

        public Criteria andAmountEqualTo(BigDecimal value) {
            addCriterion("amount =", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountNotEqualTo(BigDecimal value) {
            addCriterion("amount <>", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountGreaterThan(BigDecimal value) {
            addCriterion("amount >", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("amount >=", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountLessThan(BigDecimal value) {
            addCriterion("amount <", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountLessThanOrEqualTo(BigDecimal value) {
            addCriterion("amount <=", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountIn(List<BigDecimal> values) {
            addCriterion("amount in", values, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountNotIn(List<BigDecimal> values) {
            addCriterion("amount not in", values, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("amount between", value1, value2, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("amount not between", value1, value2, "amount");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNull() {
            addCriterion("user_id is null");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNotNull() {
            addCriterion("user_id is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdEqualTo(Integer value) {
            addCriterion("user_id =", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotEqualTo(Integer value) {
            addCriterion("user_id <>", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThan(Integer value) {
            addCriterion("user_id >", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("user_id >=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThan(Integer value) {
            addCriterion("user_id <", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThanOrEqualTo(Integer value) {
            addCriterion("user_id <=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdIn(List<Integer> values) {
            addCriterion("user_id in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotIn(List<Integer> values) {
            addCriterion("user_id not in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdBetween(Integer value1, Integer value2) {
            addCriterion("user_id between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotBetween(Integer value1, Integer value2) {
            addCriterion("user_id not between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andSubOrAddIsNull() {
            addCriterion("sub_or_add is null");
            return (Criteria) this;
        }

        public Criteria andSubOrAddIsNotNull() {
            addCriterion("sub_or_add is not null");
            return (Criteria) this;
        }

        public Criteria andSubOrAddEqualTo(Integer value) {
            addCriterion("sub_or_add =", value, "subOrAdd");
            return (Criteria) this;
        }

        public Criteria andSubOrAddNotEqualTo(Integer value) {
            addCriterion("sub_or_add <>", value, "subOrAdd");
            return (Criteria) this;
        }

        public Criteria andSubOrAddGreaterThan(Integer value) {
            addCriterion("sub_or_add >", value, "subOrAdd");
            return (Criteria) this;
        }

        public Criteria andSubOrAddGreaterThanOrEqualTo(Integer value) {
            addCriterion("sub_or_add >=", value, "subOrAdd");
            return (Criteria) this;
        }

        public Criteria andSubOrAddLessThan(Integer value) {
            addCriterion("sub_or_add <", value, "subOrAdd");
            return (Criteria) this;
        }

        public Criteria andSubOrAddLessThanOrEqualTo(Integer value) {
            addCriterion("sub_or_add <=", value, "subOrAdd");
            return (Criteria) this;
        }

        public Criteria andSubOrAddIn(List<Integer> values) {
            addCriterion("sub_or_add in", values, "subOrAdd");
            return (Criteria) this;
        }

        public Criteria andSubOrAddNotIn(List<Integer> values) {
            addCriterion("sub_or_add not in", values, "subOrAdd");
            return (Criteria) this;
        }

        public Criteria andSubOrAddBetween(Integer value1, Integer value2) {
            addCriterion("sub_or_add between", value1, value2, "subOrAdd");
            return (Criteria) this;
        }

        public Criteria andSubOrAddNotBetween(Integer value1, Integer value2) {
            addCriterion("sub_or_add not between", value1, value2, "subOrAdd");
            return (Criteria) this;
        }

        public Criteria andVersionIsNull() {
            addCriterion("version is null");
            return (Criteria) this;
        }

        public Criteria andVersionIsNotNull() {
            addCriterion("version is not null");
            return (Criteria) this;
        }

        public Criteria andVersionEqualTo(Integer value) {
            addCriterion("version =", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotEqualTo(Integer value) {
            addCriterion("version <>", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionGreaterThan(Integer value) {
            addCriterion("version >", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionGreaterThanOrEqualTo(Integer value) {
            addCriterion("version >=", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLessThan(Integer value) {
            addCriterion("version <", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLessThanOrEqualTo(Integer value) {
            addCriterion("version <=", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionIn(List<Integer> values) {
            addCriterion("version in", values, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotIn(List<Integer> values) {
            addCriterion("version not in", values, "version");
            return (Criteria) this;
        }

        public Criteria andVersionBetween(Integer value1, Integer value2) {
            addCriterion("version between", value1, value2, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotBetween(Integer value1, Integer value2) {
            addCriterion("version not between", value1, value2, "version");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andSysRemarkIsNull() {
            addCriterion("sys_remark is null");
            return (Criteria) this;
        }

        public Criteria andSysRemarkIsNotNull() {
            addCriterion("sys_remark is not null");
            return (Criteria) this;
        }

        public Criteria andSysRemarkEqualTo(String value) {
            addCriterion("sys_remark =", value, "sysRemark");
            return (Criteria) this;
        }

        public Criteria andSysRemarkNotEqualTo(String value) {
            addCriterion("sys_remark <>", value, "sysRemark");
            return (Criteria) this;
        }

        public Criteria andSysRemarkGreaterThan(String value) {
            addCriterion("sys_remark >", value, "sysRemark");
            return (Criteria) this;
        }

        public Criteria andSysRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("sys_remark >=", value, "sysRemark");
            return (Criteria) this;
        }

        public Criteria andSysRemarkLessThan(String value) {
            addCriterion("sys_remark <", value, "sysRemark");
            return (Criteria) this;
        }

        public Criteria andSysRemarkLessThanOrEqualTo(String value) {
            addCriterion("sys_remark <=", value, "sysRemark");
            return (Criteria) this;
        }

        public Criteria andSysRemarkLike(String value) {
            addCriterion("sys_remark like", value, "sysRemark");
            return (Criteria) this;
        }

        public Criteria andSysRemarkNotLike(String value) {
            addCriterion("sys_remark not like", value, "sysRemark");
            return (Criteria) this;
        }

        public Criteria andSysRemarkIn(List<String> values) {
            addCriterion("sys_remark in", values, "sysRemark");
            return (Criteria) this;
        }

        public Criteria andSysRemarkNotIn(List<String> values) {
            addCriterion("sys_remark not in", values, "sysRemark");
            return (Criteria) this;
        }

        public Criteria andSysRemarkBetween(String value1, String value2) {
            addCriterion("sys_remark between", value1, value2, "sysRemark");
            return (Criteria) this;
        }

        public Criteria andSysRemarkNotBetween(String value1, String value2) {
            addCriterion("sys_remark not between", value1, value2, "sysRemark");
            return (Criteria) this;
        }

        public Criteria andSysRemarkLikeInsensitive(String value) {
            addCriterion("upper(sys_remark) like", value.toUpperCase(), "sysRemark");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table t_user_balance_change_recd
     *
     * @mbg.generated
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }
    }
}