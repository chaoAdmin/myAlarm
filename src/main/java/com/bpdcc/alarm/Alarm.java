package com.bpdcc.alarm;

import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author cc
 */
public class Alarm implements Runnable {
    private Thread current;
//    private boolean stopMusicThread = true;

    private long left = 0;
    private Date date;
    private List<String> week;
    private String musicFilePath;
    private String title = BaseConfigUtil.TITLE;
    private String message = BaseConfigUtil.MESSAGE;
    private boolean able;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat TIME_SDF = new SimpleDateFormat("HH:mm");
    private static final Calendar CALENDAR = Calendar.getInstance();


    public static void main(String[] args) throws ParseException {
    }

    public Alarm(Date date, String musicFilePath) {
        this(date);
        this.musicFilePath = musicFilePath;
    }

    public Alarm(Date date) {
        this.date = date;
        CALENDAR.setTime(date);
    }

    public Alarm() {
    }

    public boolean playAlarmMusic() throws InterruptedException {
        Music music = new Music(musicFilePath);

        Thread.sleep(left);

        new Thread(music).start();

        int result = JOptionPane.showConfirmDialog(null,message + BaseConfigUtil.SUPPLEMENT_MESSAGE,title,JOptionPane.OK_CANCEL_OPTION);
        if(result == JOptionPane.YES_OPTION){
            music.stopMusic();
            return true;
        } else if(result == JOptionPane.CANCEL_OPTION){
            left = BaseConfigUtil.LATER * 60 * 1000;
            music.stopMusic();
        }
        return false;
    }

    /**
     * 将设置好的时间转换为当前日期下的时间
     */
    private void convertCurrentDay(){
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        String[] times = TIME_SDF.format(date).split(":");

        CALENDAR.set(Calendar.YEAR,now.get(Calendar.YEAR));
        CALENDAR.set(Calendar.MONTH,now.get(Calendar.MONTH));
        CALENDAR.set(Calendar.DAY_OF_YEAR,now.get(Calendar.DAY_OF_YEAR));
        CALENDAR.set(Calendar.HOUR_OF_DAY,Integer.valueOf(times[0]));
        CALENDAR.set(Calendar.MINUTE,Integer.valueOf(times[1]));

        this.date = CALENDAR.getTime();
    }

    public void run() {
        convertCurrentDay();
        if(CollectionUtils.isEmpty(week)){
            if(date.before(new Date())){
                CALENDAR.add(Calendar.DAY_OF_WEEK,1);
                date = CALENDAR.getTime();
            }
        } else {
            int week = CALENDAR.get(Calendar.DAY_OF_WEEK);
            boolean flag = false;
            for (String s : this.week) {
                if(Integer.valueOf(s) > week){
                    CALENDAR.set(Calendar.DAY_OF_WEEK,Integer.valueOf(s));
                    date = CALENDAR.getTime();
                    flag = true;
                    break;
                } else if(Integer.valueOf(s) == week){
                    if(date.after(new Date())){
                        flag = true;
                        break;
                    }
                }
            }

            if(!flag){
                CALENDAR.set(Calendar.DAY_OF_WEEK,Integer.valueOf(this.week.get(0)));
                CALENDAR.set(Calendar.WEEK_OF_YEAR,CALENDAR.get(Calendar.WEEK_OF_YEAR) + 1);
                CALENDAR.set(Calendar.DAY_OF_WEEK,Integer.valueOf(getWeek().get(0)));
                date = CALENDAR.getTime();
            }
        }

        left = date.getTime() - System.currentTimeMillis();

//        System.out.println("left is " + left);
        if(left > 0){
            boolean threadFlag = true;
            boolean stop = false;
            try {
                while (!stop){
                    stop = playAlarmMusic();
                }
            } catch (InterruptedException e) {
//                System.out.println("线程结束");
                threadFlag = false;
            }
            if(threadFlag){
                if(CollectionUtils.isEmpty(week)){
                    setAble(false);
                    AlarmYamlUtil.writerYaml();
                    AlarmYamlUtil.readYaml();
                    AlarmYamlUtil.getAlarmInterface().repaintUI();
                } else {
                    run();
                }
            }

        }
    }

    public void startupThread(){
        if(current != null){
            shutDownThread();
        }
        current = new Thread(this);
        current.start();
//        System.out.println(current.getName() + "\t startup!");
    }

    public void shutDownThread(){
        if(current != null ){
//            System.out.println(current.getName() + "\t shutdown!");
            current.interrupt();
            current = null;
        }
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        CALENDAR.setTime(date);
    }

    public String getMusicFilePath() {
        return musicFilePath;
    }

    public void setMusicFilePath(String musicFilePath) {
        this.musicFilePath = musicFilePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getWeek() {
        return week;
    }

    public void setWeek(List<String> week) {
        this.week = week;
    }

    public boolean isAble() {
        return able;
    }

    public void setAble(boolean able) {
        this.able = able;
    }

}
