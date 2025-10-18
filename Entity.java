
import java.awt.Rectangle;

public class Entity {
    protected int[] pos, vel;
    protected int size;
    protected Rectangle hitbox;
    
    protected int speed_cap;
    protected boolean jumping, falling, on_ground;
    protected RayCast vel_cast;

    protected RayCast left_vel_cast;
    protected RayCast right_vel_cast;
    protected RayCast top_vel_cast;
    protected RayCast bottom_vel_cast;

    public Entity(int[] pos, int[] vel, int size, int speed_cap){
        this.pos = pos;
        this.vel = vel;
        this.size = size;
        this.hitbox = new Rectangle(pos[0], pos[1], size, size + 1);

        this.speed_cap = speed_cap;
        this.jumping = false;
        this.falling = true;
        this.on_ground = true;
        this.vel_cast = new RayCast(pos[0], pos[1], pos[0] + vel[0], pos[1] + vel[1]);
    }

    public void move(){
        check_collision_with_platform();
        for (Repulsion repulsion : GamePanel.repulsions){
            repulsion.obj_vel_change(this);
        }
        // speed cap
        if (this.vel[1] > speed_cap){
            this.vel[1] = speed_cap;
        }
        if (this.vel[1] < -speed_cap){
            this.vel[1] = -speed_cap;
        }

        // pos change
        this.pos[0] += this.vel[0];
        this.pos[1] += this.vel[1];

        // vel change
        this.vel[0] *= 0.99;

        // Jumping vs Falling logic
        if (!this.on_ground){
            this.falling = true;
        }
        if (this.jumping){
            this.falling = false;
            this.vel[1] --;
        } 
        if (this.falling){
            this.vel[1] ++;
        }
        if (this.jumping && this.vel[1] <= -12){
            this.jumping = false;
            this.falling = true;
        }
    }
    

    public void update_hitboxs(){
        this.hitbox.x = this.pos[0] - this.size/2;
        this.hitbox.y = this.pos[1];
        this.vel_cast = new RayCast(pos[0], pos[1], pos[0] + vel[0], pos[1] + vel[1]);
    }
    public void check_collision_with_platform(){
        boolean intersection = false;
        for (Rectangle platform : GamePanel.platforms){
            
            if (platform.intersects(this.hitbox)){
                intersection = true;
                if (this.vel[1] >= 0){
                    if (this.vel[1] != 0){
                        this.vel[1] = 0;
                    }
                    if (!on_ground){
                        this.pos[1] = platform.y - this.size/2 - 1;
                    }
                    this.on_ground = true;
                    this.falling = false;
                }
            }
        }
        if (!intersection){
            this.on_ground = false;
        }
    }
}
