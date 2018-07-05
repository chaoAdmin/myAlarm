package com.bpdcc.alarm;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * @author cc
 */
public class Music implements Runnable{

    private String filePath;
    private boolean stop = false;

    public Music(String filePath) {
        this.filePath = filePath;
    }

    public void playMusic(){
        stop = false;
        File musicFile = new File(filePath);
        if(!musicFile.exists()){
            return;
        }

        AudioFormat audioFormat = null;
        SourceDataLine sourceDataLine = null;
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(musicFile);
            audioFormat = audioInputStream.getFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat,AudioSystem.NOT_SPECIFIED);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);

            int nBytesRead;
            byte[] data = new byte[1024 * 10];
            while ((nBytesRead = audioInputStream.read(data, 0, data.length)) != -1 ) {
                if(stop){
                    break;
                }
                sourceDataLine.start();
                sourceDataLine.write(data, 0, nBytesRead);
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }finally {
            try {
                audioInputStream.close();
                sourceDataLine.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopMusic(){
        stop = true;
    }

    public void run() {
        playMusic();
    }
}
