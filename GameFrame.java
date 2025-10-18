/*GameFrame.java
 * Lucas Seto
 * 
 * Jframe for the panel
 */

import javax.swing.JFrame;

public class GameFrame extends JFrame{

    public static GamePanel panel;
    
    public static Thread thread;

    public GameFrame(){
        super("King's Desperado");
        thread = new Thread();
        panel = new GamePanel();

        this.add(panel);
        this.pack();

        this.setDefaultCloseOperation(3);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);
    }
}
