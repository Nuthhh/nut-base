package com.nut.base.core.util;

import java.text.DecimalFormat;

/**
 * @Auther: han jianguo
 * @Date: 2019/11/20 11:20
 * @Description:
 **/
public class ConsoleProgressBarUtil {

    private static int barLen = 50; // 控制台显示的进度条长度

    private static char showChar = '#'; // 进度条字符

    private static DecimalFormat formater = new DecimalFormat("#.## %");


    public static void show(float rate) {
        show(rate, barLen, showChar);
    }

    public static void show(float rate, int barLen, char showChar) {
        if (rate < 0F || rate > 1F) {
            return;
        }
        System.out.print('\r');
        int len = (int) (rate * barLen);
        System.out.print("Progress [");
        for (int i = 0; i < len; i++) {
            System.out.print(showChar);
        }
        for (int i = 0; i < barLen - len; i++) {
            System.out.print(" ");
        }
        System.out.print("] " + format(rate));
    }

    private static String format(float num) {
        return formater.format(num);
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 1; i <= 140; i++) {
            show((float) (i * 1.0 / 140));
            System.out.println();
            Thread.sleep(100);
        }
    }

}
