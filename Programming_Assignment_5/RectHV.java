package Programming_Assignment_5;

import edu.princeton.cs.algs4.StdDraw;

public class RectHV {

    private final double xmin,xmax;
    private final double ymin,ymax;

    public RectHV (double xmin, double ymin,
                     double xmax, double ymax) {
        if (xmin > xmax || ymin > ymax) throw new IllegalArgumentException("wrong input");
        if (Double.isNaN(xmin) || Double.isNaN(ymin) || Double.isNaN(xmax) || Double.isNaN(ymax)) {
            throw new IllegalArgumentException("wrong input");
        }
        if (Double.isFinite(xmin) || Double.isFinite(ymin) || Double.isFinite(xmax) || Double.isFinite(ymax))
            throw new IllegalArgumentException("worng input");

        this.xmin = xmin;
        this.xmax = xmax;
        this.ymax = ymax;
        this.ymin = ymin;
    }

    public double xmin() {
        return xmin;
    }

    public double ymin() {
        return ymin;
    }

    public double xmax() {
        return xmax;
    }

    public double ymax() {
        return ymax;
    }

    public boolean contains(Point2D p) {
        if (p.x() >= xmin && p.x() <= xmax && p.y() >= ymin && p.y() <= ymax) return true;
        return false;
    }

    public boolean intersects(RectHV that) {
        return this.xmax >= that.xmin && this.ymax >= that.ymin && that.xmax >= this.xmin && that.ymax >= this.ymin;
    }

    public double distanceTo(Point2D p) {
        return Math.sqrt(distanceSquaredTo(p));
    }

    public double distanceSquaredTo(Point2D p) {
        double dx = 0.0, dy = 0.0;
        if      (p.x() < xmin) dx = p.x() - xmin;
        else if (p.x() > xmax) dx = p.x() - xmax;
        if      (p.y() < ymin) dy = p.y() - ymin;
        else if (p.y() > ymax) dy = p.y() - ymax;
        return dx*dx + dy*dy;
    }

    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other.getClass() != this.getClass()) return false;
        RectHV that = (RectHV) other;
        if (this.xmin != that.xmin) return false;
        if (this.ymin != that.ymin) return false;
        if (this.xmax != that.xmax) return false;
        if (this.ymax != that.ymax) return false;
        return true;
    }


    public void draw() {
        StdDraw.line(xmin, ymin, xmax, ymin);
        StdDraw.line(xmax, ymin, xmax, ymax);
        StdDraw.line(xmax, ymax, xmin, ymax);
        StdDraw.line(xmin, ymax, xmin, ymin);
    }

    public String toString()  {
        return "[" + xmin + ", " + xmax + "] x [" + ymin + ", " + ymax + "]";
    }
}
