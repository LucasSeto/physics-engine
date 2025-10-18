/*SoundEffect.java
 * Lucas Seto
 * 
 * creates sound effects with methods to start and stop them
 */

import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundEffect{
    private Clip c;
    public SoundEffect(String filename){
        setClip(filename);
    }
    public void setClip(String filename){
        try{
            File f = new File(filename);
            c = AudioSystem.getClip();
            c.open(AudioSystem.getAudioInputStream(f));
        } catch(Exception e){ System.out.println("error"); }
    }
    public void play(){// play sound effect
        c.setFramePosition(0);
        c.start();// start playing
    }
    public void stop(){
        c.stop();// stop playing
    }
}