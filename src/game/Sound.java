package game;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {
    Clip clip;
    URL soundURL[] = new URL[30];
    float volume = 0; // ค่า 0 คือเสียงปกติ, ค่าติดลบคือเสียงเบาลง

    public Sound() {
        // ระบุตำแหน่งไฟล์เพลง
        soundURL[0] = getClass().getResource("/res/sound/bgm.wav"); 
        soundURL[1] = getClass().getResource("/res/sound/ef.wav");
    }
    

    // เมธอดสำหรับปรับระดับเสียง (ใส่ค่าลบเพื่อให้เบาลง)
    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void setFile(int i, float volume) { // เพิ่ม float volume เข้ามา
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);

            // ปรับเสียงตามค่าที่ส่งมา
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(volume); 

        } catch (Exception e) {
            // Error handling
        }
    }

    public void play() {
        if (clip != null) {
            clip.start();
        }
    }

    public void loop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }
}