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
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable{
    // Constants
    public static final int SCREEN_WIDTH = 1500, SCREEN_HEIGHT = 1000;
    private static final double GRAVITATIONAL_CONSTANT = 10000;

    // Class objects
    private Thread thread;

    public static MouseHandler mouse = new MouseHandler();
    public static KeyHandler key = new KeyHandler();
    private Menu menu;

    public static Entity m1, m2, m3;
    private final List<Entity> entities = new ArrayList<>();
    private volatile boolean creativeMode;
    private volatile Entity draggedEntity;
    private double dragOffsetX, dragOffsetY;
    private int massInfoScroll;
    //public Vector momentumi, momentumf; 
    //boolean colliding;

    // Regular fields
    public int gamestate;
    private final int FPS = 60;
    private static final int PHYSICS_SUBSTEPS = 8;
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

        int orbitingMass = 10;
        double orbitLengthScale = 150;
        double velocityScale = Math.sqrt(GRAVITATIONAL_CONSTANT * orbitingMass / orbitLengthScale);
        double figureCenterX = SCREEN_WIDTH * 0.4;
        double figureCenterY = SCREEN_HEIGHT / 2.0;
        double initialX = 0.97000436 * orbitLengthScale;
        double initialY = 0.24308753 * orbitLengthScale;

        m1 = new Entity(
            new double[]{figureCenterX - initialX, figureCenterY - initialY},
            Utils.toVector(new double[]{0.4662036850 * velocityScale, -0.4323657300 * velocityScale}),
            orbitingMass);
        m2 = new Entity(
            new double[]{figureCenterX + initialX, figureCenterY + initialY},
            Utils.toVector(new double[]{0.4662036850 * velocityScale, -0.4323657300 * velocityScale}),
            orbitingMass);
        m3 = new Entity(
            new double[]{figureCenterX, figureCenterY},
            Utils.toVector(new double[]{-0.9324073700 * velocityScale, 0.8647314600 * velocityScale}),
            orbitingMass);
        entities.add(m1);
        entities.add(m2);
        entities.add(m3);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                handleCreativeMousePressed(event);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                draggedEntity = null;
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent event) {
                if (creativeMode && draggedEntity != null) {
                    draggedEntity.pos[0] = Math.max(draggedEntity.mass / 2.0,
                            Math.min(getWidth() - draggedEntity.mass / 2.0, event.getX() + dragOffsetX));
                    draggedEntity.pos[1] = Math.max(draggedEntity.mass / 2.0,
                            Math.min(getHeight() - draggedEntity.mass / 2.0, event.getY() + dragOffsetY));
                    repaint();
                }
            }
        });
        addMouseWheelListener(event -> {
            if (creativeMode && event.getX() >= getWidth() - 420) {
                synchronized (entities) {
                    massInfoScroll = Math.max(0, Math.min(
                            Math.max(0, entities.size() - VISIBLE_MASS_PANELS),
                            massInfoScroll + Integer.signum(event.getWheelRotation())));
                }
                repaint();
            }
        });

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
                if (creativeMode) {
                    break;
                }
                synchronized (entities) {
                    double timeStep = 1.0 / (FPS * PHYSICS_SUBSTEPS);
                    for (int step = 0; step < PHYSICS_SUBSTEPS; step++) {
                        updateGravitationalAccelerations();
                        for (Entity entity : entities) {
                            double velocityX = entity.vel.get_x() + entity.acc.get_x() * timeStep / 2;
                            double velocityY = entity.vel.get_y() + entity.acc.get_y() * timeStep / 2;
                            entity.pos[0] += velocityX * timeStep;
                            entity.pos[1] += velocityY * timeStep;
                            entity.vel = Utils.toVector(new double[]{velocityX, velocityY});
                        }

                        updateGravitationalAccelerations();
                        for (Entity entity : entities) {
                            double velocityX = entity.vel.get_x() + entity.acc.get_x() * timeStep / 2;
                            double velocityY = entity.vel.get_y() + entity.acc.get_y() * timeStep / 2;
                            entity.vel = Utils.toVector(new double[]{velocityX, velocityY});
                        }

                        for (int i = 0; i < entities.size(); i++) {
                            for (int j = i + 1; j < entities.size(); j++) {
                                resolveElasticCollision(entities.get(i), entities.get(j));
                            }
                        }
                    }
                }
            }
        }
        mouse.previous = mouse.pressed;
        key.previous = key.keys.clone();
        //offset = new int[]{player.pos[0] - SCREEN_WIDTH/2, player.pos[1] - SCREEN_HEIGHT/2};
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D)g.create();
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (gamestate) {
            case 0 -> {// 
                synchronized (entities) {
                    for (Entity entity : entities) {
                        entity.draw(g2D);
                    }
                    drawMassInfo(g2D);
                    drawCreativeControls(g2D);
                }
            }
        }
        g2D.dispose();
    }

    private void drawMassInfo(Graphics2D g2D) {
        int panelX = massPanelX();
        int panelY = massPanelY();
        int panelWidth = 390;
        int panelHeight = 100;
        int panelGap = 8;
        int endIndex = Math.min(entities.size(), massInfoScroll + VISIBLE_MASS_PANELS);

        for (int i = massInfoScroll; i < endIndex; i++) {
            Entity entity = entities.get(i);
            int row = i - massInfoScroll;
            int y = panelY + row * (panelHeight + panelGap);
            g2D.setColor(new Color(18, 24, 28, 220));
            g2D.fillRoundRect(panelX, y, panelWidth, panelHeight, 8, 8);
            g2D.setColor(new Color(92, 112, 120));
            g2D.drawRoundRect(panelX, y, panelWidth, panelHeight, 8, 8);

            g2D.setFont(g2D.getFont().deriveFont(java.awt.Font.BOLD, 12f));
            g2D.setColor(Color.WHITE);
            g2D.drawString("MASS " + (i + 1), panelX + 12, y + 19);
            g2D.setFont(g2D.getFont().deriveFont(java.awt.Font.PLAIN, 11f));
            g2D.drawString("Mass: " + entity.mass, panelX + 12, y + 39);

            g2D.setColor(new Color(70, 190, 255));
            g2D.drawString(formatVector("v", entity.vel, "px/s"), panelX + 12, y + 59);
            g2D.setColor(new Color(255, 174, 92));
            g2D.drawString(formatVector("a", entity.acc, "px/s^2"), panelX + 12, y + 78);

            if (creativeMode) {
                drawControlButton(g2D, massButtonBounds(row, "decrease"), "-");
                drawControlButton(g2D, massButtonBounds(row, "increase"), "+");
                drawControlButton(g2D, massButtonBounds(row, "delete"), "DEL");
            }
        }
        if (creativeMode && entities.size() > VISIBLE_MASS_PANELS) {
            g2D.setColor(Color.WHITE);
                g2D.drawString((massInfoScroll + 1) + "-" + endIndex + " / " + entities.size()
                    + "  (scroll to browse)", panelX + 12, panelY + VISIBLE_MASS_PANELS * (panelHeight + panelGap) + 4);
        }
    }

    private void drawCreativeControls(Graphics2D g2D) {
        drawControlButton(g2D, creativeButtonBounds(), creativeMode ? "Exit creative mode" : "Creative mode");
        if (creativeMode) {
            drawControlButton(g2D, addMassButtonBounds(), "+  Add mass");
            g2D.setColor(Color.WHITE);
            g2D.setFont(g2D.getFont().deriveFont(java.awt.Font.PLAIN, 12f));
            g2D.drawString("Drag a mass to reposition it", getWidth() - 220, 112);
        }
    }

    private void drawControlButton(Graphics2D g2D, Rectangle bounds, String label) {
        g2D.setColor(new Color(42, 58, 65));
        g2D.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 7, 7);
        g2D.setColor(new Color(120, 164, 174));
        g2D.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 7, 7);
        g2D.setColor(Color.WHITE);
        g2D.setFont(g2D.getFont().deriveFont(java.awt.Font.BOLD, 12f));
        int textWidth = g2D.getFontMetrics().stringWidth(label);
        g2D.drawString(label, bounds.x + (bounds.width - textWidth) / 2,
                bounds.y + (bounds.height + g2D.getFontMetrics().getAscent()) / 2 - 2);
    }

    private Rectangle creativeButtonBounds() {
        return new Rectangle(getWidth() - 216, 16, 200, 38);
    }

    private static final int VISIBLE_MASS_PANELS = 8;

    private Rectangle addMassButtonBounds() {
        return new Rectangle(getWidth() - 216, 62, 200, 38);
    }

    private Rectangle massButtonBounds(int row, String action) {
        int x = massPanelX() + switch (action) {
            case "decrease" -> 278;
            case "increase" -> 310;
            default -> 342;
        };
        int width = "delete".equals(action) ? 38 : 26;
        return new Rectangle(x, massPanelY() + row * 108 + 26, width, 26);
    }

    private Rectangle massInfoBounds(int row) {
        return new Rectangle(massPanelX(), massPanelY() + row * 108, 390, 100);
    }

    private int massPanelX() {
        return getWidth() - 406;
    }

    private int massPanelY() {
        return 114;
    }

    private void handleCreativeMousePressed(MouseEvent event) {
        if (event.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        requestFocusInWindow();
        if (creativeButtonBounds().contains(event.getPoint())) {
            creativeMode = !creativeMode;
            draggedEntity = null;
            repaint();
            return;
        }
        if (!creativeMode) {
            return;
        }
        if (addMassButtonBounds().contains(event.getPoint())) {
            synchronized (entities) {
                entities.add(new Entity(new double[]{getWidth() / 2.0, getHeight() / 2.0}, new Vector(0, 0), 30));
            }
            repaint();
            return;
        }

        synchronized (entities) {
            int endIndex = Math.min(entities.size(), massInfoScroll + VISIBLE_MASS_PANELS);
            for (int i = massInfoScroll; i < endIndex; i++) {
                int row = i - massInfoScroll;
                Entity entity = entities.get(i);
                if (massButtonBounds(row, "decrease").contains(event.getPoint())) {
                    entity.mass = Math.max(10, entity.mass - 5);
                    repaint();
                    return;
                }
                if (massButtonBounds(row, "increase").contains(event.getPoint())) {
                    entity.mass = Math.min(500, entity.mass + 5);
                    repaint();
                    return;
                }
                if (massButtonBounds(row, "delete").contains(event.getPoint())) {
                    entities.remove(i);
                    massInfoScroll = Math.min(massInfoScroll, Math.max(0, entities.size() - VISIBLE_MASS_PANELS));
                    repaint();
                    return;
                }
                if (massInfoBounds(row).contains(event.getPoint())) {
                    return;
                }
            }

            for (int i = entities.size() - 1; i >= 0; i--) {
                Entity entity = entities.get(i);
                if (Math.hypot(event.getX() - entity.pos[0], event.getY() - entity.pos[1]) <= entity.mass / 2.0) {
                    draggedEntity = entity;
                    dragOffsetX = entity.pos[0] - event.getX();
                    dragOffsetY = entity.pos[1] - event.getY();
                    entity.vel = new Vector(0, 0);
                    entity.acc = new Vector(0, 0);
                    return;
                }
            }
        }
    }

    private String formatVector(String label, Vector vector, String unit) {
        return String.format(Locale.ROOT, "%s %.1f %s @ %.0f deg", label, vector.get_mag(), unit, vector.get_ang());
    }

    private void updateGravitationalAccelerations() {
        for (Entity entity : entities) {
            Vector netForce = new Vector(0, 0);
            for (Entity other : entities) {
                if (entity != other) {
                    netForce = Utils.add_vector(netForce, universal_gravitation(entity, other));
                }
            }
            entity.fnet = netForce;
            if (entity.mass > 0) {
                entity.acc = Utils.toVector(new double[]{
                    netForce.get_x() / entity.mass,
                    netForce.get_y() / entity.mass
                });
            } else {
                entity.acc = new Vector(0, 0);
            }
        }
    }

    private void resolveElasticCollision(Entity first, Entity second) {
        double dx = second.pos[0] - first.pos[0];
        double dy = second.pos[1] - first.pos[1];
        double distance = Math.hypot(dx, dy);
        double radius = (first.mass + second.mass) / 2.0;
        if (distance > radius || first.mass <= 0 || second.mass <= 0) {
            return;
        }

        double normalX = distance == 0 ? 1 : dx / distance;
        double normalY = distance == 0 ? 0 : dy / distance;
        double inverseMassFirst = 1.0 / first.mass;
        double inverseMassSecond = 1.0 / second.mass;
        double inverseMassSum = inverseMassFirst + inverseMassSecond;

        double relativeVelocityX = second.vel.get_x() - first.vel.get_x();
        double relativeVelocityY = second.vel.get_y() - first.vel.get_y();
        double velocityAlongNormal = relativeVelocityX * normalX + relativeVelocityY * normalY;
        if (velocityAlongNormal < 0) {
            double impulse = -2 * velocityAlongNormal / inverseMassSum;
            first.vel = Utils.toVector(new double[]{
                first.vel.get_x() - impulse * inverseMassFirst * normalX,
                first.vel.get_y() - impulse * inverseMassFirst * normalY
            });
            second.vel = Utils.toVector(new double[]{
                second.vel.get_x() + impulse * inverseMassSecond * normalX,
                second.vel.get_y() + impulse * inverseMassSecond * normalY
            });
        }

        double overlap = radius - distance;
        double penetrationBeyondSlop = Math.max(0, overlap - 0.25);
        if (penetrationBeyondSlop > 0) {
            double correctionX = penetrationBeyondSlop * normalX / inverseMassSum;
            double correctionY = penetrationBeyondSlop * normalY / inverseMassSum;
            first.pos[0] -= correctionX * inverseMassFirst;
            first.pos[1] -= correctionY * inverseMassFirst;
            second.pos[0] += correctionX * inverseMassSecond;
            second.pos[1] += correctionY * inverseMassSecond;
        }
    }

    public Vector universal_gravitation(Entity m1, Entity m2){
        double delta_x = m2.pos[0] - m1.pos[0];
        double delta_y = m2.pos[1] - m1.pos[1];
        double delta_d = Math.sqrt(Math.pow(delta_y, 2) + Math.pow(delta_x, 2));
        if (delta_d == 0) {
            return new Vector(0, 0);
        }
        Vector fg = new Vector(GRAVITATIONAL_CONSTANT*m1.mass*m2.mass/Math.pow(delta_d, 2), (360+Math.toDegrees(Math.atan2(delta_y, delta_x)))%360);
        return fg;
    }
    public Vector normal_force(Entity m1, Entity m2){
        
        double delta_x = m2.pos[0] - m1.pos[0];
        double delta_y = m2.pos[1] - m1.pos[1];
        Vector fn = new Vector(m1.vel.get_mag(), 180+((360+Math.toDegrees(Math.atan2(delta_y, delta_x)))%360));
        return fn;
    }
}