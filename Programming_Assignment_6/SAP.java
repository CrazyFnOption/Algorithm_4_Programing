

import edu.princeton.cs.algs4.Digraph;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;


public class SAP {

    private int length;
    private int ancestor;
    private final Digraph copyG;
    private int[] distTo1;
    private int[] distTo2;
    private boolean[] marked1;
    private boolean[] marked2;
    private final Stack<Integer> stack1;
    private final Stack<Integer> stack2;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("argument to G");
        copyG = new Digraph(G);
        distTo1 = new int [G.V()];
        distTo2 = new int [G.V()];
        marked1 = new boolean[G.V()];
        marked2 = new boolean[G.V()];
        stack1 = new Stack<>();
        stack2 = new Stack<>();
    }

    private void checkVertex(int x) {
        int v = marked1.length;
        if (x < 0 || x >= v) throw new IllegalArgumentException();
    }

    private void checkVertices(Iterable<Integer> x) {
        if (x == null) throw new IllegalArgumentException();
        int v = marked1.length;
        for (Integer vv :x) {
            if (vv == null) throw new IllegalArgumentException();
            if (vv < 0 || vv >= v) throw new IllegalArgumentException();
        }
    }

    private void init() {
        while (!stack1.isEmpty()) {
            int v = stack1.pop();
            marked1[v] = false;
        }
        while (!stack2.isEmpty()) {
            int v = stack2.pop();
            marked2[v] = false;
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        checkVertex(v);
        checkVertex(w);
        compute(v, w);
        return length;
    }



    private void compute(int v, int w) {
        length = -1;
        ancestor = -1;
        distTo1[v] = 0;
        distTo2[w] = 0;
        marked1[v] = true;
        marked2[w] = true;
        stack1.push(v);
        stack2.push(w);
        Queue<Integer> q1 = new LinkedList<>();
        Queue<Integer> q2 = new LinkedList<>();
        q1.add(v);
        q2.add(w);
        bfs(q1, q2);
    }

    private void compute(Iterable<Integer> v, Iterable<Integer> w) {
        length = -1;
        ancestor = -1;
        Queue<Integer> q1 = new LinkedList<>();
        Queue<Integer> q2 = new LinkedList<>();
        for (int x :v) {
            marked1[x] = true;
            stack1.push(x);
            distTo1[x] = 0;
            q1.add(x);
        }
        for (int x: w) {
            marked2[x] = true;
            stack2.push(x);
            distTo2[x] = 0;
            q2.add(x);
        }
        bfs(q1, q2);
    }

    private void bfs(Queue<Integer> q1, Queue<Integer> q2) {
        while (!q1.isEmpty() || !q2.isEmpty()) {
            if (!q1.isEmpty()) {
                int v = q1.remove();
                if (marked2[v]) {
                    if (distTo1[v] + distTo2[v] < length || length == -1) {
                        ancestor = v;
                        length = distTo1[v] + distTo2[v];
                    }
                }

                if (distTo1[v] < length || length == -1) {
                    for (int w: copyG.adj(v)) {
                        if (!marked1[w]) {
                            distTo1[w] = distTo1[v] + 1;
                            marked1[w] = true;
                            stack1.push(w);
                            q1.add(w);
                        }
                    }
                }
            }

            if (!q2.isEmpty()) {
                int v = q2.remove();
                if (marked1[v]) {
                    if (distTo1[v] + distTo2[v] < length || length == -1) {
                        ancestor = v;
                        length = distTo1[v] + distTo2[v];
                    }
                }
                if (distTo2[v] < length || length == -1) {
                    for (int w:copyG.adj(v)) {
                        if (!marked2[w]) {
                            distTo2[w] = distTo2[v] + 1;
                            marked2[w] = true;
                            stack2.push(w);
                            q2.add(w);
                        }
                    }
                }
            }
        }
        init();
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        checkVertex(v);
        checkVertex(w);
        compute(v, w);
        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        checkVertices(v);
        checkVertices(w);
        compute(v, w);
        return length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        checkVertices(v);
        checkVertices(w);
        compute(v, w);
        return ancestor;
    }

}