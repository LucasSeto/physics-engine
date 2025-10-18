/*Player.java
 * Lucas Seto
 * 
 * The player and its interactions
 *      -
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class Player extends Entity{
    public double angle, stamina;
    private int sliding_dir;

    // Constuctor 
    public Player(int px, int py){
        super(new int[]{px, py}, new int[]{0, 0}, 25, 20);

        this.pos = new int[]{px, py};
        this.vel = new int[]{0, 0};
        this.size = 25;
        sliding_dir = 1;

        //this.falling = true;
        this.angle = Math.atan2(GamePanel.mouse.pos[1] - this.pos[1], GamePanel.mouse.pos[0] - this.pos[0]);

        this.stamina = 3.01;
    }

    public void draw(Graphics2D g2D){
        g2D.setColor(new Color(255));
        g2D.drawImage(Utils.black_king, pos[0] - size/2 - GamePanel.offset[0], pos[1] - size/2 - GamePanel.offset[1], null);
        g2D.drawLine(this.pos[0] - GamePanel.offset[0], this.pos[1] - GamePanel.offset[1], (int)vel_cast.end_point.x - GamePanel.offset[0], (int)vel_cast.end_point.y - GamePanel.offset[1]);
        draw_gui(g2D);
    }
    public void draw_gui(Graphics2D g2D){
        for (int i = 1; i <= 3; i ++){
            g2D.setColor(new Color(0));
            g2D.drawRect(100 * i, 70, 75, 30);
            g2D.setColor(new Color(0, 255, 255));
            g2D.fillRect(100 * i, 70, (int)(this.stamina-i)*75, 30);
        }
    }
    public void update()
    {
        this.angle = Math.atan2(GamePanel.mouse.pos[1] - GamePanel.SCREEN_HEIGHT/2, GamePanel.mouse.pos[0] - GamePanel.SCREEN_WIDTH/2);
        this.inputs();
        this.move();
        this.update_hitboxs();
        this.regen_stamina();
    }

    public void regen_stamina(){
        if (this.stamina < 3 && !GamePanel.key.keys[KeyEvent.VK_CONTROL]){
            this.stamina += 0.01;
        }
    }

    public void inputs(){
        if (this.vel[0] < 10 && this.vel[0] > -10){
            if (GamePanel.key.keys[KeyEvent.VK_A] && this.vel[0] <= Math.abs(this.speed_cap)){
                this.vel[0] -= 2;
            } else if (GamePanel.key.keys[KeyEvent.VK_D] && this.vel[0] <= Math.abs(this.speed_cap)){
                this.vel[0] += 2;
            }
            if (GamePanel.key.keys[KeyEvent.VK_W] && this.on_ground){
                this.jumping = true;
            }
        }
        if (GamePanel.key.keys[KeyEvent.VK_SHIFT] && !GamePanel.key.previous[KeyEvent.VK_SHIFT] && this.stamina >= 1){// dash
            vel[0] += 30*Math.cos(angle);
            vel[1] += 30*Math.sin(angle);
            this.stamina --;
        }
        if (GamePanel.key.keys[KeyEvent.VK_CONTROL] && this.on_ground){// slide
            if (GamePanel.key.keys[KeyEvent.VK_CONTROL] && !GamePanel.key.previous[KeyEvent.VK_CONTROL]){
                if (Math.toDegrees(angle) >= -90 && Math.toDegrees(angle) <= 90){
                    this.sliding_dir = 1;
                } else {
                    this.sliding_dir = -1;
                }
            } 
            this.vel[0] = 15 * this.sliding_dir;
        }
    }
}