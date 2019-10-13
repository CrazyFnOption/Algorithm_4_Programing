package Programming_Assignment_10;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int R = 256;

    public static void encode() {
        char[] aux = new char[R];
        for (int i = 0; i < R; i++) {
            aux[i] = (char)i;
        }
        String input = BinaryStdIn.readString();
        boolean printChar = true;
        for (int i = 0; i < input.length(); i++) {
            move(aux, input.charAt(i), printChar);
        }
        BinaryStdOut.close();
    }

    // 这里看题目条件反而看掉了很多东西，这里顺次一个一个往后面排，而不是直接交换。
    // 这里仍然需要注意的是，这种交换类似于我之前做过的题目，相当于一种排序，但是直接将最前面和每一个进行交换，达到了每个元素往后面移动的目的
    // 这种排序方式是需要花费大量时间去注意的
    private static void move(char[] aux, char ch, boolean printChar) {
        char tmp = aux[0];
        for (int i = 0; i < R; i++) {
            if (aux[i] == ch) {
                aux[0] = ch;
                aux[i] = tmp;
                if (printChar) BinaryStdOut.write((char)i);
                break;
            }
            char t = aux[i];
            aux[i] = tmp;
            tmp = t;
        }
    }

    // 其实跟上面的encode属于同一种方式去写出来的。
    public static void decode() {
        char[] aux = new char[R];
        for (int i = 0; i < R; i++)
            aux[i] = (char)i;

        while (!BinaryStdIn.isEmpty()) {
            int pos = BinaryStdIn.readInt(8);
            BinaryStdOut.write(aux[pos]);
            move(aux, aux[pos], false);
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if (args[0].equals("-"))
            MoveToFront.encode();
        if (args[0].equals("+"))
            MoveToFront.decode();
    }


}