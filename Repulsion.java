
import java.awt.Color;
import java.awt.Graphics2D;

public class Repulsion {
    public int radius;
    public int[] pos;
    public int status;
    private final int max_size, vel_factor;

    public Repulsion(int max_size, int x, int y){
        this.pos = new int[]{x, y};
        this.status = 1;// var for toggling the increase and decrease of the radius
        this.max_size = max_size;
        this.vel_factor = (int)(max_size*0.3);
    }

    public void draw(Graphics2D g2D){
        g2D.setColor(Color.red);
        g2D.fillOval(this.pos[0] - this.radius - GamePanel.offset[0], this.pos[1] - this.radius - GamePanel.offset[1], this.radius*2, this.radius*2);
    }
    public void update(){
        if (this.radius >= this.max_size){
            this.status = 0;
        }
        switch (this.status) {
            case 1 -> {
                this.radius += (int)(this.max_size/10);
            }
            case 0 -> {
                this.radius -= (int)(this.max_size/30);
            }
        }
    }

    public void obj_vel_change(Entity e){
        int dist = (int)(Math.sqrt(Math.pow(e.pos[0] - this.pos[0], 2) + Math.pow(e.pos[1] - this.pos[1], 2)));
        if (dist <= this.radius){
            double ang = Utils.toVector(new double[]{this.pos[0] - e.pos[0], this.pos[1] - e.pos[1]})[1];
            e.vel[0] += this.vel_factor*Math.cos(ang + Math.PI);
            e.vel[1] += this.vel_factor*Math.sin(ang + Math.PI);
        }
    }
}
