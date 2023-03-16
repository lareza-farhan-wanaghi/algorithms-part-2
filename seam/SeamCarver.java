
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.LinkedStack;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
    private CalculatedPixel[][] picturePixels;
    private boolean isTransposed;

    private static class CalculatedPixel {
        private int rgb;
        private double energy;

        public CalculatedPixel(int r, double e) {
            rgb = r;
            energy = e;
        }
    }

    private static class Node {
        private int idx1;
        private int idx2;
        private Node prev;
        private Bag<Node> adj;
        private double distance;

        public Node(int idx1, int idx2) {
            this.idx1 = idx1;
            this.idx2 = idx2;
        }

        public void addAdj(Node node) {
            if (adj == null) {
                adj = new Bag<>();
            }
            adj.add(node);
        }

        public void relax(double newDistance, Node prev) {
            if (this.prev == null || newDistance < distance) {
                this.prev = prev;
                this.distance = newDistance;
            }
        }

        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + idx1;
            hash = 31 * hash + 1;
            hash = 31 * hash + idx2;
            hash = 31 * hash + 2;
            return hash;
        }

        public void print() {
            StdOut.println(String.format("%d-%d %f", idx1, idx2, distance));
        }
    }

    private static class ShortestPath {
        private SeamCarver seam;
        private ST<Integer, Node> marker;
        private LinkedStack<Node> topologicalOrder;
        private Node shortestEndNode;

        public ShortestPath(SeamCarver seam) {
            this.seam = seam;
            shortestEndNode = null;
            marker = new ST<>();
            topologicalOrder = new LinkedStack<>();
            for (int j = 0; j < this.seam.internalHeight(); j++) {
                Node firstNode = new Node(0, j);
                marker.put(firstNode.hashCode(), firstNode);
                dfs(firstNode);
            }
            rateTopology();
        }

        private void dfs(Node pair) {
            for (int i = -1; i < 2; i++) {
                int nextX = pair.idx1 + 1;
                int nextY = pair.idx2 + i;
                if (nextX >= seam.internalWidth())
                    break;
                if (nextY < 0 || nextY >= seam.internalHeight())
                    continue;

                Node adj = new Node(nextX, nextY);
                int adjHash = adj.hashCode();
                if (!marker.contains(adjHash)) {
                    marker.put(adjHash, adj);
                    dfs(adj);
                } else {
                    adj = marker.get(adjHash);
                }
                pair.addAdj(adj);
            }
            topologicalOrder.push(pair);
        }

        private void rateTopology() {
            for (Node prev : topologicalOrder) {
                if (prev.idx1 < seam.internalWidth() - 1) {
                    for (Node cur : prev.adj) {
                        cur.relax(prev.distance + seam.picturePixels[cur.idx1][cur.idx2].energy,
                                prev);
                    }
                } else if (shortestEndNode == null || prev.distance < shortestEndNode.distance) {
                    shortestEndNode = prev;
                }
            }
        }

        public int[] getShortestPath() {
            int[] result = new int[seam.internalWidth()];
            Node cur = shortestEndNode;
            for (int i = seam.internalWidth() - 1; i >= 0; i--) {
                result[i] = cur.idx2;
                cur = cur.prev;
            }
            return result;
        }
    }

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }

        picturePixels = new CalculatedPixel[picture.width()][picture.height()];
        for (int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                double energy = 0;
                if (i == 0 || i == picture.width() - 1 || j == 0 || j == picture.height() -
                        1) {
                    energy = 1000;
                } else {
                    energy = computeEnergy(
                            picture.getRGB(i + 1, j),
                            picture.getRGB(i - 1, j),
                            picture.getRGB(i, j + 1),
                            picture.getRGB(i, j - 1));
                }

                picturePixels[i][j] = new CalculatedPixel(picture.getRGB(i, j), energy);
            }
        }
    }

    // transpose in-place
    private void transposePixels() {
        if (internalHeight() == internalWidth()) {
            for (int i = 0; i < internalWidth(); i++) {
                for (int j = i + 1; j < internalHeight(); j++) {
                    CalculatedPixel temp = picturePixels[i][j];
                    picturePixels[i][j] = picturePixels[j][i];
                    picturePixels[j][i] = temp;
                }
            }
        } else {
            CalculatedPixel[][] result = new CalculatedPixel[internalHeight()][internalWidth()];
            for (int i = 0; i < internalWidth(); i++)
                for (int j = 0; j < internalHeight(); j++) {
                    result[j][i] = picturePixels[i][j];
                }
            picturePixels = result;
        }
        isTransposed = !isTransposed;
    }

    private double computeEnergy(int px, int xp, int py, int yp) {
        return Math.sqrt(Math.pow(((px >> 16) & 0xff) - ((xp >> 16) & 0xff), 2) +
                Math.pow(((px >> 8) & 0xff) - ((xp >> 8) & 0xff), 2) +
                Math.pow((px & 0xff) - (xp & 0xff), 2) +
                Math.pow(((py >> 16) & 0xff) - ((yp >> 16) & 0xff), 2) +
                Math.pow(((py >> 8) & 0xff) - ((yp >> 8) & 0xff), 2) +
                Math.pow((py & 0xff) - (yp & 0xff), 2));
    }

    private void removeSeam(int[] seam) {
        if (seam == null || seam.length != internalWidth() || internalHeight() <= 1) {
            throw new IllegalArgumentException();
        }

        CalculatedPixel[][] result = new CalculatedPixel[internalWidth()][internalHeight() - 1];
        Bag<Node> recomputeEnergy = new Bag<>();

        int prevY = seam[0];
        int x = 0;
        for (int y : seam) {
            if (y < 0 || y >= internalHeight() || Math.abs(prevY - y) > 1) {
                throw new IllegalArgumentException();
            }
            System.arraycopy(picturePixels[x], 0, result[x], 0, y);
            System.arraycopy(picturePixels[x], y + 1, result[x], y, internalHeight() - 1 - y);
            if (y < internalHeight() - 1) {
                recomputeEnergy.add(new Node(x, y));
                if (x + 1 < internalWidth()) {
                    recomputeEnergy.add(new Node(x + 1, y));
                }
                if (x - 1 >= 0) {
                    recomputeEnergy.add(new Node(x - 1, y));
                }
            }
            if (y - 1 >= 0) {
                recomputeEnergy.add(new Node(x, y - 1));
            }
            prevY = y;
            x++;
        }
        picturePixels = result;
        for (

        Node indexPair : recomputeEnergy) {
            int i = indexPair.idx1;
            int j = indexPair.idx2;
            double energy = 0;
            if (i == 0 || i == internalWidth() - 1 || j == 0 || j == internalHeight() - 1) {
                energy = 1000;
            } else {
                energy = computeEnergy(
                        picturePixels[i + 1][j].rgb,
                        picturePixels[i - 1][j].rgb,
                        picturePixels[i][j + 1].rgb,
                        picturePixels[i][j - 1].rgb);
                // StdOut.printf("rgb:%d px:%d, xp:%d, py:%d, yp:%d , e:%f \n",
                // picturePixels[i][j].rgb,
                // picturePixels[i + 1][j].rgb,
                // picturePixels[i - 1][j].rgb,
                // picturePixels[i][j + 1].rgb,
                // picturePixels[i][j - 1].rgb, energy);
            }
            picturePixels[i][j].energy = energy;
        }
    }

    // internalWidth of current picture
    private int internalWidth() {
        return picturePixels.length;
    }

    // internalHeight of current picture
    private int internalHeight() {
        return picturePixels[0].length;
    }

    // current picture
    public Picture picture() {
        Picture p;
        if (isTransposed) {
            p = new Picture(internalHeight(), internalWidth());
            for (int y = 0; y < internalHeight(); y++) {
                for (int x = 0; x < internalWidth(); x++) {
                    p.setRGB(y, x, picturePixels[x][y].rgb);
                }
            }
        } else {
            p = new Picture(internalWidth(), internalHeight());
            for (int x = 0; x < internalWidth(); x++) {
                for (int y = 0; y < internalHeight(); y++) {
                    p.setRGB(x, y, picturePixels[x][y].rgb);
                }
            }
        }
        return p;
    }

    // internalWidth of current picture
    public int width() {
        if (isTransposed)
            return internalHeight();
        else
            return internalWidth();
    }

    // internalHeight of current picture
    public int height() {
        if (isTransposed)
            return internalWidth();
        else
            return internalHeight();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) {
            throw new IllegalArgumentException();
        }
        if (isTransposed) {
            return picturePixels[y][x].energy;
        } else {
            return picturePixels[x][y].energy;
        }
    }

    // public int rgb(int x, int y) {
    // if (x < 0 || x >= width() || y < 0 || y >= height()) {
    // throw new IllegalArgumentException();
    // }
    // if (isTransposed) {
    // return picturePixels[y][x].rgb;
    // } else {
    // return picturePixels[x][y].rgb;
    // }
    // }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (isTransposed) {
            transposePixels();
        }
        ShortestPath sp = new ShortestPath(this);
        return sp.getShortestPath();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (!isTransposed) {
            transposePixels();
        }
        ShortestPath sp = new ShortestPath(this);
        return sp.getShortestPath();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (isTransposed) {
            transposePixels();
        }
        removeSeam(seam);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (!isTransposed) {
            transposePixels();
        }
        removeSeam(seam);
    }

    public static void main(String[] args) {
        String[][] a = new String[][] {
                new String[] { "090506", "090804", "070403", "070600", "060307", "050002" },
                new String[] { "030702", "040609", "020703", "010207", "060709", "000708" },
                new String[] { "010009", "090300", "020905", "000505", "070401", "070201" },
                new String[] { "050808", "090801", "070701", "080500", "050507", "000401" },
                new String[] { "040302", "020205", "050900", "090002", "030600", "050608" },
                new String[] { "090908", "050401", "090405", "050004", "080902", "010509" }
        };
        Picture picture = new Picture(a.length, a[0].length);
        for (int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                picture.setRGB(i, j, Integer.parseInt(a[i][j], 16));
            }
        }
        SeamCarver cv = new SeamCarver(picture);
        cv.removeVerticalSeam(new int[] { 0, 1, 2, 2, 3, 2 });
    }
}
