import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Topological;

public class Test {
    private int count;
    private int[] id = new int[count];

    public void union(int p, int q) {
        int pid = find(p);
        int qid = find(q);
        if (pid == qid)
            return;
        for (int index = 0; index < id.length; index++) {
            if (id[index] == pid)
                id[index] = qid;
            count--;
        }
    }

    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    public int find(int i) {
        return id[i];
    }

    // quickfind complexity - (N+3) - 2(N+1)
    public int count() {
        return count;
    }

    public boolean matches(String s1, String s2) {
        int count = 0;
        char[] firstStringArray = s1.toCharArray();
        char[] secondStringArray = s2.toCharArray();
        while (count < Math.min(s1.length(), s2.length())) {
            if (firstStringArray[count] != secondStringArray[count])
                return false;
        }
        return true;
    }

    // 1.5.23 Doubling test
    public static void main(String[] args) {
        /* How come I get answer for tinyDG.txt but not for digraph3.txt I think if the cycle is connected to the rest
        * of the graph, it can be ordered topologically but if it has two separate components, then it can not. I think
        * the expected answer for digraph3 is only if the nodes are in the same component, if not, you return -1*/
         Topological topological = new Topological(new Digraph(new In("digraph3.txt")));
        // Topological topological = new Topological(new Digraph(new In("tinyDG.txt")));
        for (int v : topological.order()) {
            StdOut.print(" " + v);
        }
        System.out.println();
    }
}
