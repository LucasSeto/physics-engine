
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Vector {
    public double magnitude;
    public double angle;

    public Vector(double mag, double ang){
        this.magnitude = mag;
        this.angle = ang;
    }

    public void draw(Graphics2D g2D, double x, double y, Color c){
        g2D.setColor(c);
        g2D.setStroke(new BasicStroke(5));
        g2D.drawLine((int)x, (int)y, (int)(x + components()[0]*25), (int)(y + components()[1]*25));
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
}
