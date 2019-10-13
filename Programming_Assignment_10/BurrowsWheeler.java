package Programming_Assignment_10;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    private static final int R = 256;

    // 这个步骤就是直接去构造 最后一列的字符串序列，这就是这样的一个转化过程。
    public static void transform() {
        String input = BinaryStdIn.readString();
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(input);
        int first = 0;
        for (int i = 0; i < input.length(); i++) {
            if (circularSuffixArray.index(i) == 0) {
                first = i;
                break;
            }
        }
        BinaryStdOut.write(first);
        // 牛逼了这个地方，前面后缀循环数组里面index 实际上表示的就是这个位置在原来的字符串中表示的位置在哪，
        // 既然现在要看最后一个，那么久直接减去一就可以了，当然要注意负数取mod的情况。
        for (int i = 0; i < input.length(); i++) {
            int index = (circularSuffixArray.index(i) - 1 + input.length()) % input.length();
            BinaryStdOut.write(input.charAt(index));
        }
        BinaryStdOut.close();
    }

    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String last = BinaryStdIn.readString();

        int len = last.length();
        int[] next = new int[len];
        int[] count = new int[R + 1];
        char[] firstcol = new char[len];
        for (int i = 0; i < len; i++)
            count[last.charAt(i) + 1]++;
        for (int i = 0; i < R; i++)
            count[i + 1] += count[i];
        // 下面的变化就是特别奇妙了
        // 利用基数排序，计算相应的个数，count数组对应排序过后的位置，然后按照last数组的特性
        // 其当前的位置，也就是数组下一行的位置。
        for (int i = 0; i < len; i++) {
            int pos = count[last.charAt(i)] ++;
            firstcol[pos] = last.charAt(i);
            next[pos] = i;
        }
        // 以上就得到next数组，下面可以直接写出来即可。

        for (int i = 0; i < len; i++) {
            BinaryStdOut.write(firstcol[first]);
            first = next[first];
        }

        BinaryStdOut.close();

    }
    
    public static void main(String[] args) {
        if (args[0].equals("-"))
            BurrowsWheeler.transform();
        if (args[0].equals("+"))
            BurrowsWheeler.inverseTransform();
    }
}