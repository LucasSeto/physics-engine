/*Utils.java
 * Lucas Seto
 * 
 * class for simple utilities that I repeatedly use throughout the game
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.swing.ImageIcon;

public class Utils {
    private static Font new_font;// make a new font
    private static Random rand = new Random();

    // Image objects for textures
    public static final Image black_king = new ImageIcon("assets/img/king.jpg").getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
    public static final Image revolver = new ImageIcon("assets/img/revolver.jpg").getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
    public static final Image crosshair = new ImageIcon("assets/img/crosshair.png").getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
    public static final Image bullet = new ImageIcon("assets/img/bullet.jpg").getImage().getScaledInstance(10, 10, Image.SCALE_DEFAULT);
    
    // public static final BufferedImage[][] walkSprite = loadPlayer("Farming_Game/assets/player/walk.png", 8, 4);
    // public static final BufferedImage[][] idleSprite = loadPlayer("Farming_Game/assets/player/idle.png", 4, 4);


    public Utils(){
        new_font = null;
    }
    // private static BufferedImage[][] loadPlayer(String filename, int row, int col){
    //     int height = 14;
    //     int width = 13;

    //     BufferedImage sprites = loadsheet(filename);
    //     BufferedImage[][] walkFrames = new BufferedImage[row][col];

    //     for (int r = 0; r < row; r++) {
    //         for (int c = 0; c < col; c++) {
    //             int x = r*(width+1); // x position of the frame
    //             int y = c*(height+2); // y position of the frame

    //             int newWidth = (int)(width*2);
    //             int newHeight = (int)(height*2);
    //             BufferedImage ogFrame = sprites.getSubimage(x, y, width, height);
    //             BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

    //             Graphics2D g2d = resized.createGraphics();
    //             g2d.drawImage(ogFrame.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
    //             g2d.dispose();
                
    //             walkFrames[r][c] = resized;
    //         }
    //     }
    //     return walkFrames;
    // }

    public static void playMusic(int gamestate){// method takes the gamestate to play the appropriate background music
        String newMusic = "";

        // Determine the new music based on the game state
        switch (gamestate) {
            case 0, 7, 8 -> { newMusic = "Farming_Game/assets/music/Title_pause.mid"; }// main menu music
            case 1, 2, 3, 5, 6-> { newMusic = "Farming_Game/assets/music/game.mid"; }// main game music
            case 4 -> { newMusic = "Farming_Game/assets/music/shop.mid"; }// shop music
        }
        
        // Only starts music if its different from the currently playing one
        if(!newMusic.equals(Music.current)){// differnt music (different gamestate)
            Music.stopMidi();// stop and close the previous midi
            Music.closeMidi();
            if(!newMusic.isEmpty()){// real file path
                Music.startMidi(newMusic);// start the midi with this new mid file
            }
            Music.current = newMusic;// set the current var to the new music to compare to later
        }
    }

    public static void renderText(Graphics g, String text, String font_file, int font_size, int red, int green, int blue, int x, int y){
        try{
            new_font = loadFont(font_file, font_size);// try loading the font
        } catch(FontFormatException ex){
            System.out.println(ex);	
        }

        g.setColor(new Color(red, green, blue));// sets the custom color
        g.setFont(new_font);// set the font to the loaded font
        g.drawString(text, x, y);// draw the custom text at the custom position
    }

    public static Font loadFont(String name, int size) throws FontFormatException{
        Font font = null;
        try {
            File fntFile = new File(name);// create a File object from the string of the .ttf file 
            font = Font.createFont(Font.TRUETYPE_FONT, fntFile).deriveFont((float)size);// create the font from the File object
        } catch(IOException | FontFormatException ex){
            System.out.println(ex);	
        }
        return font;
    }

    public static double[] toVector(double[] components){
        double magnitude = Math.sqrt(Math.pow(components[0], 2) + Math.pow(components[1], 2));// pythag theorum
        double ang = Math.atan2(components[1], components[0]);// tan inverse of the y over the x
        return new double[]{magnitude, ang};
    }
    public static int[] toComponent(double[] vector){
        double x_comp = vector[0]*Math.cos(vector[1]);// x component of the vector is the mag times the cos of theta
        double y_comp = vector[0]*Math.sin(vector[1]);// y component of the vector is the mag times the sin of theta
        return new int[]{(int)x_comp, (int)y_comp};
    }
}