
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class RayCast {
    Point2D.Double start_point;
    Point2D.Double end_point;
    Line2D.Double ray;
    

    public RayCast (int start_x, int start_y, int end_x, int end_y){
        start_point = new Point2D.Double(start_x, start_y);
        end_point = new Point2D.Double(end_x, end_y);
        this.ray = new Line2D.Double(start_x, start_y, end_x, end_y);
    }

    public boolean line_intersection(Line2D.Double ray2){
        return this.ray.intersectsLine(ray2);
    }
    public boolean rect_intersection(Rectangle2D rect){
        return this.ray.intersects(rect);
    }
}
