package com.tj.event.util;

import com.tj.util.enums.RaceStatusEnum;
import com.tj.util.enums.WinTypeEnum;

public class CrawlUtils {

    public static int getStatusIndex(String status) {
        int statusIndex;
        if (status.contains("半场")) {//赛事结果:(0:取消1:正常进行中 2:已经结束3:未开始
            statusIndex = RaceStatusEnum.processing.getCode();
        } else if (status.contains("未开场")) {
            statusIndex = RaceStatusEnum.un_start.getCode();
        } else if (status.contains("结束") || status.contains("完")) { //数据页面目前没有此种状态
            statusIndex = RaceStatusEnum.end.getCode();
//          endTime = status.substring(status.length() )
        } else {
            statusIndex = RaceStatusEnum.cancel.getCode();
        }
        return statusIndex;
    }

    public static int getWinType(String score) {
        String[] scores;
        if (score.contains("-")) {
            scores = score.trim().replace(" ", "").split("-");
            if (Integer.parseInt(scores[0].trim()) > Integer.parseInt(scores[1].trim())) {
                return WinTypeEnum.HOME_WIN.getCode();
            }else if (Integer.parseInt(scores[0].trim()) < Integer.parseInt(scores[1].trim())) {
                return WinTypeEnum.VISIT_WIN.getCode();
            } else {
                return WinTypeEnum.BALANCE.getCode();
            }
        }
        return WinTypeEnum.NO_VALUE.getCode();
    }
}