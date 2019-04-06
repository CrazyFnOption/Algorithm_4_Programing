package Programming_Assignment_4;


import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    private BoardNode current;
    private BoardNode twinCurrent;

    private class BoardNode implements Comparable<BoardNode> {
        private Board item;
        private int priority;
        private int move;
        private BoardNode preBoard;

        public BoardNode (Board item,BoardNode preBoard) {
            this.item = item;
            this.preBoard = preBoard;
            if (preBoard == null) this.move = 0;
            else this.move = preBoard.move + 1;
            this.priority = this.move + item.manhattan();
        }

        public int compareTo(BoardNode b) {
            return Integer.compare(this.priority,b.priority);
        }
    }


    private void putNeighbors (BoardNode current,MinPQ<BoardNode>pq) {
        Iterable<Board> neighbors = current.item.neighbors();
        for (Board it : neighbors) {
            if (current.preBoard == null || !it.equals(current.preBoard.item)) {
                pq.insert(new BoardNode(it,current));
            }
        }
    }


    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("Constructor argument Board is null!");
        }
        current = new BoardNode(initial,null);
        twinCurrent = new BoardNode(initial.twin(),null);
        MinPQ<BoardNode> pq = new MinPQ<BoardNode>();
        MinPQ<BoardNode> twinpq = new MinPQ<BoardNode>();
        pq.insert(current);
        twinpq.insert(twinCurrent);
        while (true) {
            current = pq.delMin();
            if (current.item.isGoal()) break;
            putNeighbors(current,pq);

            twinCurrent = twinpq.delMin();
            if (twinCurrent.item.isGoal()) break;
            putNeighbors(twinCurrent,twinpq);
        }
    }

    public boolean isSolvable() {
        return current.item.isGoal();
    }

    public int moves() {
        if (current.item.isGoal())   return current.move;
        return -1;
    }


    public Iterable<Board> solution() {
        if (isSolvable()) {
            Stack<Board> stack = new Stack<>();
            BoardNode node = current;
            while (node != null) {
                stack.push(node.item);
                node = node.preBoard;
            }
            return stack;
        }
        return null;
    }

}