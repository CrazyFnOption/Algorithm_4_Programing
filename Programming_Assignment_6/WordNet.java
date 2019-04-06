


import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Topological;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;


public class WordNet {
    private Map<String, TreeSet<Integer> > synSets;
    private Map<Integer, String> ssynsets;
    private Digraph hyperNyms;
    private int idSum;
    private int outSum;
    private SAP sap;


    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("arguments to WordNet() is null");
        }

        readsynset(synsets);
        readhypernyms(hypernyms);
    }

    private void readsynset(String synsets) {

        synSets = new TreeMap<String, TreeSet<Integer> >();
        this.ssynsets = new TreeMap<Integer, String>();
        In synset = new In(synsets);
        idSum = 0;

        while (synset.hasNextLine()) {

            idSum++;
            String str = synset.readLine();
            String[] field = str.split(",");
            int id = Integer.parseInt(field[0]);
            this.ssynsets.put(id, field[1]);
            String[] nons = field[1].split(" ");
            for (String tmp : nons) {
                if (synSets.containsKey(tmp)) {
                    if (synSets.containsKey(tmp)) {
                        synSets.get(tmp).add(id);
                    }
                }
                else {
                    TreeSet<Integer> ids = new TreeSet<>();
                    ids.add(id);
                    synSets.put(tmp, ids);
                }
            }
        }
    }

    private void readhypernyms(String hypernyms) {

        hyperNyms = new Digraph(idSum);
        In hypernym = new In(hypernyms);
        boolean[] outToal = new boolean[idSum];

        while (hypernym.hasNextLine()) {
            String str = hypernym.readLine();
            String[] field = str.split(",");
            int v = Integer.parseInt(field[0]);
            for (int i = 1; i < field.length; i++) {
                int w = Integer.parseInt(field[i]);
                hyperNyms.addEdge(v, w);
            }
            if (!outToal[v] && field.length > 1) {
                outSum++;
            }
            outToal[v] = true;
        }
        isRootRAG();
        sap = new SAP(hyperNyms);
    }

    private void isRootRAG() {
        if (idSum - outSum != 1) {
            throw new IllegalArgumentException("more than one root");
        }
        Topological TO = new Topological(hyperNyms);
        if (!TO.hasOrder()) {
            throw new IllegalArgumentException("is not a Root RAG");
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return synSets.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("word is null");
        return synSets.containsKey(word);
    }


    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("nounA or nounB is null");
        }
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("the two noun is not exist");
        }
        TreeSet<Integer> setA = synSets.get(nounA);
        TreeSet<Integer> setB = synSets.get(nounB);
        if (setA.size() == 1 && setB.size() == 1) {
            return sap.length(setA.last(), setB.last());
        }
        else return sap.length(setA, setB);

    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException();
        }

        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }
        TreeSet<Integer> setA = synSets.get(nounA);
        TreeSet<Integer> setB = synSets.get(nounB);
        int ID;
        if (setA.size() == 1 && setB.size() == 1) {
            ID = sap.ancestor(setA.last(), setB.last());
        }
        else ID = sap.ancestor(setA, setB);
        return ssynsets.get(ID);
    }

}
