package com.tj.event.eventapi;

import lombok.Data;

@Data
public class RaceApi {

    private int leagueid;
    private int leagueorder;
    private String leaguename;
    private String leaguename_lang;
    private int matchid;
    private String statementdate;
    private String kickoffdate;
    private int homescore;
    private int awayscore;
    private int homeredcard;
    private int awayredcard;
    private String hometeamname;
    private String hometeamname_lang;
    private String awayteamname;
    private String awayteamname_lang;
    private Object matchtimehalf;
    private Object matchtimeminute;

    private OtherApi other;


}
