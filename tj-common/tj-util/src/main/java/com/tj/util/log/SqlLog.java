package com.tj.util.log;

public class SqlLog {

    private Integer rows;
    private Long time;
    private String sqlId;
    private String sqlAction;
    private String sqlBody;
    private long runTime;

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    public String getSqlAction() {
        return sqlAction;
    }

    public void setSqlAction(String sqlAction) {
        this.sqlAction = sqlAction;
    }

    public String getSqlBody() {
        return sqlBody;
    }

    public void setSqlBody(String sqlBody) {
        this.sqlBody = sqlBody;
    }

    public long getRunTime() {
        return runTime;
    }

    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }

    public String getSqlLog() {
        return "|runTime:" + runTime + "|rows:" + rows + "|time:" + time + "|sqlId:" + sqlId + "|sqlAction:" + sqlAction
                + "|sqlBody:" + sqlBody + "|#";
    }

}
