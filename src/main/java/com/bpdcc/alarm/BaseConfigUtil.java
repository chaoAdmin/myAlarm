package com.bpdcc.alarm;

/**
 * @author cc
 */
public class BaseConfigUtil {
    /**
     * 10分钟后再次响铃声
     */
    public static int LATER = 10;
    /**
     * 闹钟提示补充文案
     */
    public static String SUPPLEMENT_MESSAGE = "（点击取消"+LATER+"分钟后再响铃）";
    /**
     * 配置文件地址
     */
    public static String CONFIG_FILE_PATH = "alarm.yml";
    /**
     * 默认 title
     */
    public static String TITLE = "我的闹钟";
    /**
     * 默认 message
     */
    public static String MESSAGE = "时间到了";

}
