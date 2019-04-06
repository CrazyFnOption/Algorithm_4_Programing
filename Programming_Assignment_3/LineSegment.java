package Programming_Assignment_3;

public class LineSegment {
    private final Point a;
    private final Point b;

    public LineSegment(Point p, Point q) {
        if (p == null || q == null)
            throw new java.lang.IllegalArgumentException();
        a = p;
        b = q;
    }

    public void draw() {
        a.drawTo(b);
    }

    public String toString() {
        return a + " -> " + b;
    }

    public int hasCode() {
        throw new UnsupportedOperationException();
    }
}