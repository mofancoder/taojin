package com.tj.util;

import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * @author yelo
 * @date 2015/11/29
 */
public class Docs {

    public static String filePath(String fileName) {
        fileName = fileRename(fileName);
        int code = fileName.hashCode();
        int x = 0xf;
        int dir1 = (code >> 4) & x;
        int dir2 = (code >> 8) & x;
        int dir3 = (code >> 12) & x;
        int dir4 = (code >> 16) & x;
        return new StringBuilder().append("/").append(dir1).append("/")
                .append(dir2).append("/").append(dir3).append("/").append(dir4).append("/").append(fileName).toString();
    }

    public static void main(String[] args) {
        String str = "111.jpg";
        System.out.println(filePath(str));
    }

    public static String fileRename(String filename) {
        return StringUtils.isEmpty(filename) ? filename : (UUID.randomUUID() + filename.substring(filename.lastIndexOf(".")));
    }

    public static String png() {
        return UUID.randomUUID() + ".png";
    }
}
