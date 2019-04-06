package Programming_Assignment_5;

import edu.princeton.cs.algs4.StdDraw;

import java.util.Comparator;


public class Point2D implements Comparable<Point2D> {

    private final double x,y;

    private final Comparator<Point2D> x_order;
    private final Comparator<Point2D> y_order;

    public Point2D(double x, double y) {
        if (Double.isFinite(x) || Double.isFinite(y))
            throw new IllegalArgumentException("Coordinates must be finite");
        if (Double.isNaN(x) || Double.isNaN(y))
            throw new IllegalArgumentException("Coordinates cannot be NaN");
        if (x == 0.0) this.x = 0.0;
        else this.x = x;
        if (y == 0.0) this.y = 0.0;
        else this.y = y;

        x_order = new Comparator<Point2D>() {
            @Override
            public int compare(Point2D o1, Point2D o2) {
                if (o1.x > o2.x) return 1;
                else if (o1.x < o2.x) return -1;
                else return 0;
            }
        };

        y_order = new Comparator<Point2D>() {
            @Override
            public int compare(Point2D o1, Point2D o2) {
                if (o1.y < o2.y) return -1;
                if (o1.y > o2.y) return 1;
                return 0;
            }
        };

    }


    public  double x() {
        return this.x;
    }

    public  double y() {
        return this.y;
    }


    public  double distanceTo(Point2D that) {
        return Math.sqrt((this.x - that.x) * (this.x - that.x) +
                (this.y - that.y) * (this.y - that.y));
    }


    public  double distanceSquaredTo(Point2D that) {
        return (this.x - that.x) * (this.x - that.x) +
                (this.y - that.y) * (this.y - that.y);
    }


    public int compareTo(Point2D that) {
        if (this.x < that.x) return -1;
        else if (this.x > that.x) return 1;
        else if (this.y < that.y) return -1;
        else if (this.y > that.y) return 1;
        else return 0;
    }

    public boolean equals(Object that) {
        if (that == null) return false;
        if (that == this) return true;
        if (that.getClass() != this.getClass()) return false;

        Point2D other = (Point2D) that;
        return this.x == other.x && this.y == other.y;
    }


    public    void draw() {
        StdDraw.point(x,y);
    }

    public  String toString() {
        return "(" + x + ", " + y + ")";
    }

}
