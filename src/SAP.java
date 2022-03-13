import edu.princeton.cs.algs4.*;

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
    private int[] id;
    private int count = 0;
    private int hops = 0;
    private static final int INFINITY = Integer.MAX_VALUE;
    private final boolean print = false;
    ST<Integer, Integer> st;

    // constructor takes a digraph ( not necessarily a DAG )
    public SAP(Digraph digraph) {
        if (digraph == null)
            throw new IllegalArgumentException("Digraph value can not be null");
        digraphDFCopy = new Digraph(digraph);
        n = digraphDFCopy.V();
        onStack = new boolean[n];
        DistTo = new int[n];
        edgeTo = new int[n];
        id = new int[n];
        marked = new boolean[n];
        for (int i = 0; i < n; i++) {
            edgeTo[i] = i;
            id[i] = i;
        }
        //pre = new Queue<>();
        reversePost = new Stack<>();
        //postOrder = new Queue<>();
        for (int i = 0; i < n; i++) {
            if (!marked[i]) dfs(digraphDFCopy, i);
        }
        System.out.println("Hi");
    }

    private void dfs(Digraph digraphDFCopy, int v) {
        marked[v] = true;
        onStack[v] = true;
        //pre.enqueue(v);
        for (int w : digraphDFCopy.adj(v)) {
            edgeTo[w] = v;
            id[w] = id[v];
            DistTo[w] = DistTo[v] + 1;
            if (this.hasCycle()) return;
            else if (!marked[w]) {
                dfs(digraphDFCopy, w);
            } else if (onStack[w]) {
                cycle = new Stack<>();
                for (int x = v; x != w; x = edgeTo[x]) {
                    cycle.push(x);
                }
                cycle.push(w);
                cycle.push(v);
            }
        }
        onStack[v] = false;
        reversePost.push(v);
        //postOrder.enqueue(v);
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
        st = new ST<>();
        int counter = n;
        while (!reversePost.isEmpty() && counter >= 0) {
            st.put(reversePost.pop(), n);
            counter--;
        }
        return st.keys();
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
        lockStepBFS();
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
        lockStepBFS();
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

    /* todo write another lockstep, and check to make sure all the nodes with distance 1 from x are used, before using
        nodes with distance 2 and on and on. Use a distance counter, and a conditional that checks to see if the next node
         is further before it moves on. And use the reversePost to give priority to the vertex that is "smaller" in
        topological order. I can only assume that it means it is closer to the sink, but will have to validate this. Also
        use a method like find() to traverse edgeTo and connected to check the ids. Find out why reversePost for digraph3
         is missing some nodes and see what can be done about digraphs with two cycles */
    private void lockStepBFS() {
        marked = new boolean[n];
        Queue<Integer> fromQueue = new Queue<>();
        Queue<Integer> toQueue = new Queue<>();
        marked[from] = true;
        marked[to] = true;
        // fromQueue.enqueue(from);
        // toQueue.enqueue(to);
        DistTo[from] = 0;
        DistTo[to] = 0;
        int nodeDistance = 0;
        fromQueue.enqueue(2);
        fromQueue.enqueue(3);
        toQueue.enqueue(0);
        toQueue.enqueue(5);
        int v = 0;
        while (!fromQueue.isEmpty() && !toQueue.isEmpty()) {
            v = st.get(fromQueue.peek()) < st.get(toQueue.peek()) ? fromQueue.dequeue() : toQueue.dequeue();
            System.out.print("should print 05"+v);
        }

    }
    /*private void lockStepBFS(int f, int t) {
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
    }*/

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
        sap.reversePostOrder();
        sap.lockStepBFS();
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
