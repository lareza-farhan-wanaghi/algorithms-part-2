import java.util.ArrayList;
import java.util.HashMap;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class WordNet {
    private Digraph G;
    private ArrayList<String> synsetMap;
    private HashMap<String, Bag<Integer>> nounIndeciesMap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        synsetMap = new ArrayList<>();
        nounIndeciesMap = new HashMap<>();

        In synsetIn = new In(synsets);
        while (synsetIn.hasNextLine()) {
            String[] lineContent = synsetIn.readLine().split(",");
            int synsetIndex = Integer.parseInt(lineContent[0]);
            synsetMap.add(lineContent[1]);
            for (String s : lineContent[1].split("\\s+")) {
                if (!nounIndeciesMap.containsKey(s)) {
                    nounIndeciesMap.put(s, new Bag<>());
                }
                nounIndeciesMap.get(s).add(synsetIndex);
            }
        }

        G = new Digraph(synsetMap.size());
        In hypernymsIn = new In(hypernyms);
        while (hypernymsIn.hasNextLine()) {
            String[] lineContent = hypernymsIn.readLine().split(",");
            int source = Integer.parseInt(lineContent[0]);
            for (int i = 1; i < lineContent.length; i++) {
                G.addEdge(source, Integer.parseInt(lineContent[i]));
            }
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounIndeciesMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return nounIndeciesMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        SAP sap = new SAP(G);
        return sap.length(nounIndeciesMap.get(nounA), nounIndeciesMap.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA
    // and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        SAP sap = new SAP(G);
        int ancestorIndex = sap.ancestor(nounIndeciesMap.get(nounA), nounIndeciesMap.get(nounB));
        return ancestorIndex < 0 ? null : synsetMap.get(ancestorIndex);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        StdOut.println("Creating WN..");
        WordNet wn = new WordNet(args[0], args[1]);
        StdOut.println("Finished creating WN");

        String someNoun = "";
        int i = 0;
        for (String n : wn.nouns()) {
            someNoun = n;
            StdOut.println(someNoun);
            i += 1;
            if (i >= 5)
                break;
        }

        StdOut.println(wn.isNoun(someNoun));
        StdOut.println(wn.isNoun("I_bEt_ThIs_Word_IsNot_inCluded"));

        while (!StdIn.isEmpty()) {
            String n1 = StdIn.readString();
            String n2 = StdIn.readString();
            int distance = wn.distance(n1, n2);
            StdOut.println(String.format("distance = %d", distance));
            String ancestor = wn.sap(n1, n2);
            StdOut.println(String.format("ancestor synsets= %s", ancestor));
        }
    }
}