/*GamePanel.java
 * Lucas Seto
 * 
 * main class for updating and painting the game
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable{
    // Constants
    public static final int SCREEN_WIDTH = 1000, SCREEN_HEIGHT = 600;

    // Class objects
    private Thread thread;

    public static MouseHandler mouse = new MouseHandler();
    public static KeyHandler key = new KeyHandler();
    private Menu menu;

    public static Entity m1, m2, m3;
    public Vector momentumi, momentumf; 
    boolean colliding = false;

    // Regular fields
    public int gamestate;
    private final int FPS = 60;
    public static int[] offset;

    public static double time = 0;

    public GamePanel() {// Constructor
        this.thread = new Thread();
        this.menu = new Menu();

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.DARK_GRAY);
        this.addMouseListener(mouse);
        this.addMouseWheelListener(mouse);
        this.addKeyListener(key);
        this.setFocusable(true);

        m1 = new Entity(new double[]{100,300}, new Vector(50,0), 25);
        m2 = new Entity(new double[]{800,300}, new Vector(0,0), 100);
        //m3 = new Entity(new double[]{700,200}, new Vector(50,90), 50);

        this.gamestate = 0;
        //offset = new int[]{player.pos[0] - SCREEN_WIDTH/2, player.pos[1] - SCREEN_HEIGHT/2};
    }

    public void startGameThread(){// Starts the game loop
        this.thread = new Thread(this);
        this.thread.start();
    }
    @Override
    public void run() {//1 second in nano secs per frame
        double frame_interval = 1000000000/FPS;// var for the amount of nanoseconds between frames 
        double next_frame_time = System.nanoTime() + frame_interval;// time for the next frame is the current time plus the interval
        while(thread != null){// Game loop
            this.update();
            this.repaint();
            try{
                double remaining_time = next_frame_time - System.nanoTime();// var for the remaining time before thenext frame
                remaining_time /= 1000000;
                if (remaining_time < 0){
                    remaining_time = 0;
                }
                Thread.sleep((long) remaining_time);
                next_frame_time += frame_interval;// the system time for the next frame is moved up one second (frame interval)
            }catch (InterruptedException e){// catch error
            }
        }
    }

    public void update(){
        time += (double)1/60;
        try {// try to set the mouse position to where it is relative to the JPanel
            mouse.pos[0] = getMousePosition().x;
            mouse.pos[1] = getMousePosition().y;
        } catch (Exception e) {// incase mouse leaves the screen
        }

        switch (gamestate){
            case 0 -> {
                m1.move();
                m2.move();
                //m3.move();

                // m1.fnet = Utils.add_vector(universal_gravitation(m1, m2), universal_gravitation(m1, m3));
                // m2.fnet = Utils.add_vector(universal_gravitation(m2, m1), universal_gravitation(m2, m3));
                // m3.fnet = Utils.add_vector(universal_gravitation(m3, m2), universal_gravitation(m3, m1));

                System.out.println(this.momentumi);
                if (m1.colliding(m2)){
                    if (!colliding){
                        this.momentumi = Utils.add_vector(m1.momentum, m2.momentum);
                    }
                    this.colliding = true;
                    m1.fnet = Utils.add_vector(m1.fnet, normal_force(m1, m2));
                } else {
                    this.colliding = false;
                    m1.fnet.set_mag(0);
                    m1.fnet.set_ang(0);
                }
                // if (m3.colliding(m2)){
                //     this.colliding = true;
                //     m3.fnet = normal_force(m3, m2);
                // } else {
                //     this.colliding = false;
                // }
                // if (m1.colliding(m3)){
                //     this.colliding = true;
                //     m1.fnet = normal_force(m1, m3);
                // } else {
                //     this.colliding = false;
                // }
                
            }
        }
        mouse.previous = mouse.pressed;
        key.previous = key.keys.clone();
        //offset = new int[]{player.pos[0] - SCREEN_WIDTH/2, player.pos[1] - SCREEN_HEIGHT/2};
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D)g;

        switch (gamestate) {
            case 0 -> {// 
                m1.draw(g2D);
                m2.draw(g2D);
                //m3.draw(g2D);
            }
        }
        g2D.dispose();
    }

    public Vector universal_gravitation(Entity m1, Entity m2){
        double delta_x = m2.pos[0] - m1.pos[0];
        double delta_y = m2.pos[1] - m1.pos[1];
        double delta_d = Math.sqrt(Math.pow(delta_y, 2) + Math.pow(delta_x, 2));
        Vector fg = new Vector(100*m1.mass*m2.mass/Math.pow(delta_d, 2), (360+Math.toDegrees(Math.atan2(delta_y, delta_x)))%360);
        return fg;
    }
    public Vector normal_force(Entity m1, Entity m2){
        
        double delta_x = m2.pos[0] - m1.pos[0];
        double delta_y = m2.pos[1] - m1.pos[1];
        Vector fn = new Vector(m1.vel.get_mag(), 180+((360+Math.toDegrees(Math.atan2(delta_y, delta_x)))%360));
        return fn;
    }
}