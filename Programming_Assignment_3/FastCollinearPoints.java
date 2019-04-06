
package Programming_Assignment_3;

import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {

    private Point[] copies;
    private ArrayList<LineSegment> lineSegments = new ArrayList<LineSegment>();

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (points == null)
            throw new java.lang.IllegalArgumentException();

        copies = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            copies[i] = points[i];
        }

        // sort by y-coordinate
        // the endpoints are the first and last points
        Arrays.sort(copies);

        // after sort then can check if duplicate
        for (int i = 0; i < copies.length - 1; i++)
            if (copies[i].compareTo(copies[i+1]) == 0)
                throw new java.lang.IllegalArgumentException();

        for (int i = 0; i < copies.length - 1; i++) {
            Point origin = copies[i];             // Think of p as the origin.
            double[] slopes = new double[copies.length - 1 - i];
            Point[] others = new Point[copies.length - 1 - i];

            for (int j = 0; j < copies.length - 1 - i; j++)
                others[j] = copies[j + 1 + i];

            // For each other point q, determine the slope it makes with p
            for (int j = 0; j < others.length; j++)
                slopes[j] = origin.slopeTo(others[j]);

            // Sort the points according to the slopes they makes with p
            Arrays.sort(others, origin.slopeOrder());

            Arrays.sort(slopes);
            // Check if any 3 (or more) adjacent points in the
            // sorted order have equal slopes with respect to p
            // If so, these points, together with p, are collinear
            for (int cnt_same = 0, j = 0; j < slopes.length - 1; j++) {
                if (slopes[j] == slopes[j+1]) {
                    cnt_same++;
                }
                if (cnt_same >= 2) {
                    lineSegments.add(new LineSegment(origin, others[j + 1]));
                    break;
                }
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return lineSegments.size();
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] result = new LineSegment[lineSegments.size()];
        for (int i = 0; i < lineSegments.size(); i++) {
            result[i] = lineSegments.get(i);
        }
        return result;
    }
}