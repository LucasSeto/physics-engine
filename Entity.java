
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Entity {
    protected double[] pos;
    protected Vector vel, acc, fnet, fg, momentum;
    protected int mass, spring_constant;
    protected Rectangle hitbox;
    
    protected RayCast vel_cast;


    public Entity(double[] pos, Vector vel, int mass){
        this.pos = pos;

        this.vel = vel;
        this.acc = new Vector(0, 0);
        this.fnet = new Vector(0, 0);
        this.fg = new Vector(0, 0);
        this.momentum = new Vector(0, 0);

        this.mass = mass;
        this.spring_constant = 1;

        this.hitbox = new Rectangle((int)pos[0] - this.mass/2, (int)pos[1] - this.mass/2, mass, mass);
    }

    public void move(){

        // pos change
        this.pos[0] += this.vel.get_x()/60;
        this.pos[1] += this.vel.get_y()/60;

        //accelerate
        this.vel = Utils.add_vector(this.vel, this.acc);
        this.acc.set_mag(fnet.get_mag()/this.mass);
        this.acc.set_ang(fnet.get_ang());

        this.momentum.set_mag(this.mass * this.vel.get_mag());
        this.momentum.set_ang(vel.get_ang());

    }

    public void draw(Graphics2D g2D){
        g2D.setColor(Color.WHITE);
        g2D.fillOval((int)this.pos[0] - mass/2, (int)this.pos[1] - mass/2, mass, mass);
        this.vel.draw(g2D, this.pos[0], this.pos[1], Color.BLUE);
        this.fnet.draw(g2D, this.pos[0], this.pos[1], Color.RED);
    }

    public boolean colliding(Entity m1){
        double delta_d = Math.sqrt(Math.pow((int)(this.pos[0] - m1.pos[0]), 2) + Math.pow((int)(this.pos[1] - m1.pos[1]), 2));
        return delta_d <= m1.mass/2 + this.mass/2;
    }
}
