package bench;

/**
 * Class modeling a roplanif.edc2_2015.roplanif.edc2_2015.squelette.Point in a Cartesian system
 * @author hadrien
 */
public class Point {

    protected static final double epsilon = 1e-12;

    /**
     * x-coordinate of the point in a Cartesian representation
     */
    private double x;

    /**
     * y-coordinate of the point in a Cartesian representation
     */
    private double y;

    /**
     * Constructor by the given of x/y coordinates of the point
     * @param xx x-coordinate of the point
     * @param yy y-coordinate of the point
     */
    public Point(double xx, double yy) {
        this.x = xx;
        this.y = yy;
    }

    /**
     * @return the x-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * @return the y-coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Translates the point following the direction vector (dx,dy)
     * @param dx x-coordinate of the translation vector
     * @param dy y-coordinate of the translation vector
     */
    public void translater(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * @param p another point
     * @return the euclidian distance bewteen this point and the given p
     */
    public double distance(Point p) {
        double xd = (p.x - x);
        double xc = (p.y - y);
        return Math.sqrt(xd*xd + xc*xc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        if (Math.abs(point.x-x) > epsilon) return false;
        if (Math.abs(point.y-y) > epsilon) return false;
        return true;
    }

    /**
     * @return a string representation of the roplanif.edc2_2015.roplanif.edc2_2015.squelette.Point
     */
    public String toString() {
        return "(" + x + " " + y + ")";
    }

    /**
     * Draw the point on the screen
     * @param size the diameter of the point
     */
    public void dessine(double size) {
        StdDraw.circle(x, y, size);
    }

    /**
     * @return the angle of polar coordinates
     */
    public double getTheta() {
        return 2*Math.atan(y/(x+getR()));
    }

    /**
     * @return the radius of polar coordinates
     */
    public double getR() {
        return distance(new Point(0,0));
    }

    /**
     * @param angle
     * @return a Point obtained by adding the value "angle" to the theta-polar coordinate of this point.
     * (It basically "rotate" the point by angle following the circle centered on the origin)
     */
    public Point rotateFromAngle(double angle) {
        double theta = this.getTheta();
        double r = this.getR();
        double newx = r*Math.cos((theta+angle)%(2*Math.PI));
        double newy = r*Math.sin((theta+angle)%(2*Math.PI));
        return new Point(newx, newy);
    }
}
