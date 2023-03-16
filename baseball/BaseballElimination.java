import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
    private ST<String, int[]> st;
    private SET<String>[] cert;
    private int highestWR;

    public BaseballElimination(String filename) { // create a baseball division from given filename in format
        In in = new In(filename);
        int numOfTeam = Integer.parseInt(in.readLine());
        st = new ST<>();
        int offsite = 0;
        for (int i = 0; i < numOfTeam; i++) {
            String teamName = in.readString();
            int[] content = new int[5 + numOfTeam - i - 1];
            content[0] = i;
            content[1] = in.readInt();
            content[2] = in.readInt();
            content[3] = in.readInt();
            content[4] = offsite;
            int nextIndex = 5;
            for (int j = 0; j < numOfTeam; j++) {
                int r = in.readInt();
                if (j > i) {
                    content[nextIndex++] = r;
                }
            }
            if (content[1] + content[3] > highestWR) {
                highestWR = content[1] + content[3];
            }
            offsite += numOfTeam - i - 1;
            st.put(teamName, content);
        }
        cert = new SET[numOfTeam];
    }

    private boolean ar(String team) {
        int wr = st.get(team)[1] + st.get(team)[3];
        int wt = 0;
        int gt = 0;
        for (String s : cert[st.get(team)[0]]) {
            wt += st.get(s)[1];
            gt += st.get(s)[3];
        }
        return wr < (wt + gt) / cert[st.get(team)[0]].size();
    }

    private void inspectTeam(String team) {
        int ti = st.get(team)[0];
        if (cert[ti] != null) {
            return;
        }
        int tal = st.get(team).length - 5;
        int tawr = st.get(team)[1] + st.get(team)[3];
        int n = descendingVal(numberOfTeams() - 1) + 1;
        FlowNetwork fn = new FlowNetwork(n + numberOfTeams() - 1 + 1);
        cert[ti] = new SET<>();
        for (String stTeam : st.keys()) {
            int[] stVal = st.get(stTeam);
            if (ti > stVal[0]) {
                int f = 1 + stVal[4] - stVal[0];
                int dif = tawr - stVal[1];
                if (dif < 0) {
                    cert[ti].add(stTeam);
                }
                for (int i = 5; i < stVal.length; i++) {
                    int x = ti - stVal[0] - 1;
                    if (x == i - 5)
                        continue;
                    int y = i - 5 + (x > i - 5 ? 1 : 0);

                    fn.addEdge(new FlowEdge(0, f, stVal[i]));
                    fn.addEdge(new FlowEdge(f, n + stVal[0], Double.POSITIVE_INFINITY));
                    fn.addEdge(new FlowEdge(f, n + stVal[0] + y, Double.POSITIVE_INFINITY));
                    f += 1;
                }
                fn.addEdge(new FlowEdge(n + stVal[0], fn.V() - 1, Math.max(0, dif)));

            } else if (ti < stVal[0]) {
                int f = 1 + stVal[4] - tal - ti;
                int dif = tawr - stVal[1];
                if (dif < 0) {
                    cert[ti].add(stTeam);
                }
                for (int i = 5; i < stVal.length; i++) {
                    fn.addEdge(new FlowEdge(0, f, stVal[i]));
                    fn.addEdge(new FlowEdge(f, n + stVal[0] - 1, Double.POSITIVE_INFINITY));
                    fn.addEdge(new FlowEdge(f, n + stVal[0] + i - 5, Double.POSITIVE_INFINITY));
                    f += 1;
                }
                fn.addEdge(new FlowEdge(n + stVal[0] - 1, fn.V() - 1, Math.max(0, dif)));

            }
        }

        FordFulkerson ff = new FordFulkerson(fn, 0, fn.V() - 1);
        // StdOut.println(team + " is being inspected");
        // StdOut.println(fn.toString());
        // StdOut.println(ff.value());

        for (String stTeam : st.keys()) {
            if (cert[ti].contains(stTeam))
                continue;

            int[] stVal = st.get(stTeam);
            if (ti > stVal[0]) {
                if (ff.inCut(n + stVal[0])) {
                    cert[ti].add(stTeam);
                }

            } else if (ti < stVal[0]) {
                if (ff.inCut(n + stVal[0] - 1)) {
                    cert[ti].add(stTeam);
                }
            }
        }
    }

    private int descendingVal(int n) {
        return n * (n - 1) / 2;
    }

    public int numberOfTeams() {// number of teams
        return st.size();
    }

    public Iterable<String> teams() {// all teams
        return st.keys();
    }

    public int wins(String team) {// number of wins for given team
        if (!st.contains(team)) {
            throw new IllegalArgumentException();
        }

        return st.get(team)[1];
    }

    public int losses(String team) {// number of losses for given team
        if (!st.contains(team)) {
            throw new IllegalArgumentException();
        }

        return st.get(team)[2];
    }

    public int remaining(String team) {// number of remaining games for given team
        if (!st.contains(team)) {
            throw new IllegalArgumentException();
        }

        return st.get(team)[3];
    }

    public int against(String team1, String team2) {// number of remaining games between team1 and team2
        if (!st.contains(team1) || !st.contains(team2)) {
            throw new IllegalArgumentException();
        }

        int team1Index = st.get(team1)[0];
        int team2Index = st.get(team2)[0];
        if (team1Index == team2Index) {
            return 0;
        } else {
            return st.get(team1Index < team2Index ? team1 : team2)[5 + Math.abs(team2Index - team1Index) - 1];
        }
    }

    public boolean isEliminated(String team) {// is given team eliminated?
        if (!st.contains(team)) {
            throw new IllegalArgumentException();
        }

        inspectTeam(team);
        return cert[st.get(team)[0]].size() > 0;
    }

    public Iterable<String> certificateOfElimination(String team) {
        if (!st.contains(team)) {
            throw new IllegalArgumentException();
        }

        return isEliminated(team) ? cert[st.get(team)[0]] : null;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination("teams12-allgames.txt");

        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.print("}");
                StdOut.println(" A = " + division.ar(team));
                StdOut.println("----------");
            } else {
                StdOut.println(team + " is not eliminated");
                StdOut.println("----------");
            }
        }

    }
}