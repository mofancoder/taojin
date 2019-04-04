package com.tj.event.util;

public class WinInfo {

    private String[] teamName;
    private String oddsInfoType;
    private String score;
    private String winTeam;

    public WinInfo(String[] teamName, String oddsInfoType, String score, String winTeam) {
        this.teamName = teamName;
        this.oddsInfoType = oddsInfoType;
        this.score = score;
        this.winTeam = winTeam;
    }

    public WinInfo(String[] teamName, String score, String winTeam) {
        this.teamName = teamName;
        this.score = score;
        this.winTeam = winTeam;
    }

    public String getOddsInfoType() {
        return oddsInfoType;
    }

    public String getScore() {
        return score;
    }

    public String getWinTeam() {
        return winTeam;
    }

    public WinInfo invoke() {
        String[] scores;
        if (score.contains(":")) {
            scores = score.split(":");
            if (scores[0].equals(scores[1])) {
                oddsInfoType = "2";  //比分相同
                winTeam = teamName[0] + "," + teamName[1];
            }

            if ("0".equals(oddsInfoType)) {//主场胜
                winTeam = teamName[0];
                score = scores[0] + ":" + scores[1];
            } else if ("1".equals(oddsInfoType)) {
                winTeam = teamName[1];
                score = scores[1] + ":" + scores[0];
            }
        } else {
            score = "其他";
        }
        return this;
    }

    public WinInfo compare() {
        String[] scores;
        if (score.contains(" - ")) {
            scores = score.trim().replace(" ", "").split("-");
            if (Integer.parseInt(scores[0].trim()) > Integer.parseInt(scores[1].trim())) {
                winTeam = teamName[0];
            } else if (Integer.parseInt(scores[0].trim()) < Integer.parseInt(scores[1].trim())) {
                winTeam = teamName[1];
            } else {
                winTeam = teamName[0] + "," + teamName[1];//比分相同
            }
        }
        return this;
    }


}
