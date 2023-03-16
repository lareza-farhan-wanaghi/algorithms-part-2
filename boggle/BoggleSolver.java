import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    private Node root;
    private SET<String> validwords;
    private SET<Integer> traversedIndex;

    // Initializes the data structure using the given array of strings as the
    // dictionary.
    // (You can assume each word in the dictionary contains only the uppercase
    // letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (String s : dictionary) {
            if (s.length() >= 3) {
                root = insertNode(root, s, 1);
            }
        }
    }

    private static class Node {
        char c;
        int v;
        Node left, mid, right;

        public Node(char c, int v) {
            this.c = c;
            this.v = v;
        }
    }

    private Node insertNode(Node n, String s, int d) {
        char c = s.charAt(d - 1);
        if (n == null) {
            n = new Node(c, 0);
        }
        int cv = Character.compare(n.c, c);
        if (cv == 0) {
            if (d == s.length()) {
                n.v = 1;
            } else {
                n.mid = insertNode(n.mid, s, d + 1);
            }
        } else if (cv < 0) {
            n.right = insertNode(n.right, s, d);
        } else {
            n.left = insertNode(n.left, s, d);
        }
        return n;
    }

    private Node getNode(Node n, char c) {
        if (n == null) {
            return null;
        }

        int cv = Character.compare(n.c, c);
        if (cv == 0) {
            return n;
        } else if (cv < 0) {
            return getNode(n.right, c);
        } else {
            return getNode(n.left, c);
        }
    }

    private boolean getNode(Node n, String s, int d) {
        if (n == null) {
            return false;
        }

        int cv = Character.compare(n.c, s.charAt(d - 1));
        if (cv == 0) {
            if (d == s.length()) {
                return n.v > 0;
            } else {
                return getNode(n.mid, s, d + 1);
            }
        } else if (cv < 0) {
            return getNode(n.right, s, d);
        } else {
            return getNode(n.left, s, d);
        }
    }

    private int indexHash(int i, int j) {
        int h = 7;
        h = h * 31 + i;
        h = h * 31 + 1;
        h = h * 31 + j;
        h = h * 31 + 2;
        return h;
    }

    private void search(BoggleBoard b, int i, int j, StringBuilder sb, Node ln) {
        if (i < 0 || j < 0 || i >= b.rows() || j >= b.cols())
            return;
        int hash = indexHash(i, j);
        boolean isQ = b.getLetter(i, j) == 'Q';
        Node n = getNode(ln, b.getLetter(i, j));
        if (n != null && isQ) {
            n = getNode(n.mid, 'U');
        }

        if (n != null && !traversedIndex.contains(hash)) {
            traversedIndex.add(hash);
            if (isQ) {
                sb.append('Q');
            }
            sb.append(n.c);
            if (n.v > 0 && sb.length() >= 3) {
                validwords.add(sb.toString());
            }

            for (int ii = -1; ii <= 1; ii++) {
                for (int jj = -1; jj <= 1; jj++) {
                    if (ii != 0 || jj != 0) {
                        search(b, i + ii, j + jj, sb, n.mid);
                    }
                }
            }
            sb.setLength(Math.max(sb.length() - 1, 0));
            if (isQ) {
                sb.setLength(Math.max(sb.length() - 1, 0));
            }
            traversedIndex.remove(hash);
        }

    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        validwords = new SET<>();
        traversedIndex = new SET<>();
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                search(board, i, j, new StringBuilder(), root);
            }
        }
        return validwords;
    }

    // Returns the score of the given word if it is in the dictionary, zero
    // otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word.length() >= 3 && getNode(root, word, 1)) {
            // return 100;
            if (word.length() <= 4)
                return 1;
            if (word.length() <= 5)
                return 2;
            if (word.length() <= 6)
                return 3;
            if (word.length() <= 7)
                return 5;
            else
                return 11;
        } else {
            return 0;
        }

    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        StdOut.println(solver.scoreOf("INTRODUCTION"));
        BoggleBoard board = new BoggleBoard(args[1]);
        StdOut.println(board.getLetter(0, 0));
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
