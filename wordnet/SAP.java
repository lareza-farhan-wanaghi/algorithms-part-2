import java.util.HashMap;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.G = G;
    }

    private static class SAPNode {
        private int val;
        private int dist;
        private boolean isBlue;

        private SAPNode(int val, int dist, boolean isBlue) {
            this.val = val;
            this.dist = dist;
            this.isBlue = isBlue;
        }
    }

    private int findDistOrAncestor(Iterable<Integer> v, Iterable<Integer> w, boolean isReturningLength) {
        HashMap<Integer, SAPNode> exploredNode = new HashMap<>();
        Queue<SAPNode> q = new Queue<>();

        int shortestFoundDist = Integer.MAX_VALUE;
        int anchestor = -1;

        for (int vv : v) {
            SAPNode newNode = new SAPNode(vv, 0, true);
            q.enqueue(newNode);
            exploredNode.put(vv, newNode);
        }
        for (int ww : w) {
            if (!exploredNode.containsKey(ww)) {
                SAPNode newNode = new SAPNode(ww, 0, false);
                q.enqueue(newNode);
                exploredNode.put(ww, newNode);
            } else {
                shortestFoundDist = 0;
                anchestor = ww;
            }
        }

        while (q.size() > 0) {
            SAPNode cur = q.dequeue();
            int nextDist = cur.dist + 1;
            if (nextDist < shortestFoundDist) {
                for (int adj : G.adj(cur.val)) {
                    if (!exploredNode.containsKey(adj)) {
                        SAPNode newNode = new SAPNode(adj, nextDist, cur.isBlue);
                        q.enqueue(newNode);
                        exploredNode.put(adj, newNode);
                    } else {
                        SAPNode intersect = exploredNode.get(adj);
                        if (intersect.isBlue != cur.isBlue && nextDist + intersect.dist < shortestFoundDist) {
                            shortestFoundDist = nextDist + intersect.dist;
                            anchestor = intersect.val;
                        }
                    }
                }
            }
        }

        if (isReturningLength) {
            return anchestor == -1 ? -1 : shortestFoundDist;
        } else {
            return anchestor;
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        Bag<Integer> vv = new Bag<>();
        vv.add(v);
        Bag<Integer> ww = new Bag<>();
        ww.add(w);
        return findDistOrAncestor(vv, ww, true);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path;
    // -1 if no such path
    public int ancestor(int v, int w) {
        Bag<Integer> vv = new Bag<>();
        vv.add(v);
        Bag<Integer> ww = new Bag<>();
        ww.add(w);
        return findDistOrAncestor(vv, ww, false);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in
    // w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return findDistOrAncestor(v, w, true);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such
    // path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return findDistOrAncestor(v, w, false);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        StdOut.println("Creating SAP..");
        SAP sap = new SAP(G);
        StdOut.println("Finished creating SAP");
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            StdOut.println(String.format("length = %d", length));
            int ancestor = sap.ancestor(v, w);
            StdOut.println(String.format("ancestor = %d", ancestor));
        }

    }
}
