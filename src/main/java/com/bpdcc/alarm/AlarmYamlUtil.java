package com.bpdcc.alarm;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import org.apache.commons.collections.CollectionUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cc
 */
public class AlarmYamlUtil {

    private static final String FILE_PATH = System.getProperty("user.home") + System.getProperty("file.separator") + BaseConfigUtil.CONFIG_FILE_PATH;
    private static List<Alarm> list;
    private static AlarmInterface alarmInterface;

    static {
        readYaml();
    }

    public static void writerYaml(){
        YamlWriter yamlWriter = null;
        try {
            yamlWriter = new YamlWriter(new FileWriter(FILE_PATH));
            yamlWriter.write(list);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                yamlWriter.close();
            } catch (YamlException e) {
                e.printStackTrace();
            }

        }
    }

    public static void readYaml(){
        File file = new File(FILE_PATH);
        if(!file.exists()){
            try {
                file.createNewFile();
                list = new ArrayList<Alarm>();
                return ;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlReader reader = null;
        try {
            reader = new YamlReader(new FileReader(FILE_PATH));
            list = reader.read(List.class,Alarm.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (YamlException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(CollectionUtils.isEmpty(list)){
                list = new ArrayList<Alarm>();
            }
        }
    }

    public static List<Alarm> getList() {
        return list;
    }

    public static void setList(List<Alarm> list) {
        AlarmYamlUtil.list = list;
    }

    public static AlarmInterface getAlarmInterface() {
        return alarmInterface;
    }

    public static void setAlarmInterface(AlarmInterface alarmInterface) {
        AlarmYamlUtil.alarmInterface = alarmInterface;
    }
}
