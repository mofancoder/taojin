package com.tj.util;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class GraphValidCode {

    private static final char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static String getCode(int count) {
        Random r = new Random();
        String code = "";
        for (int i = 0; i < count; i++) {
            int k = r.nextInt(34);
            code = code + Character.toString(codeSequence[k]);
        }
        return code;
    }

    public static BufferedImage createImage(String code) {
        int width = 100, height = 35, linecount = 100;//图片的宽度，高度，干扰线的数
        int x = 0, fontHeight = 0, codeY = 0;//字体的宽度，字体高度，干扰线高度
        int red = 0, green = 0, blue = 0;
        int codecount = code.length();
        x = width / (codecount + 2);
        fontHeight = height - 2;
        codeY = height - 4;
        Random r = new Random();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.gray);
        g.fillRect(0, 0, width, height);
        Font font = new Font("Arial", Font.PLAIN, fontHeight);
        g.setFont(font);
        for (int i = 0; i < linecount; i++) {
            int xb = r.nextInt(width);
            int yb = r.nextInt(height);
            int xe = xb + r.nextInt(width / 8);
            int ye = yb + r.nextInt(height / 8);
            red = r.nextInt(255);
            blue = r.nextInt(255);
            green = r.nextInt(255);
            g.setColor(new Color(red, green, blue));
            g.drawLine(xb, yb, xe, ye);
        }

        for (int i = 0; i < codecount; i++) {
            String unit = code.substring(i, i + 1);
            red = r.nextInt(255);
            blue = r.nextInt(255);
            green = r.nextInt(255);
            g.setColor(new Color(red, green, blue));
            g.drawString(unit, (i + 1) * x, codeY);
        }
        return image;
    }


    public static void output(String code, ServletOutputStream out) throws IOException {

        ImageIO.write(createImage(code), "png", out);
        out.close();

    }


    /**
     * baseString 递归调用
     *
     * @param num 十进制数(不超过1亿)
     * @aram lenghtNum 位数
     */
    public static String baseString(int num, int lenghtNum) {
        int base = 36;
        String code = getBaseCode(num, base);
        Integer len = code.length();
        if (lenghtNum > len) {
            for (int i = 0; i < lenghtNum - len; i++) {
                code = "0" + code;
            }
        }
        return code;
    }

    /**
     * baseString 递归调用
     *
     * @param num  十进制数
     * @param base 要转换成的进制数(不超过1亿)
     */
    public static String getBaseCode(int num, int base) {
        String str = "";
        String digit = "09a18bc27def3ghij4klmno5pqrstu6vwxyz7";
        if (num == 0) {
            return "0";
        } else {
            str = getBaseCode(num / base, base);
            String code = str + digit.charAt(num % base);
            return code;
        }
    }

    /**
     * 获取指定长度的唯一 code
     *
     * @param digitLen code 长度
     * @param Number   自增唯一的  int 数值
     * @return
     */
    public static String getUniqueCode(int digitLen, int Number) {
        String digit = "0123456789abcdefghijklmnopqrstuvwxyz";
        int size = digit.length();
        String code = "";
        char[] list = digit.toCharArray();
        for (int i = 0; i < digitLen; i++) {
            char[] chars = new char[size];
            int numSize = size / digitLen;
            for (int j = 0; j < digitLen; j++) {        //把字符串切成digitNum段
                int index = j * numSize;
                if (j == (digitLen - 1)) {
                    numSize = size - index;
                }
                for (int k = 0; k < numSize; k++) {
                    int currentIndex = index + k;
                    char c;
                    if (j % 2 == 0) {
                        c = list[index + (numSize - 1) - k];
                    } else {
                        c = list[index + k];
                    }

                    chars[currentIndex] = c;
                }
            }
            if (i % 2 == 0) {
                list = chars;
            }
            int den = (int) Math.pow(size, (digitLen - i - 1));

            int n = (int) (Number / den);
            char item;
            if (n > 0) {
                item = chars[n];
                Number = Number - n * den;
            } else {
                item = chars[0];
            }
            code = item + code;
        }
        return code;
    }

    public static void main(String arg[]) {
//		Map map = new HashMap();
//		int num=(int) Math.pow(36, 5);
//		for(int i=num-20;i<num;i++) {
//			String code =baseString(i, 5);
//			System.out.println(code);
//			if(map.containsKey(code)) {
//				System.out.println(code);
//			}else {
//
//				map.put(code, code);
//			}
//
//		}
//        System.out.println(baseString(100000, 6));
//        System.out.println(baseString(100001, 6));
//        System.out.println(baseString(100002, 6));
//        System.out.println(baseString(100003, 6));
//        System.out.println(baseString(100004, 6));
//        System.out.println(baseString(100005, 6));
//        System.out.println(baseString(100006, 6));
        System.out.println(baseString(107727, 6));

    }
}
