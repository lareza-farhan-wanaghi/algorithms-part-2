
/******************************************************************************
 *  Compilation:  javac PrintEnergy.java
 *  Execution:    java PrintEnergy input.png
 *  Dependencies: SeamCarver.java
 *                
 *
 *  Read image from file specified as command line argument. Print energy
 *  of each pixel as calculated by SeamCarver object. 
 * 
 ******************************************************************************/

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class PrintEnergy {

    public static void main(String[] args) {
        String[][] a = new String[][] {
                new String[] { "090506", "090804", "070403", "070600", "060307", "050002" },
                new String[] { "030702", "040609", "020703", "010207", "060709", "000708" },
                new String[] { "010009", "090300", "020905", "000505", "070401", "070201" },
                new String[] { "050808", "090801", "070701", "080500", "050507", "000401" },
                new String[] { "040302", "020205", "050900", "090002", "030600", "050608" },
                new String[] { "090908", "050401", "090405", "050004", "080902", "010509" }
        };
        Picture picture = new Picture("3x7.png");
        StdOut.printf("image is %d pixels wide by %d pixels high.\n", picture.width(), picture.height());
        StdOut.printf("Printing energy calculated for each pixel.\n");
        int[] aa = new int[] { 0, 0, 0, 0, 1, 2, 1 };
        SeamCarver sc = new SeamCarver(picture);
        sc.removeVerticalSeam(aa);

        // for (int row = 0; row < sc.height(); row++) {
        // for (int col = 0; col < sc.width(); col++)
        // StdOut.printf("%s%f%s \t\t", aa[row] == col ? "[" : "", sc.energy(col, row),
        // aa[row] == col ? "]" : "");
        // StdOut.println();
        // }
        // StdOut.println();
        // for (int row = 0; row < sc.height(); row++) {
        // for (int col = 0; col < sc.width(); col++)
        // StdOut.printf("%d \t\t", sc.rgb(col, row));
        // StdOut.println();
        // }
        // StdOut.println();
        // sc.removeVerticalSeam(aa);
        // for (int row = 0; row < sc.height(); row++) {
        // for (int col = 0; col < sc.width(); col++)
        // StdOut.printf("%s%f%s \t\t", aa[row] == col ? "[" : "", sc.energy(col, row),
        // aa[row] == col ? "]" : "");
        // StdOut.println();
        // }
        // StdOut.println();
        // for (int row = 0; row < sc.height(); row++) {
        // for (int col = 0; col < sc.width(); col++)
        // StdOut.printf("%d \t\t", sc.rgb(col, row));
        // StdOut.println();
        // }
    }

}
