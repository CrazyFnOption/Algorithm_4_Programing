package Programming_Assignment_4;

import edu.princeton.cs.algs4.StdRandom;
import java.util.ArrayList;


public class Board {
    private int [][] blocks;
    private final int dimension;

    public Board(int[][] blocks) {
        if (blocks == null) throw new NullPointerException("Null blocks");
        dimension = blocks.length;
        this.blocks = new int[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            this.blocks[i] = blocks[i].clone();
        }

    }

    public int dimension() {
        return dimension;
    }

    public int hamming() {
        int cnt = 0;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (blocks[i][j] == 0) continue;
                if (blocks[i][j] != i  * dimension + j + 1) cnt ++;
            }
        }
        return cnt;
    }

    public int manhattan() {
        int cnt = 0;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (blocks[i][j] == 0) continue;
                if (blocks[i][j] != i  * dimension + j + 1) {
                    int val = blocks[i][j];
                    int row = (val - 1) / dimension;
                    int col = (val - 1) % dimension;
                    int dif = Math.abs(row - i)  + Math.abs(col - j);
                    cnt += dif;
                }
            }
        }
        return cnt;
    }


    public boolean isGoal() {
        return hamming() == 0;
    }

    private void swap (int i1,int r1,int i2,int r2) {
        int tmp = blocks[i1][r1];
        blocks[i1][r1] = blocks[i2][r2];
        blocks[i2][r2] = tmp;
    }

    public Board twin() {
        Board twinBoard = new Board(blocks);
        int row = 0,col = 0;
        if (blocks[row][col] == 0) col++;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (blocks[i][j] != 0 && blocks[i][j] != blocks[row][col]) {
                    twinBoard.swap(i,j,row,col);
                    return twinBoard;
                }
            }
        }


        return twinBoard;
    }

    public boolean equals(Object y) {
         if (y == null) return false;
         if (y.getClass().isInstance(this)) {
             Board tmp = (Board) y;
             if (tmp.dimension != this.dimension)
                 return false;
             for (int i = 0; i < dimension; i++) {
                 for (int j = 0; j < dimension; j++) {
                     if (tmp.blocks[i][j] != this.blocks[i][j])
                         return false;
                 }
             }
             return true;
         }
         return false;
    }

    public Iterable<Board> neighbors() {
        ArrayList<Board> neighbors = new ArrayList<>();
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (blocks[i][j] == 0) {

                    if (i > 0) {
                        Board tmpUp = new Board(blocks);
                        tmpUp.swap(i,j,i - 1,j);
                        neighbors.add(tmpUp);
                    }

                    if (i < dimension - 1) {
                        Board tmpDown = new Board(blocks);
                        tmpDown.swap(i,j,i + 1,j);
                        neighbors.add(tmpDown);
                    }

                    if (j > 0) {
                        Board tmpLeft = new Board(blocks);
                        tmpLeft.swap(i,j,i,j - 1);
                        neighbors.add(tmpLeft);
                    }

                    if (j < dimension - 1) {
                        Board tmpRight = new Board(blocks);
                        tmpRight.swap(i,j,i,j + 1);
                        neighbors.add(tmpRight);
                    }
                    break;
                }
            }
        }
        return neighbors;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(dimension + "\n");
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                sb.append(String.format("%2d ", blocks[row][col]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    //public static void main(String[] args) // unit tests (not graded)
}