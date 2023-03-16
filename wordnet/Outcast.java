import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private WordNet wn;

    public Outcast(WordNet wordnet) // constructor takes a WordNet object
    {
        wn = wordnet;
    }

    public String outcast(String[] nouns) { // given an array of WordNet nouns, return an outcast{
        int[] scores = new int[nouns.length];
        int highest = 0;
        int resultIndex = 0;
        for (int i = 0; i < nouns.length; i++) {
            for (int j = i + 1; j < nouns.length; j++) {
                int score = wn.distance(nouns[i], nouns[j]);
                scores[i] += score;
                scores[j] += score;
                if (scores[i] > highest) {
                    highest = scores[i];
                    resultIndex = i;
                }
                if (scores[j] > highest) {
                    highest = scores[j];
                    resultIndex = j;
                }
            }
        }

        return nouns[resultIndex];
    }

    public static void main(String[] args) // see test client below{
    {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }

}