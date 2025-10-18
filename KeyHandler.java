/*Keyboard_input
 * Angie Huang
 * 
 * class for handling keyboard input
 */

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// Keeps track of key interactions
public class KeyHandler implements KeyListener {
    public boolean[] keys;         // keeps track of all keys (true if pressed, false if released)
    public boolean[] previous;      // tracks the state of keys in previous frame

    public boolean left;

    // constructor--> init key arrays and WASD tracking map
    public KeyHandler(){  
        keys = new boolean[KeyEvent.KEY_LAST + 1];
        this.previous = new boolean[2000];
    }
    // Handle key press events, marking key as pressed
    @Override
    public void keyPressed(KeyEvent e){ // sets key index to true if there's a key interaction
        int key = e.getKeyCode();  
        keys[key] = true;
    }

    // Handles key release events
    @Override
    public void keyReleased(KeyEvent e){    // sets key index to false if released
        int key = e.getKeyCode();
        keys[key] = false; 
    }

    @Override
    public void keyTyped(KeyEvent e){}
}
