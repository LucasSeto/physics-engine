/*Mouse_input.java
 * Lucas Seto
 * 
 * class to handle mouse inputs
 */

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseHandler implements MouseListener, MouseWheelListener{

    public int[] pos;// array for the position of the mouse
    public boolean pressed, left_click, right_click, scroll;// bools for the mouse states
    public boolean previous;// previous mouse press bool (last frame)

    public int scroll_direction; // -1 for up, 1 for down
    public final int UP = -1, DOWN = 1;
    public int scroll_amount;  

    public MouseHandler(){
        this.pos = new int[]{0, 0};

        this.pressed = false;
        this.left_click = false;
        this.right_click = false;
        this.scroll = false;

        this.scroll_direction = 0; // neutral at start
        this.scroll_amount = 0;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scroll = true;
        scroll_direction = Integer.signum(e.getWheelRotation()); // -1 (up) or 1 (down)
        scroll_amount = e.getScrollAmount() * e.getWheelRotation();// getting the amount of scroll 
    }

    public void update(){
        decrease_scroll();// actively decrease the scroll
    }

    public void decrease_scroll(){// method to decrease the scroll
        if (scroll_amount > 0){
            scroll_amount -= 1;
        }
        else if (scroll_amount < 0){
            scroll_amount += 1;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.pressed = true;

        switch (e.getButton()) {
            case MouseEvent.BUTTON1 -> left_click = true;   // Left button
            case MouseEvent.BUTTON2 -> scroll = true;      // Middle button (scroll wheel)
            case MouseEvent.BUTTON3 -> right_click = true; // Right button
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.pressed = false;

        switch (e.getButton()) {
            case MouseEvent.BUTTON1 -> left_click = false;   // Left button
            case MouseEvent.BUTTON2 -> scroll = false;       // Middle button (scroll wheel)
            case MouseEvent.BUTTON3 -> right_click = false;  // Right button
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}