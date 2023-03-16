import java.util.ArrayList;
import java.util.Arrays;
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static class Item implements Comparable<Item> {
        int i;
        char c;

        public Item(int i, char c) {
            this.i = i;
            this.c = c;
        }

        @Override
        public int compareTo(Item o) {
            return c - o.c;
        }
    }

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray cs = new CircularSuffixArray(s);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            int j = cs.index(i);
            sb.append(s.charAt((j + s.length() - 1) % s.length()));
            if (j == 0) {
                BinaryStdOut.write(i);
            }
        }

        BinaryStdOut.write(sb.toString());
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int i = BinaryStdIn.readInt();
        ArrayList<Item> sca = new ArrayList<>();
        for (int j = 0; !BinaryStdIn.isEmpty(); j++) {
            sca.add(new Item(j, BinaryStdIn.readChar()));
        }
        sca.sort(null);

        int j = i;
        for (int k = 0; k < sca.size(); k++) {
            BinaryStdOut.write(sca.get(j).c);
            j = sca.get(j).i;
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform *************
    // if args[0] is "+", apply Burrows-Wheeler inverse transform **************
    public static void main(String[] args) {
        if (args[0].charAt(0) == '-') {
            transform();
        } else {
            inverseTransform();
        }
    }

}
