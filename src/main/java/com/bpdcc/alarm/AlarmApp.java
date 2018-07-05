package com.bpdcc.alarm;

import java.util.List;

/**
 * @author cc
 */
public class AlarmApp {

    public static void main(String[] args) {
        AlarmApp app = new AlarmApp();
    }

    public AlarmApp() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() throws Exception {
        List<Alarm> list = AlarmYamlUtil.getList();
        for (Alarm alarm : list) {
            if(alarm.isAble()){
                alarm.startupThread();
                Thread.sleep(1000);
            }
        }
    }
}
