/*Music.java
 * Lucas Seto
 * 
 * the music player class for starting and stopping the midi files
 */

import java.io.*;
import javax.sound.midi.*;

public class Music {
    private static Sequencer midiPlayer;
    public static String current;

    // constructor that takes the name of an MP3 file
    public Music(String filename) {
        current = "";
    }

    public static void startMidi(String midFilename) {
        try {
           File midiFile = new File(midFilename);
           Sequence song = MidiSystem.getSequence(midiFile);
           midiPlayer = MidiSystem.getSequencer();
           midiPlayer.open();
           midiPlayer.setSequence(song);
           midiPlayer.setLoopCount(-1); // loop
           midiPlayer.start();// start midi player
        } catch (MidiUnavailableException e) {
           e.printStackTrace();
        } catch (InvalidMidiDataException e) {
           e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        }
    }	

    public static void stopMidi() {// stop the midi player if running
        if (midiPlayer != null && midiPlayer.isRunning()) {
            midiPlayer.stop();
        }
    }

    public static void closeMidi() {// close the midi player
        if (midiPlayer != null) {
            midiPlayer.close();
        }
    }
}