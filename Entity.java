
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Entity {
    protected double[] pos;
    protected Vector vel, acc, fnet;
    protected int mass;
    protected Rectangle hitbox;
    
    protected RayCast vel_cast;


    public Entity(double[] pos, Vector vel, int mass){
        this.pos = pos;
        this.vel = vel;
        this.acc = new Vector(0, 0);
        this.fnet = new Vector(0, 0);
        this.mass = mass;
        this.hitbox = new Rectangle((int)pos[0] - this.mass/2, (int)pos[1] - this.mass/2, mass, mass);

    }

    public void move(){

        // pos change
        this.pos[0] += this.vel.get_x();
        this.pos[1] += this.vel.get_y();

        //accelerate
        this.vel = Utils.add_vector(this.vel, this.acc);
        this.acc.set_mag(fnet.get_mag()/this.mass);
        this.acc.set_ang(fnet.get_ang());

    }

    public void draw(Graphics2D g2D){
        g2D.setColor(Color.WHITE);
        g2D.fillRect((int)this.pos[0] - mass/2, (int)this.pos[1] - mass/2, mass, mass);
    }

    public void update_hitboxs(){
        this.hitbox.x = (int)this.pos[0] - this.mass/2;
        this.hitbox.y = (int)this.pos[1] - this.mass/2;
    }
}
