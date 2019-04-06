
package Programming_Assignment_1;


import edu.princeton.cs.algs4.WeightedQuickUnionUF;


public class Percolation {
    //建立两个并查集 来控制 backwash 的虚拟节点所产生的问题
    private WeightedQuickUnionUF uf;
    private WeightedQuickUnionUF backwash;
    //打开的数目
    private int num;
    //这里用一维数组来表示整张图
    private boolean[] percolation;
    //总数目
    private int len;

    public Percolation(int n) {
        if (n < 1) throw new IllegalArgumentException("Illeagal Argument");
        len = n;
        percolation = new boolean[n * n + 2];
        uf = new WeightedQuickUnionUF(n * n + 2);
        backwash = new WeightedQuickUnionUF(n * n + 1);
        num = 0;
        for (int i = 1;i < n * n + 1; i++){
            percolation[i] = false;
        }
        percolation[0] = percolation[n * n + 1] = true;
    }

    private void check(int i,int j){
        if (i < 1 || i > len || j < 1 || j > len)
            throw new IllegalArgumentException("out of the range");
    }

    private int get_position(int i,int j) {
        return (i - 1) * len + j;
    }

    public void open(int row, int col) {
        check(row,col);
        if (isOpen(row,col)) return;
        int index = get_position(row,col);
        percolation[index] = true;
        num++;
        //处理虚拟节点与实际中的点的关系
        //并且同时处理一下前后左右点之间的关系
        if (row == 1){
            uf.union(0,index);
            backwash.union(0,index);
        }

        else if (isOpen(index - len)){
            uf.union(index,index - len);
            backwash.union(index,index - len);
        }

        if (row == len) uf.union(len * len + 1,index);
        else if (isOpen(index + len)){
            uf.union(index,index + len);
            backwash.union(index,index + len);
        }

        if (col != 1 && isOpen(index - 1)){
            uf.union(index,index - 1);
            backwash.union(index,index - 1);
        }

        if (col != len && isOpen(index + 1)){
            uf.union(index,index + 1);
            backwash.union(index,index + 1);
        }
    }
    private boolean isOpen(int x){
        return percolation[x];
    }

    public boolean isOpen(int row, int col){
        check(row,col);
        return isOpen(get_position(row,col));
    }

    public boolean isFull(int row, int col){
        check(row,col);
        int index = get_position(row,col);
        if (backwash.connected(index,0))
            return true;
        return false;
    }
    public int numberOfOpenSites(){
        return num;
    }
    public boolean percolates(){
        return uf.connected(0,len * len + 1);
    }

}