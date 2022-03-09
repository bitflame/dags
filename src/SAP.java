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
    private int[] edgeTo;
    private int[] DistTo;
    private final int[] id;
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
        for (int i = 0; i < n; i++) {
            id[i] = i;
            edgeTo[i] = i;
        }
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
        if (connected(v, w)) {
            minDistance = DistTo[v] + DistTo[w];
            /* todo -- Need to test everything. I do not recall seeing anything about representing a node that does not
            *   map to any other node vs. a one that does not exist. */
        } else {
            for (int i = 0; i < n; i++) {
                id[i] = i;
                edgeTo[i] = i;
            }
            lockStepBFS(v, w);
        }
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
        if (connected(v, w)) {
            minDistance = DistTo[v] + DistTo[w];
        } else {
            for (int i = 0; i < n; i++) {
                id[i] = i;
                edgeTo[i] = i;
            }
            lockStepBFS(v, w);
        }

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
        while (x != edgeTo[x]) {
            x = edgeTo[x];
        }
        return x;
    }

    private boolean connected(int v, int w) {
        int x = find(v);
        if (x == find(w)) {
            ancestor = x;
            return true;
        }
        return false;
    }

    private void updateDistance(int newNode, int previousNode) {
        while (DistTo[newNode] + 1 < DistTo[previousNode]) {
            DistTo[previousNode] = DistTo[newNode] + 1;
            newNode = previousNode;
            previousNode = edgeTo[previousNode];
        }
    }

    private void lockStepBFS(int f, int t) {
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
                            /* todo -- may have to update id, and DistTo. Also may have to use a stack to detect cycles to prevent updating DistTo for nodes in a cycle */
                            /* todo -- use the inorder() and postorder() Iterables to get a node from each end until you hit a common node */
                        }
                    }
                }
            }
        }
        if (currentDistance == INFINITY) {
            // System.out.println("setting minDistance to -1 becuase currentDistance is
            // INFINITY ");
            minDistance = -1;
            ancestor = -1;
            // return minDistance;
        } else if (currentAncestor == -1) {
            return;
        } else {
            minDistance = currentDistance;
            ancestor = currentAncestor;
            // Once I have an ancestor I can update the id of its edgeTos to its id. I think
        }
        // return currentDistance;
    }

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
        Digraph digraph = new Digraph(new In("digraph3.txt"));
        SAP sap = new SAP(digraph);
        System.out.printf("%b\n", sap.testEdgeTo(11, 12));
        System.out.printf("%b\n", sap.testEdgeTo(11, 8));
    }
}
