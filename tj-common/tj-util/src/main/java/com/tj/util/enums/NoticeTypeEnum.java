package com.tj.util.enums;

public enum NoticeTypeEnum {
    SystemMsg(0, "https://gdpic.oss-cn-shenzhen.aliyuncs.com/notice/SystemMsg.png"),//系统消息
    OperationMsg(1, "https://gdpic.oss-cn-shenzhen.aliyuncs.com/notice/OperationMsg.png"),//运营消息
    TransferMsg(2, "https://gdpic.oss-cn-shenzhen.aliyuncs.com/notice/TransferMsg.png");//动账消息

    private Integer code;
    private String icon;

    private NoticeTypeEnum(Integer code, String rateName) {
        this.code = code;
        this.icon = rateName;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
