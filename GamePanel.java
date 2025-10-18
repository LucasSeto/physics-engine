/*GamePanel.java
 * Lucas Seto
 * 
 * main class for updating and painting the game
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable{
    // Constants
    public static final int SCREEN_WIDTH = 800, SCREEN_HEIGHT = 600;

    // Class objects
    private Thread thread;
    private static Random rand;

    public static MouseHandler mouse = new MouseHandler();
    public static KeyHandler key = new KeyHandler();
    private Menu menu;
    public static Player player;
    
    public static ArrayList<Rectangle> platforms;
    public static ArrayList<Repulsion> repulsions;
    public static ArrayList<Bullet> bullets;

    // Regular fields
    public int gamestate;
    private final int FPS = 60;
    public static int[] offset;

    public GamePanel() {// Constructor
        this.thread = new Thread();
        this.menu = new Menu();
        rand = new Random();

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.DARK_GRAY);
        this.addMouseListener(mouse);
        this.addMouseWheelListener(mouse);
        this.addKeyListener(key);
        this.setFocusable(true);

        player = new Player(250, 100);
        this.gamestate = 0;
        offset = new int[]{player.pos[0] - SCREEN_WIDTH/2, player.pos[1] - SCREEN_HEIGHT/2};

        platforms = new ArrayList<>();
        platforms.add(new Rectangle(0, 400, SCREEN_WIDTH, 200));
        
        platforms.add(new Rectangle(100, 370, 100, 20));
        platforms.add(new Rectangle(150, 320, 100, 20));
        
        platforms.add(new Rectangle(200, 270, 100, 20));
        platforms.add(new Rectangle(250, 220, 100, 20));

        repulsions = new ArrayList<>();

        bullets = new ArrayList<>();
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
                if (mouse.left_click && !mouse.previous){
                    bullets.add(new Bullet(player.pos[0], player.pos[1], 5, 10, player.angle));
                }
                // Repulsions
                for (int i = repulsions.size() - 1; i >= 0; i --){
                    repulsions.get(i).update();
                    if (repulsions.get(i).radius <= 0){
                        repulsions.remove(i);
                    }
                }
                // Bullets
                for (int i = bullets.size() - 1; i >= 0; i --){
                    bullets.get(i).update();
                    for (Rectangle platform : platforms){
                        if (bullets.get(i).hitbox.intersects(platform)){
                            repulsions.add(new Repulsion(40, bullets.get(i).pos[0], bullets.get(i).pos[1]));
                            bullets.remove(i);
                            break;
                        }
                    }
                }
            }
        }
        mouse.previous = mouse.pressed;
        key.previous = key.keys.clone();
        offset = new int[]{player.pos[0] - SCREEN_WIDTH/2, player.pos[1] - SCREEN_HEIGHT/2};
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D)g;

        switch (gamestate) {
            case 0 -> {// 

                g2D.setColor(Color.gray);
                for (Rectangle platform : platforms){
                    g2D.fillRect(platform.x - GamePanel.offset[0], platform.y - GamePanel.offset[1], platform.width, platform.height);
                }

                for (Repulsion repulsion : repulsions){
                    repulsion.draw(g2D);
                }

                for (Bullet bullet : bullets){
                    bullet.draw(g2D);
                }
                Utils.renderText(g2D, "BINGOID","assets/MinimalPixelFont.ttf", 100, 255, 255, 255, 200 - offset[0], 150 - offset[1]);
                
                player.draw(g2D);
            }
        }
        
        g2D.drawImage(Utils.crosshair, mouse.pos[0] - 25, mouse.pos[1] - 25, null);
        g2D.dispose();
    }
}