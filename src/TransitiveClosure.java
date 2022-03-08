import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedDFS;

public class TransitiveClosure {
    DirectedDFS[] all;

    public TransitiveClosure(Digraph digraph) {
        all = new DirectedDFS[digraph.V()];
        for (int i = 0; i < digraph.V(); i++) {
            all[i] = new DirectedDFS(digraph, i);
        }
    }

    boolean reachable(int v, int w) {
        return all[v].marked(w);
    }

    public static void main(String[] args) {

    }
}
