import java.util.Arrays;
import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {
    private int[] indeces;

    private static class Item implements Comparable<Item> {
        int i;
        static String s;

        public Item(int i) {
            this.i = i;
        }

        @Override
        public int compareTo(Item o) {
            for (int j = 0; j < s.length(); j++) {
                int k = s.charAt((i + j) % s.length()) - s.charAt((o.i + j) % s.length());
                if (k < 0) {
                    return -1;
                } else if (k > 0) {
                    return 1;
                }
            }
            return 0;
        }
    }

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }

        indeces = new int[s.length()];
        Item.s = s;
        Item[] items = new Item[s.length()];
        boolean isSorted = true;
        for (int i = 0; i < s.length(); i++) {
            if (isSorted && i < s.length() - 1 && s.charAt(i) != s.charAt(i + 1)) {
                isSorted = false;
            }

            items[i] = new Item(i);
        }

        if (!isSorted) {
            Arrays.sort(items);
        }
        for (int i = 0; i < s.length(); i++) {
            indeces[i] = items[i].i;
        }
    }

    // length of s
    public int length() {
        return indeces.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length()) {
            throw new IllegalArgumentException();
        }

        return indeces[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String s = "AAAAA";
        CircularSuffixArray cs = new CircularSuffixArray(s);
        for (int i = 0; i < s.length(); i++) {
            StdOut.println(cs.index(i));
        }
    }

}
