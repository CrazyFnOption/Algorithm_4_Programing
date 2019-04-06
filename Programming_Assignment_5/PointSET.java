package Programming_Assignment_5;
//这一种方法同样就属于暴力类型的方法
//直接用给定好的API 然后直接逐个逐个的遍历 最后得出结果。

import edu.princeton.cs.algs4.StdDraw;
import java.util.TreeSet;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;



public class PointSET {
    private TreeSet<Point2D> pset;
    private int size;

    public PointSET()  {
        pset = new TreeSet<Point2D>();
        size = 0;
    }

    public boolean isEmpty() {
        return pset.isEmpty();
    }

    public int size() {
        return pset.size();
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        pset.add(p);
        size++;
    }

    public boolean contains(Point2D p) {
        if (isEmpty()) throw new IllegalArgumentException();
        return pset.contains(p);
    }

    public void draw() {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        for (Point2D it:pset) {
            it.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        if (isEmpty()) throw new IllegalArgumentException();
        TreeSet<Point2D> rset = new TreeSet<Point2D>();
        for (Point2D it :pset) {
            if (rect.contains(it)) {
                rset.add(it);
            }
        }
        return rset;
    }

    public Point2D nearest(Point2D p) {
        if (isEmpty()) throw new IllegalArgumentException();
        double mindis = Double.MAX_VALUE;
        Point2D ret = null;
        for (Point2D s : pset) {
            double dis = s.distanceSquaredTo(p);
            if (dis < mindis) {
                mindis = dis;
                ret = s;
            }
        }
        return ret;
    }

}