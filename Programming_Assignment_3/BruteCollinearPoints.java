package Programming_Assignment_3;

import java.util.ArrayList;
import java.util.Arrays;

public class BruteCollinearPoints {

    private Point[] copies;
    private ArrayList<LineSegment> lineSegments = new ArrayList<LineSegment>();

    // finds all line segments containing 4 points
    public BruteCollinearPoints(final Point[] points) {

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

        for (int ip = 0; ip < copies.length-3; ip++) {
            for (int iq = ip + 1; iq < copies.length-2; iq++) {
                double slopeP2Q = copies[ip].slopeTo(copies[iq]);
                for (int ir = iq + 1; ir < copies.length-1; ir++) {
                    double slopeQ2R = copies[iq].slopeTo(copies[ir]);
                    if (slopeP2Q != slopeQ2R) continue;
                    for (int is = ir + 1; is < copies.length; is++) {
                        double slopeR2S = copies[ir].slopeTo(copies[is]);
                        // if 3 of 4's slopes are equal then 4 points are colllinear
                        if (slopeP2Q == slopeR2S)
                            lineSegments.add(new LineSegment(copies[ip], copies[is]));
                    }
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