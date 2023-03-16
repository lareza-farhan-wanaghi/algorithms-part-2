import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    // apply move-to-front encoding, reading from standard input and writing to
    // standard output

    public static void encode() {
        char[] sc = new char[256];
        for (int i = 0; i < sc.length; i++) {
            sc[i] = (char) i;
        }
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int indexOfC = -1;
            for (int i = 0; i < sc.length; i++) {
                if (sc[i] == c) {
                    indexOfC = i;
                    break;
                }
            }
            System.arraycopy(sc, 0, sc, 1, indexOfC);
            sc[0] = c;
            BinaryStdOut.write((byte) indexOfC);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to
    // standard output
    public static void decode() {
        char[] sc = new char[256];
        for (int i = 0; i < sc.length; i++) {
            sc[i] = (char) i;
        }
        while (!BinaryStdIn.isEmpty()) {
            int i = Byte.toUnsignedInt(BinaryStdIn.readByte());
            char c = sc[i];
            System.arraycopy(sc, 0, sc, 1, i);
            sc[0] = c;
            BinaryStdOut.write(c);
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].charAt(0) == '-') {
            encode();
        } else {
            decode();

        }
    }

}