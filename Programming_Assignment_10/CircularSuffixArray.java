package Programming_Assignment_10;

import java.util.Arrays;
import java.util.Comparator;

public class CircularSuffixArray {

    private int length;
    private Integer[] index;
    private char[] value;

    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException("the call to CircularSuffixArray() is null");
        this.length = s.length();
        index = new Integer[length];
        value = new char[length];

        for (int i = 0; i < length; i++) {
            index[i] = i;
            value[i] = s.charAt(i);
        }

        // 接下来这个地方不能去构造n个数组去存，因为会耗费大量的时间与空间，所以这里最好采用特殊的算法去实现
        // 特别是这个地方的排序看的我特别的蒙，为什么要这样写，这样重载的排序到底是排序什么
        // 首先，先看图，他是给出一个字符串，然后获得一列每次向左边移一个的字符串列，然后对这个字符串列进行排序，得到后面的结果。
        // 然后这里使用的是字符串排序里面的三相切分，一个一个进行比较，主要注意的是每一个index，就意味着第一个最前面的那个序列。
        // 然而下面这种写的办法，是一种简略的办法，专门用于不需要空间的字符串排序，o1,o2在于比较第o1，或者o2个字符串，而i就专门来递增其的位数。
        Arrays.sort(index, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                for (int i = 0; i < length; i++) {
                    char c1 = value[(i + o1) % length];
                    char c2 = value[(i + o2) % length];
                    if (c1 > c2) return 1;
                    if (c1 < c2) return -1;
                }
                return 0;
            }
        });
    }


    public int length() {return length;}


    public int index(int i) {
        if (i < 0 || i >= length) throw new IllegalArgumentException("the call to index() is out of range");
        return index[i];
    }
    
    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
        for (int i = 0; i < csa.length(); i++)
            System.out.print(csa.index(i) + " ");
    }

}
