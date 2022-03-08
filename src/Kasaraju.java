import edu.princeton.cs.algs4.Digraph;

public class Kasaraju {
    boolean[] marked;
    int[] id;
    int count;

    public Kasaraju(Digraph G) {
        marked = new boolean[G.V()];
        id = new int[G.V()];
        DebthFirstOrder debthFirstOrder = new DebthFirstOrder(G.reverse());
        for (int s : debthFirstOrder.reversePost) {
            if (!marked[s]) {
                dfs(G, s);
                count++;
            }
        }
    }

    public void dfs(Digraph graph, int v) {
        marked[v] = true;
        id[v] = count;
        for (int w : graph.adj(v)) {
            if (!marked[w]) dfs(graph, w);
        }
    }

    public boolean stronglyConnected(int v, int w) {
        return id[v] == id[w];
    }

    public int id(int v) {
        return id[v];
    }

    public int count() {
        return count;
    }

    public static void main(String[] args) {

    }
}
