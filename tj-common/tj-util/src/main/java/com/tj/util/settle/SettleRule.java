package com.tj.util.settle;

public class SettleRule {

    /**
     *
     * @param scoreActual 真实比分
     * @param betScore 下注比分
     * @return
     */
    public static boolean isWin(String scoreActual,String betScore){
        //截取真实比分前后数字
        int temp = scoreActual.length();
        int firstActualScore = Integer.parseInt(scoreActual.substring(0,1));
        int endActualScore = Integer.parseInt(scoreActual.substring(temp-1,temp));
        //获取投注比分前后数字
        temp = betScore.length();
        int firstBetScore = Integer.parseInt(betScore.substring(0,1));
        int endBetScore = Integer.parseInt(betScore.substring(temp-1,temp));

        if(firstActualScore>firstBetScore||endActualScore>endBetScore){
            return true;
        }
        return false;
    }

    /**
     *
     * @param scoreActual 真实比分
     * @param betScore  下注比分
     * @return 真实比分是否等于下注比分
     * @author yangzhixin
     * @time 2019-01-08
     */
    public static boolean equalsNumber(String scoreActual,String betScore){
        scoreActual = scoreActual.trim();
        betScore = betScore.trim();
        //截取真实比分前后数字
        int temp = scoreActual.length();
        int firstActualScore = Integer.parseInt(scoreActual.substring(0,1));
        int endActualScore = Integer.parseInt(scoreActual.substring(temp-1,temp));
        //获取投注比分前后数字
        temp = betScore.length();
        int firstBetScore = Integer.parseInt(betScore.substring(0,1));
        int endBetScore = Integer.parseInt(betScore.substring(temp-1,temp));

        if(firstActualScore==firstBetScore&&endActualScore==endBetScore){
            return true;
        }
        return false;
    }

}
