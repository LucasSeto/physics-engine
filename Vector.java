
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

public class Vector {
    public double magnitude;
    public double angle;

    public Vector(double mag, double ang){
        this.magnitude = mag;
        this.angle = ang;
    }

    public void draw(Graphics2D g2D, double x, double y, Color c){
        draw(g2D, x, y, c, 1, Double.POSITIVE_INFINITY);
    }

    public void draw(Graphics2D g2D, double x, double y, Color c, double scale, double maxLength){
        double[] vectorComponents = components();
        double dx = vectorComponents[0] * scale;
        double dy = vectorComponents[1] * scale;
        double vectorLength = Math.hypot(dx, dy);
        if (vectorLength == 0 || !Double.isFinite(vectorLength) || maxLength <= 0){
            return;
        }

        double arrowLength = Math.min(vectorLength, maxLength);
        double directionX = dx / vectorLength;
        double directionY = dy / vectorLength;
        double endX = x + directionX * arrowLength;
        double endY = y + directionY * arrowLength;
        double headLength = Math.min(12, arrowLength * 0.4);
        double headBaseX = endX - directionX * headLength;
        double headBaseY = endY - directionY * headLength;
        double headHalfWidth = headLength * 0.55;
        double wingOneX = headBaseX - directionY * headHalfWidth;
        double wingOneY = headBaseY + directionX * headHalfWidth;
        double wingTwoX = headBaseX + directionY * headHalfWidth;
        double wingTwoY = headBaseY - directionX * headHalfWidth;

        g2D.setColor(c);
        g2D.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2D.drawLine((int)x, (int)y, (int)headBaseX, (int)headBaseY);
        Path2D arrowHead = new Path2D.Double();
        arrowHead.moveTo(endX, endY);
        arrowHead.lineTo(wingOneX, wingOneY);
        arrowHead.lineTo(wingTwoX, wingTwoY);
        arrowHead.closePath();
        g2D.fill(arrowHead);
    }

    public double[] components(){
        double x_comp = this.magnitude*Math.cos(Math.toRadians(angle));// x component of the vector is the mag times the cos of theta
        double y_comp = this.magnitude*Math.sin(Math.toRadians(angle));// y component of the vector is the mag times the sin of theta
        return new double[]{x_comp, y_comp};
    }

    public double get_x(){
        return components()[0];
    }
    public double get_y(){
        return components()[1];
    }
    public double get_mag(){
        return this.magnitude;
    }
    public double get_ang(){
        return this.angle;
    }

    public void set_x(double x){
        System.out.println("not a feature yet");
    }
    public void set_y(double y){
        System.out.println("not a feature yet");
    }
    public void set_mag(double mag){
        this.magnitude = mag;
    }
    public void set_ang(double ang){
        this.angle = ang;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "" + this.magnitude + " " + this.angle;
    }
}
