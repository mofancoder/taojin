package com.tj.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpUtil {
    /**
     * 以字母开头，长度在6~18之间，只能包含字符、数字和下划线
     *
     * @return
     */
    private static final String ILLEGAL_CHAR = "[^%&'.,;=?$\\x22]+";

    public static boolean matcher1(String input) {
        String regExp = "^[a-zA-Z]\\w{5,17}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(input);
        return m.matches();

    }

    /**
     * 匹配邮箱
     *
     * @param input
     * @return
     */
    public static boolean matcherEmail(String input) {
        String regExp = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(input);
        return m.matches();

    }

    public static boolean matcherNickName(String input) {
        String regExp = "[\\u4e00-\\u9fa5\\w]+";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(input);
        return m.matches();
    }

    /**
     * 检查去全部是数字
     *
     * @param input
     * @return
     */
    public static boolean matcherNumber(String input) {
        String regExp = "^[0-9]*$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(input);
        return m.matches();

    }

    public static boolean matcherPhone(String input) {
        String regExp = "^(0|86|17951)?(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(input);
        return m.matches();
    }

    public static boolean notContainIllegalChar(String input) {
        Pattern p = Pattern.compile(ILLEGAL_CHAR);
        Matcher m = p.matcher(input);
        return m.matches();
    }

    /**
     * html标签转换
     *
     * @param html
     * @return
     */
    public static String ChangeToHtml(String html) {
        html = html.replace("&", "&amp;");
        //html=html.replace(" ", "&nbsp;");
        html = html.replace("<", "&lt;");
        html = html.replace(">", "&gt;");
        html = html.replace("\"", "&quot;");
        //html=html.replace("  ", "&nbsp;&nbsp;&nbsp;&nbsp;");
        //html=html.replace("public", "<b>public</b>");
        return html;
    }

    public static void main(String[] args) {
        System.out.println(matcherNickName("!@@##"));
    }
}
