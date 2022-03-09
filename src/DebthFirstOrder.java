import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
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
        marked = new boolean[G.V()];
        for (int i = 0; i < G.V(); i++) {
            if (!marked[i]) dfs(G, i);
        }
    }

    public void dfs(Digraph digraph, int v) {
        marked[v] = true;
        preorder.enqueue(v);
        for (int w : digraph.adj(v))
            if (!marked[w]) dfs(digraph, w);
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

    public static void main(String[] args) {
        Digraph digraph = new Digraph(new In("tinyDG.txt"));
        DebthFirstOrder debthFirstOrder = new DebthFirstOrder(digraph);
        System.out.println("Here is nodes in preorder: ");
        for (int i : debthFirstOrder.pre()) {
            System.out.print(" " + i);
        }
        System.out.println();
        System.out.println(" Here are the nodes in post order: ");
        for (int j : debthFirstOrder.post()) {
            System.out.println(" " + j);
        }
        System.out.printf("");
        System.out.println("Here are the nodes in reverse post order: ");
        for (int k : debthFirstOrder.reversePost()) {
            System.out.println(" " + k);
        }
    }
}
