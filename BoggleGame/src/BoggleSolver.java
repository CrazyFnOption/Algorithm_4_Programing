


import edu.princeton.cs.algs4.Bag;

import java.util.HashSet;
import edu.princeton.cs.algs4.Stack;

public class BoggleSolver
{
    //自己建立的 字典树，这样方便后面dfs查询的时候的剪枝。
    private Node root;
    private BoggleBoard board;
    private int col,row;
    private HashSet<String> allwords;
    private Bag<Integer>[] adj;
    private boolean[] vis;
    private Stack<Integer> dice;

    private class Node {
        int val = 0;
        private Node[] next = new Node[26];
    }

    public BoggleSolver(String[] dictionary) {
        root = new Node();
        for (int i = 0; i < dictionary.length; i++) {
            put(dictionary[i]);
        }
    }

    private void put(String word) {
        root = put(root, word, 0);
    }

    //这里就是直接构造出一个字典树，通过这个字典树来存储所有字符，并且剪枝dfs。
    private Node put(Node x, String word, int d) {
        if (x == null) x = new Node();
        if (d == word.length()) {
            x.val = 1;
            return x;
        }
        int c = word.charAt(d) - 'A';
        x.next[c] = put(x.next[c], word, d + 1);
        return x;
    }

    private int get(String word) {
        Node x = get(root, word, 0);
        if (x == null) return 0;
        return x.val;
    }

    private Node get(Node x, String word, int d) {
        if (x == null) return null;
        if (d == word.length()) return x;
        int c = word.charAt(d) - 'A';
        return get(x.next[c], word, d + 1);
    }

    private boolean check(int i, int j) {
        return i >= 0 && i < row && j >= 0 && j < col;
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        this.board = board;
        allwords = new HashSet<>();
        row = board.rows();
        col = board.cols();
        //这个地方的写法需要注意一下。
        adj = (Bag<Integer>[]) new Bag[row * col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                int v = i * col + j;
                adj[v] = new Bag<Integer>();
                //这里就相当于图论里面的建立边，直接给后面dfs提供条件
                if (check(i - 1, j)) adj[v].add((i - 1) * col + j);
                if (check(i + 1, j)) adj[v].add((i + 1) * col + j);
                if (check(i, j - 1)) adj[v].add(i * col + j - 1);
                if (check(i, j + 1)) adj[v].add(i * col + j + 1);
                if (check(i + 1, j - 1)) adj[v].add((i + 1) * col + j - 1);
                if (check(i + 1, j + 1)) adj[v].add((i + 1) * col + j + 1);
                if (check(i - 1, j - 1)) adj[v].add((i - 1) * col + j - 1);
                if (check(i - 1, j + 1)) adj[v].add((i - 1) * col + j + 1);
            }
        }

        //接下来就到了 dfs搜图的时候
        //最先开始我个人的想法是在这个地方用bfs来进行，当时发现到后面存在很多的问题，比如时间复杂度是特别高的。

        for(int i = 0; i < row * col; i++) {
            vis = new boolean[row * col];
            dice = new Stack<Integer>();
            vis[i] = true;
            dice.push(i);
            //这个地方需要留意的是 root在这里并没有其他的含义
            char c = getLetter(i);
            if (c == 'Q') dfs(i, root.next['Q' - 'A'].next['U' - 'A'], "QU", dice);
            else dfs(i, root.next[c - 'A'], c + "", dice);
            //由于这个地方前面就直接重新定义了，所以就不需要采用清空操作了。
        }
        return allwords;
    }

    private char getLetter(int v) {
        return board.getLetter(v / col , v % col);
    }

    private void dfs(int v, Node x, String prefix, Stack<Integer>dices) {
        if (prefix.length() > 2 && x != null && x.val == 1) {
            allwords.add(prefix);
        }

        for (int w : adj[v]) {
            char c = getLetter(w);
            if (!vis[w] && x != null && x.next[c - 'A'] != null) {
                dice.push(w);
                vis[w] = true;

                if (c == 'Q') {
                    dfs(w, x.next['Q' - 'A'].next['U' - 'A'], prefix + "QU", dice);
                }
                else dfs(w, x.next[c - 'A'], prefix + c, dice);

                int d = dice.pop();
                vis[d] = false;
            }
        }
    }

    public int scoreOf(String word) {
        if (get(word) == 0)
            return 0;
        else {
            int len = word.length();
            if (len <= 2) return 0;
            else if(len == 3 || len == 4) return 1;
            else if (len == 5) return 2;
            else if (len == 6) return 3;
            else if (len == 7) return 5;
            else return 11;
        }
    }
}