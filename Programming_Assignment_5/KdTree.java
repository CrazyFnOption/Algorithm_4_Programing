package Programming_Assignment_5;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private Node root;
    private int size;


    private class Node {
        private final Point2D item;
        private Node leftBotton;
        private Node rightUp;
        private RectHV recv;

        public Node (Point2D a,RectHV recv) {
            if (a == null) throw new IllegalArgumentException();
            item = a;
            this.recv = recv;
            leftBotton = null;
            rightUp = null;
        }
    }

    public KdTree() {
        root = null;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private int compareTo(Point2D o1, Point2D o2,boolean ori) {
        if (o1.equals(o2)) return 0;
        if (ori == false) {
            if (o1.x() > o2.x()) return 1;
            return -1;
        }
        else {
            if (o1.y() < o2.y()) return -1;
            return 1;
        }
    }

    private Node insert(Node cur, Point2D p,double xmin,double ymin,double xmax,double ymax,boolean ori) {
        if (cur == null) {
            size++;
            return new Node (p,new RectHV(xmin,ymin,xmax,ymax));
        }
        int cmp = compareTo(p,cur.item,ori);
        double x0 = xmin,y0 = ymin;
        double x1 = xmax,y1 = ymax;
        if (cmp < 0) {
            if (ori == false) x1 = cur.item.x();
            else y1 = cur.item.y();
            cur.leftBotton = insert(cur.leftBotton,p,x0,y0,x1,y1,!ori);
        }
        else if (cmp > 0) {
            if (ori == false) x0 = cur.item.x();
            else y0 = cur.item.y();
            cur.rightUp = insert(cur.rightUp,p,x0,y0,x1,y1,!ori);
        }
        //这里就是set的去重功能了
        return cur;
    }

    public void insert(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        root = insert(root,p,0,0,1,1,false);
    }

    private boolean get(Node cur,Point2D p,boolean ori) {
        if (cur == null) return false;
        int cmp = compareTo(p,cur.item,ori);
        if (cmp < 0) return get(cur.leftBotton,p,!ori);
        else if (cmp > 0) return get(cur.rightUp,p,!ori);
        return true;
    }

    public boolean contains(Point2D p) {
        if (isEmpty()) throw new IllegalArgumentException();
        return get(root,p,false);
    }

    private void draw(Node cur,boolean ori) {
        if (cur == null) return;

        // draw a point
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.01);
        StdDraw.point(cur.item.x(), cur.item.y());

        //draw a line
        if (ori == false) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius();
            StdDraw.line(cur.item.x(),cur.recv.ymin(),cur.item.x(),cur.recv.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius();
            StdDraw.line(cur.recv.xmin(),cur.item.y(),cur.recv.xmax(),cur.item.y());
        }

        //recursive in this tree
        draw(cur.leftBotton,!ori);
        draw(cur.rightUp,!ori);
    }

    public void draw(){
        StdDraw.setScale(0, 1);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius();
        new RectHV(0,0,1,1).draw();
        if (isEmpty()) throw new IllegalArgumentException();
        draw(root, false);
    }

    public Iterable<Point2D> range(RectHV rect) {

        if (rect == null) throw new IllegalArgumentException();
        Queue<Point2D> points = new Queue<>();
        Queue<Node> queue = new Queue<>();
        if  (root == null) return null;
        queue.enqueue(root);
        while (!queue.isEmpty()) {
            Node cur = queue.dequeue();
            if (cur == null) continue;
            if (rect.contains(cur.item)) points.enqueue(cur.item);
            if (cur.leftBotton != null && rect.intersects(cur.leftBotton.recv)) queue.enqueue(cur.leftBotton);
            if (cur.rightUp != null && rect.intersects(cur.rightUp.recv)) queue.enqueue(cur.rightUp);
        }
        return points;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (root == null) return null;
        Point2D tmp = null;
        double max = Double.MAX_VALUE;
        Queue<Node> q = new Queue<>();
        q.enqueue(root);
        while (!q.isEmpty()) {
            Node t = q.dequeue();
            double dis = p.distanceSquaredTo(t.item);
            if (dis < max) {
                max = dis;
                tmp = t.item;
            }

            //左子树的BFS操作 记录符合条件的节点
            if (t.leftBotton != null && t.leftBotton.recv.distanceSquaredTo(p) < max)
                q.enqueue(t.leftBotton);

            //右子树的BFS操作 记录符合条件的节点
            if (t.rightUp != null && t.rightUp.recv.distanceSquaredTo(p) < max)
                q.enqueue(t.rightUp);

        }
        return tmp;
    }





}
