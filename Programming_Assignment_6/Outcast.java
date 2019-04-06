

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordnet;
    public Outcast(WordNet wordnet) {
        if (wordnet == null) throw new IllegalArgumentException("wrong wordnet");
        this.wordnet = wordnet;
    }

    public String outcast(String[] nouns) {
        if (nouns == null) throw new IllegalArgumentException("this nouns is null");

        int Maxn = 0;
        int index = -1;
        for (int i = 0; i < nouns.length; i++) {
            int sum = 0;
            for (int j = 0; j < nouns.length; j++) {
                if (i == j) continue;
                int tmp = wordnet.distance(nouns[i], nouns[j]);
                if (tmp == -1) continue;
                sum += tmp;
            }
            if (sum > Maxn) {
                Maxn = sum;
                index = i;
            }
        }
        if (index == -1) throw new IllegalArgumentException("error");
        return nouns[index];
    }


    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}