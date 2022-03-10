import edu.princeton.cs.algs4.Queue;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;

import java.util.Iterator;

public class SAP {
    private final Digraph digraphDFCopy;
    private int ancestor;
    private int minDistance;
    private int from;
    private int to;
    private final int n;
    private boolean[] marked;
    private boolean[] onStack;
    Stack<Integer> cycle;
    Stack<Integer> reversePost;
    Queue<Integer> pre;
    Queue<Integer> postOrder;
    Queue<Integer> bfsQueue = new Queue<>();
    private int[] edgeTo;
    private int[] DistTo;
    private final int[] id;
    private int count = 0;
    private int hops = 0;
    private static final int INFINITY = Integer.MAX_VALUE;
    private final boolean print = false;

    // constructor takes a digraph ( not necessarily a DAG )
    public SAP(Digraph digraph) {
        if (digraph == null)
            throw new IllegalArgumentException("Digraph value can not be null");
        digraphDFCopy = new Digraph(digraph);
        n = digraphDFCopy.V();
        id = new int[n];
        edgeTo = new int[n];
        pre = new Queue<Integer>();
        DistTo = new int[n];
        onStack = new boolean[n];
        marked = new boolean[n];
        reversePost = new Stack<Integer>();
        postOrder = new Queue<Integer>();
        for (int i = 0; i < n; i++) {
            if (!marked[i]) dfs(digraphDFCopy, i);
        }
    }

    private void dfs(Digraph digraphDFCopy, int v) {
        marked[v] = true;
        onStack[v] = true;
        pre.enqueue(v);
        for (int w : digraphDFCopy.adj(v)) {
            if (this.hasCycle()) return;
            else if (!marked[w]) {
                edgeTo[w] = v;
                id[w] = v;
                dfs(digraphDFCopy, w);
            } else if (onStack[w]) {
                cycle = new Stack<Integer>();
                for (int x = v; x != w; x = edgeTo[x]) {
                    cycle.push(x);
                }
                cycle.push(w);
                cycle.push(v);
            }
        }
        onStack[v] = false;
        reversePost.push(v);
        postOrder.enqueue(v);
    }


    private boolean stronglyConnected(int v, int w) {
        return id[v] == id[w];
    }

    private Iterable<Integer> cycle() {
        return cycle;
    }

    private Iterable<Integer> preOrder() {
        return pre;
    }

    private Iterable<Integer> reversePostOrder() {
        Queue<Integer> temp = new Queue<>();
        while (!reversePost.isEmpty()) {
            temp.enqueue(reversePost.pop());
        }
        return temp;
    }

    private Iterable<Integer> postOrder() {
        return postOrder;
    }

    private boolean hasCycle() {
        return cycle != null;
    }

    // length of the shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        // System.out.println("length(): Calculating the distance between : " + v + " "
        // + w);
        if (v < 0 || v >= digraphDFCopy.V())
            throw new IllegalArgumentException("The node ids should be within acceptable range.\n");
        if (w < 0 || w >= digraphDFCopy.V())
            throw new IllegalArgumentException("The node ids should be within acceptable range.\n");
        if (((this.from == v && this.to == w) || (this.to == v && this.from == w)) && v != w)
            return minDistance;
        from = v;
        to = w;
        if (v == w) {
            ancestor = v;
            minDistance = 0;
            return 0;
        }
        if ((digraphDFCopy.indegree(from) == 0 && digraphDFCopy.outdegree(from) == 0)
                || (digraphDFCopy.indegree(to) == 0 &&
                digraphDFCopy.outdegree(to) == 0)) {
            ancestor = -1;
            return minDistance = -1;
        }
        lockStepBFS(from, to);
        return minDistance;
    }

    // length of the shortest ancestral path between any vertex in v and any vertex
    // in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException("Iterable value to SAP.length() can not be null.\n");
        int currentDistance = 0;
        int prevDistance = INFINITY;
        // System.out.printf("sap triggers ancestor() with iterables ");
        Iterator<Integer> i = v.iterator();
        Iterator<Integer> j = w.iterator();
        if ((!i.hasNext()) || (!j.hasNext())) {
            return minDistance = -1;
        }
        int source;
        int destination;
        Object obj;
        while (i.hasNext()) {
            obj = i.next();
            if (obj == null)
                throw new IllegalArgumentException("The values Iterables give to length() can not be null.");
            else
                source = (Integer) obj;
            while (j.hasNext()) {
                obj = j.next();
                if (obj == null)
                    throw new IllegalArgumentException("The values Iterables give to length() can not be null.");
                destination = (Integer) obj;
                currentDistance = length(source, destination);
                // System.out.printf("Current Distance: %d \n", currentDistance);
                if (currentDistance != -1 && currentDistance < prevDistance) {
                    prevDistance = currentDistance;
                }
            }
            j = w.iterator();
        }
        // System.out.printf("Here is the last value in previous distance: %d\n" ,
        // prevDistance);
        if (prevDistance != INFINITY)
            minDistance = prevDistance;
        return minDistance;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path;
    // -1 if no such path
    public int ancestor(int v, int w) {
        // System.out.println("Calculating the ancestor between : " + v + " " + w);
        if (v < 0 || v >= digraphDFCopy.V())
            throw new IllegalArgumentException("The node ids should be within acceptable range.\n");
        if (w < 0 || w >= digraphDFCopy.V())
            throw new IllegalArgumentException("The node ids should be within acceptable range.\n");
        if (v < 0 || w < 0)
            throw new IllegalArgumentException("The node ids should be within acceptable range.\n");
        if (((this.from == v && this.to == w) || (this.to == v && this.from == w)) && v != w)
            return ancestor;
        from = v;
        to = w;
        if (v == w) {
            return ancestor = v;
        }
        if ((digraphDFCopy.indegree(from) == 0 && digraphDFCopy.outdegree(from) == 0)
                || (digraphDFCopy.indegree(to) == 0 &&
                digraphDFCopy.outdegree(to) == 0)) {
            minDistance = -1;
            return ancestor = -1;
        }
        lockStepBFS(v, w);
        return ancestor;
    }

    // a common ancestor that participates in the shortest ancestral path; -1 if no
    // such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException("Iterable value to SAP.ancestor() can not be null.\n");

        int len = 0;
        int prevLen = INFINITY;
        int currentAncestor = 0;
        Iterator<Integer> i = v.iterator();
        Iterator<Integer> j = w.iterator();
        if ((!i.hasNext()) || (!j.hasNext())) {
            return ancestor = -1;
        }
        int source;
        int destination;
        Object obj;
        while (i.hasNext()) {
            obj = i.next();
            if (obj == null)
                throw new IllegalArgumentException("The values Iterables give to length() can not be null.");
            else
                source = (Integer) obj;
            while (j.hasNext()) {
                obj = j.next();
                if (obj == null)
                    throw new IllegalArgumentException("The values Iterables give to length() can not be null.");
                destination = (Integer) obj;
                len = length(source, destination);
                if (len != -1 && len < prevLen) {
                    currentAncestor = ancestor;
                    prevLen = len;
                }
            }
            j = w.iterator();
        }
        ancestor = currentAncestor;
        minDistance = prevLen;
        return ancestor;
    }

    private int find(int x) {
        hops = 0;
        while (x != edgeTo[x]) {
            x = edgeTo[x];
            hops++;
        }
        return x;
    }

    private void updateDistance(int newNode, int previousNode) {
        while (DistTo[newNode] + 1 < DistTo[previousNode]) {
            DistTo[previousNode] = DistTo[newNode] + 1;
            newNode = previousNode;
            previousNode = edgeTo[previousNode];
        }
    }

    private void lockStepBFS(int f, int t) {
        int currentDistance = INFINITY;
        marked = new boolean[n];
        bfsQueue = new Queue<>();
        marked[f] = true;
        marked[t] = true;
        DistTo[f] = 0;
        DistTo[t] = 0;
        bfsQueue.enqueue(f);
        bfsQueue.enqueue(t);
        while (!bfsQueue.isEmpty()) {
            int v = bfsQueue.dequeue();
            for (int j : digraphDFCopy.adj(v)) {
                if (!marked[j]) {
                    DistTo[j] = DistTo[v] + 1;
                    edgeTo[j] = v;
                    marked[j] = true;
                    bfsQueue.enqueue(j);
                } else if (DistTo[j] + DistTo[v] + 1 < currentDistance) {
                    /* if j is marked, it is likely to be an ancestor, and I can double-check  by using stronglyConnected()
                     * and the rest of the data structures like preorder, postorder, and reversePostorder. If I set ids during
                     * dfs, then I can check to see if a marked node has a different id, and that means it is from the other
                     * end and an ancestor */

                    ancestor = j;
                    currentDistance = DistTo[j] + DistTo[v] + 1;
                    minDistance = currentDistance;
                } else return;
            }
        }
    }

    /* private void lockStepBFS(int f, int t) {
        marked = new boolean[n];
        Queue<Integer> fromQueue = new Queue<>();
        Queue<Integer> toQueue = new Queue<>();
        fromQueue.enqueue(f);
        toQueue.enqueue(t);
        marked[f] = true;
        marked[t] = true;
        DistTo[f] = 0;
        DistTo[t] = 0;
        int currentDistance = INFINITY;
        int currentAncestor = -1;
        int temp = 0;
        while (!(fromQueue.isEmpty() && toQueue.isEmpty())) {
            if (!fromQueue.isEmpty()) {
                int v = fromQueue.dequeue();
                if (print)
                    System.out.printf("took %d from fromQueue \n", v);
                for (int j : digraphDFCopy.adj(v)) {
                    if (!marked[j]) {
                        marked[j] = true;
                        DistTo[j] = DistTo[v] + 1;
                        id[j] = id[v];
                        edgeTo[j] = v;
                        fromQueue.enqueue(j);
                    } else {
                        if (connected(f, t)) {
                            currentDistance = DistTo[j] + DistTo[v] + 1;
                            currentAncestor = j;
                        } else {
                            edgeTo[j] = v;
                        }
                    }
                }
            }
            if (!toQueue.isEmpty()) {
                int w = toQueue.dequeue();
                if (print)
                    System.out.printf("took %d from toQueue \n", w);
                for (int k : digraphDFCopy.adj(w)) {
                    if (!marked[k]) {
                        marked[k] = true;
                        DistTo[k] = DistTo[w] + 1;
                        id[k] = id[w];
                        edgeTo[k] = w;
                        toQueue.enqueue(k);
                    } else {
                        if (connected(f, t)) {
                            currentAncestor = k;
                            currentDistance = DistTo[k] + DistTo[w] + 1;
                        } else {
                            edgeTo[k] = w;
                        }
                    }
                }
            }
        }
        if (currentDistance == INFINITY) {
            minDistance = -1;
            ancestor = -1;
        } else if (currentAncestor == -1) {
            return;
        } else {
            minDistance = currentDistance;
            ancestor = currentAncestor;
        }
    } */

    private boolean testEdgeTo(int ancestor, int destination) {
        // System.out.printf("inside testEdge for " + from + " and " + to);
        if (ancestor == destination)
            return true;
        int i = ancestor;
        int counter = 0;
        for (; i != destination && counter < n; i = edgeTo[i]) {
            if (i == -1)
                break;
            counter++;
            // System.out.print(" " + i);
        }
        // if (i != -1) System.out.print(" " + i);
        return (i == destination);
    }

    public static void main(String[] args) {
        Digraph digraph = new Digraph(new In("tinyDG.txt"));
        SAP sap = new SAP(digraph);
        // System.out.println(sap.ancestor(1, 2));
        /* The following code will print the preorder, postorder,
        and reverse postorder of any digraph's nodes*/
        System.out.println("Here is nodes in preorder: ");
        for (int i : sap.preOrder()) {
            System.out.print(" " + i);
        }
        System.out.println();
        System.out.println(" Here are the nodes in post order: ");
        for (int j : sap.postOrder) {
            System.out.println(" " + j);
        }
        System.out.printf("");
        System.out.println("Here are the nodes in reverse post order: ");
        for (int k : sap.reversePost) {
            System.out.println(" " + k);
        }
        System.out.println("Here are the nodes in the cycle: ");
        if (sap.hasCycle())
            for (int m : sap.cycle()) {
                System.out.println(" " + m);
            }
    }
}
