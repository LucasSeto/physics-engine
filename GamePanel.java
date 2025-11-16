/*GamePanel.java
 * Lucas Seto
 * 
 * main class for updating and painting the game
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable{
    // Constants
    public static final int SCREEN_WIDTH = 800, SCREEN_HEIGHT = 600;

    // Class objects
    private Thread thread;

    public static MouseHandler mouse = new MouseHandler();
    public static KeyHandler key = new KeyHandler();
    private Menu menu;
    public static Player player;
    public static Entity m2;
    public ArrayList<Entity> entity_list;

    // Regular fields
    public int gamestate;
    private final int FPS = 60;
    public static int[] offset;

    public GamePanel() {// Constructor
        this.thread = new Thread();
        this.menu = new Menu();

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.DARK_GRAY);
        this.addMouseListener(mouse);
        this.addMouseWheelListener(mouse);
        this.addKeyListener(key);
        this.setFocusable(true);

        this.entity_list = new ArrayList<>();
        player = new Player(250, 100, 25);
        m2 = new Entity(new double[]{500,300}, new Vector(0,0), 100);
        this.entity_list.add(player);
        this.entity_list.add(m2);

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
        try {// try to set the mouse position to where it is relative to the JPanel
            mouse.pos[0] = getMousePosition().x;
            mouse.pos[1] = getMousePosition().y;
        } catch (Exception e) {// incase mouse leaves the screen
        }

        switch (gamestate){
            case 0 -> {
                //Player
                player.update();
                m2.move();
                m2.update_hitboxs();
                player.fnet = universal_gravitation(player, m2);
                //m2.fnet = Utils.add_vector(player.fnet, universal_gravitation(player, m2));
                System.out.println(player.vel.get_ang());
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

                player.draw(g2D);
                Utils.renderText(g2D, ""+player.pos[0], "assets/MinimalPixelFont.ttf", 30, 255, 255, 255, 100, 100);
                Utils.renderText(g2D, ""+player.pos[1], "assets/MinimalPixelFont.ttf", 30, 255, 255, 255, 150, 120);
                
                Utils.renderText(g2D, ""+player.vel.get_mag(), "assets/MinimalPixelFont.ttf", 30, 0, 0, 255, 100, 150);
                Utils.renderText(g2D, ""+player.vel.get_ang(), "assets/MinimalPixelFont.ttf", 30, 0, 0, 255, 150, 170);

                Utils.renderText(g2D, ""+player.acc.get_mag(), "assets/MinimalPixelFont.ttf", 30, 255, 0, 0, 100, 200);
                Utils.renderText(g2D, ""+player.acc.get_ang(), "assets/MinimalPixelFont.ttf", 30, 255, 0, 0, 150, 220);
                player.vel.draw(g2D, player.pos[0], player.pos[1]);

                Utils.renderText(g2D, ""+player.fnet.get_mag(), "assets/MinimalPixelFont.ttf", 30, 255, 255, 0, 100, 250);
                Utils.renderText(g2D, ""+player.fnet.get_ang(), "assets/MinimalPixelFont.ttf", 30, 255, 255, 0, 150, 270);
                m2.draw(g2D);

            }
        }
        
        g2D.drawImage(Utils.crosshair, mouse.pos[0] - 25, mouse.pos[1] - 25, null);
        g2D.dispose();
    }

    public Vector universal_gravitation(Entity m1, Entity m2){
        double delta_x = m2.pos[0] - m1.pos[0];
        double delta_y = m2.pos[1] - m1.pos[1];
        double delta_d = Math.sqrt(Math.pow(delta_y, 2) + Math.pow(delta_x, 2));
        Vector fg = new Vector(0.00667*m1.mass*m2.mass/Math.pow(delta_d, 2), (360-Math.toDegrees(Math.atan2(delta_y, delta_x)))%360);
        return fg;
    }
}