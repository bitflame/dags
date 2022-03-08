import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

import java.util.Stack;

public class DebthFirstOrder {
    Stack<Integer> reversePost;
    Queue<Integer> preorder;
    Queue<Integer> postorder;
    boolean[] marked;

    public DebthFirstOrder(Digraph G) {
        reversePost = new Stack<>();
        postorder = new Queue<>();
        preorder = new Queue<>();
        for (int i = 0; i < G.V(); i++) {
            if (!marked[i]) dfs(G, i);
        }
    }

    public void dfs(Digraph digraph, int v) {
        marked[v] = true;
        preorder.enqueue(v);
        for (int w : digraph.adj(v)) {
            if (!marked[w]) dfs(digraph, w);
        }
        postorder.enqueue(v);
        reversePost.push(v);
    }

    public Iterable<Integer> pre() {
        return preorder;
    }

    public Iterable<Integer> post() {
        return postorder;
    }

    public Iterable<Integer> reversePost() {
        return reversePost;
    }
}
