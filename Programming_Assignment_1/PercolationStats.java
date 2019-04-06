package Programming_Assignment_1;


import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

public class PercolationStats {

    private double[] results; // estimated threshold for each trial
    private double avg;
    private double std;

    public PercolationStats(int n, int trials){

        if(n <= 0 || trials <= 0) throw new IllegalArgumentException();

        results = new double[trials];
        for(int i = 0; i < trials; i++){
            int step = 0;
            Percolation pr = new Percolation(n);
            while(!pr.percolates()){
                int row = StdRandom.uniform(n) + 1;
                int col = StdRandom.uniform(n) + 1;
                if(!pr.isOpen(row, col)){
                    pr.open(row, col);
                    step++;
                }
            }
            results[i] = (double)step / (n * n);
        }

        this.avg = StdStats.mean(results);
        this.std = StdStats.stddev(results);

    }

    public static void main(String[] args){

        StdOut.printf("%-25s\n", "Please input 2 integers");
        int N = StdIn.readInt();
        int T = StdIn.readInt();

        Stopwatch wt = new Stopwatch();

        PercolationStats ps = new PercolationStats(N, T);

        // elapsed CPU time in seconds
        double elapsed = wt.elapsedTime();

        StdOut.printf("%-25s= %.15f\n", "elapsed CPU time", elapsed);
        StdOut.printf("%-25s= %.7f\n", "mean", ps.mean());
        StdOut.printf("%-25s= %.17f\n", "stddev", ps.stddev());
        StdOut.printf("%-25s= [%.15f, %.15f]\n", "%95 confidence interval",
                ps.confidenceLo(), ps.confidenceHi());
    }

    public double mean(){
        return this.avg;
    }

    public double stddev(){
        return this.std;
    }

    public double confidenceLo(){
        return mean() - 1.96 * stddev() / Math.sqrt(results.length);
    }

    public double confidenceHi(){
        return mean() + 1.96 * stddev() / Math.sqrt(results.length);
    }

}

