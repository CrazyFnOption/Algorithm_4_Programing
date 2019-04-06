package Programming_Assignment_3;

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;
import java.util.Arrays;
import java.util.Comparator;

public class Point implements Comparable<Point> {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw() {
        StdDraw.point(x,y);
    }

    public   void drawTo(Point that) {
        StdDraw.line(x,y,that.x,that.y);
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public int compareTo(Point that) {
        if (y > that.y)         return 1;
        else if (y < that.y)    return -1;
        else if (x > that.x)    return 1;
        else if (x < that.x)    return -1;
        else return 0;
    }

    public double slopeTo(Point that)
    {
        if (x == that.x)
        {
            if (y == that.y) return Double.NEGATIVE_INFINITY;
            else             return Double.POSITIVE_INFINITY;
        }
        if (y == that.y) return 0 / 1.0;
        return (y - that.y) * 1.0 / (x - that.x);

    }

    private class SlopeOrder implements Comparator<Point>{
        public int compare(Point p, Point q)
        {
            if (slopeTo(p) < slopeTo(q)) return -1;
            if (slopeTo(p) > slopeTo(q)) return +1;
            return 0;
        }
    }

    public Comparator<Point> slopeOrder() {
        return new  SlopeOrder();
    }

    public static void main(String[] args)
    {
        int x0 = Integer.parseInt(args[0]);
        int y0 = Integer.parseInt(args[1]);
        int n = Integer.parseInt(args[2]);

        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0, 50);
        StdDraw.setYscale(0, 50);
        StdDraw.setPenRadius(0.005);
        StdDraw.enableDoubleBuffering();

        Point[] points = new Point[n];

        for (int i = 0; i < n; i++)
        {
            int x = StdRandom.uniform(50);
            int y = StdRandom.uniform(50);
            points[i] = new Point(x, y);
            points[i].draw();
        }
        // draw p = (x0, x1) in red
        Point p = new Point(x0, y0);

        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setPenRadius(0.02);
        p.draw();
        // draw line segments from p to each point, one at a time, in polar order
        StdDraw.setPenRadius();
        StdDraw.setPenColor(StdDraw.BLUE);
        Arrays.sort(points, p.slopeOrder());
        for (int i = 0; i < n; i++)
        {
            p.drawTo(points[i]);
            StdDraw.show();
            StdDraw.pause(100);
        }
    }

}
