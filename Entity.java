
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Entity {
    private static final Color VELOCITY_COLOR = new Color(70, 190, 255);
    private static final Color ACCELERATION_COLOR = new Color(255, 174, 92);
    private static final double VELOCITY_ARROW_SCALE = 0.6;
    private static final double ACCELERATION_ARROW_SCALE = 450;
    private static final double MAX_VECTOR_ARROW_LENGTH = 78;

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
        double timeStep = 1.0 / 60;
        double accelerationX = mass > 0 ? fnet.get_x() / mass : 0;
        double accelerationY = mass > 0 ? fnet.get_y() / mass : 0;
        double velocityX = vel.get_x();
        double velocityY = vel.get_y();

        this.pos[0] += velocityX * timeStep + 0.5 * accelerationX * timeStep * timeStep;
        this.pos[1] += velocityY * timeStep + 0.5 * accelerationY * timeStep * timeStep;

        this.vel = Utils.toVector(new double[]{
            velocityX + accelerationX * timeStep,
            velocityY + accelerationY * timeStep
        });
        this.acc = Utils.toVector(new double[]{accelerationX, accelerationY});

        this.momentum.set_mag(this.mass * this.vel.get_mag());
        this.momentum.set_ang(vel.get_ang());

    }

    public void draw(Graphics2D g2D){
        g2D.setColor(Color.WHITE);
        g2D.fillOval((int)this.pos[0] - mass/2, (int)this.pos[1] - mass/2, mass, mass);
        g2D.setColor(new Color(80, 90, 96));
        g2D.drawOval((int)this.pos[0] - mass/2, (int)this.pos[1] - mass/2, mass, mass);
        this.vel.draw(g2D, this.pos[0], this.pos[1], VELOCITY_COLOR, VELOCITY_ARROW_SCALE, MAX_VECTOR_ARROW_LENGTH);
        this.acc.draw(g2D, this.pos[0], this.pos[1], ACCELERATION_COLOR, ACCELERATION_ARROW_SCALE, MAX_VECTOR_ARROW_LENGTH);
    }

    public boolean colliding(Entity m1){
        double delta_d = Math.hypot(this.pos[0] - m1.pos[0], this.pos[1] - m1.pos[1]);
        return delta_d <= (m1.mass + this.mass) / 2.0;
    }
}
