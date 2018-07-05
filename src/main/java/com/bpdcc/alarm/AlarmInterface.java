/*
 * Created by cc on Sat Jun 23 16:19:23 CST 2018
 */

package com.bpdcc.alarm;

import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author cc
 */
public class AlarmInterface extends JFrame {
    private JPanel viewPanel;
    private JDialog editJDialog;
    private JDialog addJDialog;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm");
    private List<Alarm> list = AlarmYamlUtil.getList();

    public static void main(String[] args) {
        AlarmInterface alarmInterface = new AlarmInterface();
        alarmInterface.setTitle("我的闹钟");
        alarmInterface.createEditDiolog(alarmInterface,alarmInterface);
        AlarmApp app = new AlarmApp();
        AlarmYamlUtil.setAlarmInterface(alarmInterface);
    }

    public AlarmInterface() {
        JScrollPane jScrollPane = new JScrollPane();
        viewPanel = new JPanel();
        viewPanel.setLayout(new BoxLayout(viewPanel,BoxLayout.Y_AXIS));
        setSize(500,700);

        initComponents();

        jScrollPane.setViewportView(viewPanel);
        this.getContentPane().add(jScrollPane);
        this.setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void createEditDiolog(Frame owner, Component parentComponent){
        editJDialog = new JDialog(owner,true);
        editJDialog.setLocationRelativeTo(parentComponent);
        editJDialog.setSize(500,130);
        editJDialog.setResizable(false);

        JPanel topPanel = new JPanel();
        topPanel.setName("topPanel");
        topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.X_AXIS));

        JPanel middlePanel = new JPanel();
        middlePanel.setName("middlePanel");
        middlePanel.setLayout(new BoxLayout(middlePanel,BoxLayout.Y_AXIS));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setName("buttonPanel");
        buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));

        JButton ok = new JButton("ok");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //three Panel
                JPanel topPanel = (JPanel) editJDialog.getContentPane().getComponent(0);
                JPanel middlePanel = (JPanel) editJDialog.getContentPane().getComponent(1);
                JPanel buttonPanel = (JPanel) editJDialog.getContentPane().getComponent(2);

                JTextField hiddenText = (JTextField)buttonPanel.getComponent(4);
                Alarm alarm = list.get(Integer.valueOf(hiddenText.getText()));

                JSpinner year = (JSpinner)topPanel.getComponent(0);
                alarm.setDate((Date)year.getValue());

                //前面有一个时间控件，第二个是日期空间
                int index = 2;
                StringBuffer weekStr = new StringBuffer(14);
                JCheckBox jCheckBox;
                while (index <= 8){
                    jCheckBox = (JCheckBox)topPanel.getComponent(index);
                    if(jCheckBox.isSelected()){
                        weekStr.append(",").append(index - 1);
                    }
                    index ++ ;
                }
                alarm.setWeek(weekStr.length() > 0 ? Arrays.asList(weekStr.substring(1).split(",")):null);

                JTextField messageText = (JTextField)middlePanel.getComponent(0);
                alarm.setMessage(messageText.getText());
                JTextField musicFilePath = (JTextField)((JPanel)middlePanel.getComponent(1)).getComponent(0);
                alarm.setMusicFilePath(musicFilePath.getText());
                alarm.setAble(true);

                //编辑对话框隐藏
                editJDialog.dispose();

                //重写yaml
                AlarmYamlUtil.writerYaml();
                alarm.shutDownThread();
                alarm.startupThread();

                viewPanel.removeAll();
                initComponents();
                viewPanel.updateUI();
                viewPanel.repaint();
            }
        });

        JButton cancel = new JButton("cancel");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editJDialog.dispose();
            }
        });

        JTextField hiddenText = new JTextField();
        hiddenText.setVisible(false);

        buttonPanel.add(Box.createGlue());
        buttonPanel.add(ok);
        buttonPanel.add(Box.createHorizontalStrut(18));
        buttonPanel.add(cancel);
        buttonPanel.add(hiddenText);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel,BoxLayout.Y_AXIS));

        contentPanel.add(topPanel);
        contentPanel.add(middlePanel);
        contentPanel.add(buttonPanel);
        editJDialog.setContentPane(contentPanel);

    }

    private void createAddDiolog(Frame owner, Component parentComponent){
        addJDialog = new JDialog(owner,true);
        addJDialog.setLocationRelativeTo(parentComponent);
        addJDialog.setSize(500,130);
        addJDialog.setResizable(false);

        JPanel topPanel = new JPanel();
        topPanel.setName("topPanel");
        topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.X_AXIS));

        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner year = new JSpinner(model);
        try {
            year.setValue(SDF.parse("00:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSpinner.DateEditor editor = new JSpinner.DateEditor(year,"HH:mm");
        editor.setMaximumSize(new Dimension(100,30));
        year.setEditor(editor);

        topPanel.add(year);
        topPanel.add(Box.createHorizontalGlue());
        createWeeks(topPanel,null,2,true);


        JPanel middlePanel = new JPanel();
        middlePanel.setName("middlePanel");
        middlePanel.setLayout(new BoxLayout(middlePanel,BoxLayout.Y_AXIS));
        middlePanel.add(new JTextField());

        JPanel musicPanel = new JPanel();
        musicPanel.setLayout(new BoxLayout(musicPanel,BoxLayout.X_AXIS));
        final JTextField musicFilePath = new JTextField();
        musicFilePath.setMaximumSize(new Dimension(400,25));
        JButton musicButton = new JButton("选择音乐");
        musicButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JFileChooser chooser = new JFileChooser();
                final JDialog dialog = new JDialog((Frame) getOwner(),  true);
                Container dialogContentPane = dialog.getContentPane();
                dialogContentPane.setLayout(new BorderLayout());


                chooser.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String state = (String)e.getActionCommand();
                        // 确定按钮被激活
                        if(state.equals(JFileChooser.APPROVE_SELECTION)) {
                            File file = chooser.getSelectedFile();
                            musicFilePath.setText(file.getPath());
                        }
                        dialog.setVisible(false);
                    }
                });

                dialogContentPane.add(chooser, BorderLayout.CENTER);
                dialog.pack();
                dialog.setVisible(true);
            }
        });
        musicPanel.add(musicFilePath);
        musicPanel.add(Box.createHorizontalStrut(10));
        musicPanel.add(musicButton);
        middlePanel.add(musicPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setName("buttonPanel");
        buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));

        JButton ok = new JButton("ok");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPanel topPanel = (JPanel) addJDialog.getContentPane().getComponent(0);
                JPanel middlePanel = (JPanel) addJDialog.getContentPane().getComponent(1);

                JSpinner year = (JSpinner)topPanel.getComponent(0);
                Alarm alarm = new Alarm((Date)year.getValue());

                int index = 2;
                StringBuffer weekStr = new StringBuffer(14);
                JCheckBox jCheckBox;
                while (index <= 8){
                    jCheckBox = (JCheckBox)topPanel.getComponent(index);
                    if(jCheckBox.isSelected()){
                        weekStr.append(",").append(index - 1);
                    }
                    index ++ ;
                }
                alarm.setWeek(weekStr.length() > 0 ? Arrays.asList(weekStr.substring(1).split(",")):null);


                JTextField messageText = (JTextField)middlePanel.getComponent(0);
                alarm.setMessage(messageText.getText());

                JTextField musicFilePath = (JTextField)((JPanel)middlePanel.getComponent(1)).getComponent(0);
                alarm.setMusicFilePath(musicFilePath.getText());
                alarm.setAble(true);

                addJDialog.dispose();

                list.add(0,alarm);
                AlarmYamlUtil.writerYaml();

                viewPanel.removeAll();
                initComponents();
                viewPanel.updateUI();
                viewPanel.repaint();
                alarm.startupThread();
            }
        });

        JButton cancel = new JButton("cancel");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addJDialog.dispose();
            }
        });

        buttonPanel.add(Box.createGlue());
        buttonPanel.add(ok);
        buttonPanel.add(Box.createHorizontalStrut(18));
        buttonPanel.add(cancel);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel,BoxLayout.Y_AXIS));

        contentPanel.add(topPanel);
        contentPanel.add(middlePanel);
        contentPanel.add(buttonPanel);
        addJDialog.setContentPane(contentPanel);
        addJDialog.setVisible(true);
    }

    public void initComponents() {
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new BoxLayout(addPanel,BoxLayout.X_AXIS));

        JButton addButton = new JButton("add");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createAddDiolog(null,getContentPane());
            }
        });
        addPanel.add(Box.createHorizontalGlue());
        addPanel.add(addButton);

        viewPanel.add(addPanel);

        Alarm temp;
        for (int i = 0; i < list.size(); i++) {
            temp = list.get(i);
            if(null != temp){
                this.createAlarmPanel(i,temp);
            }
        }

    }

    private void createAlarmPanel(final int index, final Alarm alarm){
        JPanel alarmPanel = new JPanel();
        alarmPanel.setLayout(new BoxLayout(alarmPanel,BoxLayout.X_AXIS));

        //时间
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner year = new JSpinner(model);
        year.setValue(alarm.getDate());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(year,"HH:mm");
        year.setEditor(editor);
        year.setEnabled(false);
        year.setMaximumSize(new Dimension(200,30));

        //提示语
        final JTextField textField = new JTextField(alarm.getMessage());
        textField.setMaximumSize(new Dimension(200,30));

        //启用按钮
        final JCheckBox checkBox = new JCheckBox("启用");
        checkBox.setSelected(alarm.isAble());
        checkBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                list.get(index).setAble(checkBox.isSelected());
                if(checkBox.isSelected()){
                    list.get(index).startupThread();
                } else {
                    list.get(index).shutDownThread();
                }
                AlarmYamlUtil.writerYaml();
            }
        });

        //星期复选框
        final JPanel weekPanel = new JPanel();
        weekPanel.setLayout(new BoxLayout(weekPanel,BoxLayout.X_AXIS));
        this.createWeeks(weekPanel,alarm.getWeek(),0,false);
        weekPanel.add(Box.createHorizontalGlue());

        //编辑按钮
        JButton edit = new JButton("edit");
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPanel topPanel = (JPanel) editJDialog.getContentPane().getComponent(0);
                topPanel.removeAll();

                SpinnerDateModel model = new SpinnerDateModel();
                JSpinner year = new JSpinner(model);
                year.setValue(alarm.getDate());
                JSpinner.DateEditor editor = new JSpinner.DateEditor(year,"HH:mm");
                editor.setMaximumSize(new Dimension(100,30));
                year.setEditor(editor);
                topPanel.add(year);

                topPanel.add(Box.createHorizontalGlue());

                createWeeks(topPanel,alarm.getWeek(),2,true);

                JPanel middlePanel = (JPanel) editJDialog.getContentPane().getComponent(1);
                middlePanel.removeAll();
                JPanel musicPanel = new JPanel();
                musicPanel.setLayout(new BoxLayout(musicPanel,BoxLayout.X_AXIS));
                final JTextField musicFilePath = new JTextField(alarm.getMusicFilePath());
                musicFilePath.setMaximumSize(new Dimension(400,25));
                JButton musicButton = new JButton("选择音乐");
                musicButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        final JFileChooser chooser = new JFileChooser();
                        final JDialog dialog = new JDialog((Frame) getOwner(),  true);
                        Container dialogContentPane = dialog.getContentPane();
                        dialogContentPane.setLayout(new BorderLayout());

                        chooser.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                String state = (String)e.getActionCommand();
                                // 确定按钮被激活
                                if(state.equals(JFileChooser.APPROVE_SELECTION)) {
                                    File file = chooser.getSelectedFile();
                                    musicFilePath.setText(file.getPath());
                                }
                                dialog.setVisible(false);
                            }
                        });

                        dialogContentPane.add(chooser, BorderLayout.CENTER);
                        dialog.pack();
                        dialog.setVisible(true);
                    }
                });
                musicPanel.add(musicFilePath);
                musicPanel.add(Box.createHorizontalStrut(10));
                musicPanel.add(musicButton);

                middlePanel.add(new JTextField(textField.getText()));
                middlePanel.add(musicPanel);

                JPanel buttonPanel = (JPanel) editJDialog.getContentPane().getComponent(2);
                JTextField hiddenText = (JTextField)buttonPanel.getComponent(4);
                hiddenText.setText(String.valueOf(index));

                //刷新并显示
                topPanel.updateUI();
                topPanel.repaint();

                middlePanel.updateUI();
                middlePanel.repaint();

                editJDialog.setVisible(true);
            }
        });

        //删除按钮
        JButton delete = new JButton("del");
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                list.get(index).shutDownThread();
                list.remove(index);
                AlarmYamlUtil.writerYaml();

                viewPanel.removeAll();
                initComponents();
                viewPanel.updateUI();
                viewPanel.repaint();
            }
        });

        alarmPanel.add(year);
        alarmPanel.add(textField);
        alarmPanel.add(checkBox);
        alarmPanel.add(edit);
        alarmPanel.add(delete);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.Y_AXIS));
        jPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#4CA18F")));
        jPanel.setMaximumSize(new Dimension(500,60));

        jPanel.add(alarmPanel);
        jPanel.add(weekPanel);

        viewPanel.add(jPanel);
        viewPanel.add(Box.createVerticalStrut(5));
    }

    public void repaintUI(){
        viewPanel.removeAll();
        initComponents();
        viewPanel.updateUI();
        viewPanel.repaint();
    }

    private void createWeeks(JPanel weekPanel,List<String> list,int before,boolean able){
        weekPanel.add(new JCheckBox("星期日"));
        weekPanel.add(new JCheckBox("星期一"));
        weekPanel.add(new JCheckBox("星期二"));
        weekPanel.add(new JCheckBox("星期三"));
        weekPanel.add(new JCheckBox("星期四"));
        weekPanel.add(new JCheckBox("星期五"));
        weekPanel.add(new JCheckBox("星期六"));

        JCheckBox temp;
        int index = 1;
        while (index <= 7){
            temp = (JCheckBox)weekPanel.getComponent(index - 1 + before);
            temp.setEnabled(able);
            index ++ ;
        }

        if(CollectionUtils.isEmpty(list)){
            return ;
        }
        for (String s : list) {
            temp = (JCheckBox)weekPanel.getComponent(Integer.valueOf(s) - 1 + before);
            temp.setSelected(true);
        }

        weekPanel.updateUI();
        weekPanel.repaint();
    }



}
