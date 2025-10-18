
import java.awt.Color;
import java.awt.Graphics2D;

public class Bullet extends Entity{
    public double[] vel_vector = new double[]{0, 0};
    private double an;
    public Bullet(int x, int y, int size, int speed, double ang){
        super(new int[]{x, y}, Utils.toComponent(new double[]{speed, ang}), size, 20);
        an = ang;
    }

    public void draw(Graphics2D g2D){
        g2D.setColor(new Color(255,255,0));
        g2D.drawImage(Utils.bullet, this.pos[0] - GamePanel.offset[0], this.pos[1] - GamePanel.offset[1], null);
    } 
    public void update(){
        this.pos[0] += this.vel[0];
        this.pos[1] += this.vel[1];
        this.hitbox.x = this.pos[0];
        this.hitbox.y = this.pos[1];
    }
}

