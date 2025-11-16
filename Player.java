/*Player.java
 * Lucas Seto
 * 
 * The player and its interactions
 *      -
 */

import java.awt.event.KeyEvent;

public class Player extends Entity{

    // Constuctor 
    public Player(double px, double py, int mass){
        super(new double[]{px, py}, new Vector(1,270), mass);

    }

    public void update()
    {
        this.inputs();
        this.move();
        this.update_hitboxs();
    }

    public void inputs(){
        if (GamePanel.key.keys[KeyEvent.VK_A]){
            this.fnet.set_mag(this.fnet.get_mag() + 0.05);
        }
    }
}