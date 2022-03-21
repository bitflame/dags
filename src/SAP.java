import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.In;

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
    private Stack<Integer> cycle;
    private Stack<Integer> reversePost;
    private Queue<Integer> pre;
    private Queue<Integer> postOrder;
    private Queue<Integer> fromQueue;
    private Queue<Integer> toQueue;
    private int[] edgeTo;
    private int[] DistTo;
    private int[] id;
    private int hops = 0;
    private static final int INFINITY = Integer.MAX_VALUE;
    private final boolean print = false;
    private ST<Integer, Integer> st;


    // constructor takes a digraph ( not necessarily a DAG )
    public SAP(Digraph digraph) {
        if (digraph == null)
            throw new IllegalArgumentException("Digraph value can not be null");
        digraphDFCopy = new Digraph(digraph);
        n = digraphDFCopy.V();
        onStack = new boolean[n];
        // DistTo = new int[n];
        edgeTo = new int[n];
        id = new int[n];
        marked = new boolean[n];
        //pre = new Queue<>();
        reversePost = new Stack<>();
        //postOrder = new Queue<>();
        for (int i = 0; i < n; i++) {
            id[i] = i;
            edgeTo[i] = i;
        }
        for (int i = 0; i < n; i++) {
            if (!marked[i]) dfs(digraphDFCopy, i);
        }
        reversePostOrder();
    }

    private void dfs(Digraph digraphDFCopy, int v) {
        marked[v] = true;
        onStack[v] = true;
        // pre.enqueue(v);
        for (int w : digraphDFCopy.adj(v)) {
            //if (this.hasCycle()) return;
            if (!marked[w]) {
                id[w] = id[v];
                edgeTo[w] = v;
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
        // postOrder.enqueue(v);
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
            st.put(reversePost.pop(), counter);
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
        int currentAncestor = -1;
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

    /* y is x's current parent. The one that is supposed to be updated edgeTo but is not so we don't lose track of the
     * previous edgeTo */
    private boolean checkEdgeTo(int x, int y) {
        // System.out.printf("************checkEdge activated for %d %d ********************\n.", from, to);
        hops = 0;
        // x should have a path to one end and its parent to the other end
        for (; x != from && x != to; x = edgeTo[x]) {
            hops++;
        }
        hops++;
        // now check x's parent to make sure it can get  to the other end
        for (; y != from && y != to; y = edgeTo[y]) {
            hops++;
        }
        return ((x == from && y == to) || (x == to && y == from));
    }

    private void lockStepBFS() {
        marked = new boolean[n];
        edgeTo = new int[n];
        id = new int[n];
        for (int i = 0; i < n; i++) {
            id[i] = i;
            edgeTo[i] = i;
        }
        DistTo = new int[n];
        fromQueue = new Queue<>();
        toQueue = new Queue<>();
        marked[from] = true;
        marked[to] = true;
        if (st.get(from) > st.get(to)) {
            int temp = from;
            from = to;
            to = temp;
        }
        fromQueue.enqueue(from);
        toQueue.enqueue(to);
        DistTo[from] = 0;
        DistTo[to] = 0;
        int nodeDistance = 1;
        int v = 0;
        int currentDistance = INFINITY;
        int tempDistance = 0;
        while (!fromQueue.isEmpty() || !toQueue.isEmpty()) {
            // take from the one with less distance
            if (!fromQueue.isEmpty() && !toQueue.isEmpty()) {
                if (DistTo[fromQueue.peek()] < DistTo[toQueue.peek()] && DistTo[fromQueue.peek()] <= nodeDistance) {
                    v = fromQueue.dequeue();
                    for (int i : digraphDFCopy.adj(v)) {
                        if (!marked[i]) {
                            marked[i] = true;
                            fromQueue.enqueue(i);
                            DistTo[i] = DistTo[v] + 1;
                            edgeTo[i] = v;
                            id[i] = id[v];
                        } else if (checkEdgeTo(i, v)) {
                            // you found an ancestor
                            tempDistance = DistTo[i] + DistTo[v] + 1;
                            if (tempDistance < currentDistance) {
                                ancestor = i;
                                currentDistance = tempDistance;
                                minDistance = tempDistance;
                            } else {
                                while (!fromQueue.isEmpty()) fromQueue.dequeue();
                                while (!toQueue.isEmpty()) toQueue.dequeue();
                            }
                        } else if (id[i] == to) {
                            tempDistance = DistTo[i] + DistTo[v] + 1;
                            // System.out.printf("Might be in a cycle for %d, %d\n",from, to);
                            if (tempDistance <= currentDistance) {
                                // System.out.printf("inside id[i]==to for %d, %d\n", from, to);
                                ancestor = i;
                                minDistance = tempDistance;
                                currentDistance = tempDistance;
                            } else {
                                while (!fromQueue.isEmpty()) fromQueue.dequeue();
                                while (!toQueue.isEmpty()) toQueue.dequeue();
                            }
                        }
                        if (DistTo[v] + 1 <= DistTo[i]) {
                            DistTo[i] = DistTo[v] + 1;
                            edgeTo[i] = v;
                            id[i] = id[v];
                        }
                    }
                } else if (DistTo[toQueue.peek()] < DistTo[fromQueue.peek()] && DistTo[toQueue.peek()] <= nodeDistance) {
                    v = toQueue.dequeue();
                    for (int i : digraphDFCopy.adj(v)) {
                        if (!marked[i]) {
                            marked[i] = true;
                            toQueue.enqueue(i);
                            DistTo[i] = DistTo[v] + 1;
                            edgeTo[i] = v;
                            id[i] = id[v];
                        } else if (checkEdgeTo(i, v)) {
                            // you found an ancestor
                            tempDistance = DistTo[i] + DistTo[v] + 1;
                            if (tempDistance < currentDistance) {
                                ancestor = i;
                                currentDistance = tempDistance;
                                minDistance = tempDistance;
                            } else {
                                while (!fromQueue.isEmpty()) fromQueue.dequeue();
                                while (!toQueue.isEmpty()) toQueue.dequeue();
                            }
                        } else if (id[i] == from) {
                            tempDistance = DistTo[i] + DistTo[v] + 1;
                            // System.out.printf("Might be in a cycle for %d, %d\n",from, to);
                            if (tempDistance <= currentDistance) {
                                // System.out.printf("inside id[i]==from for %d, %d\n", from, to);
                                ancestor = i;
                                minDistance = tempDistance;
                                currentDistance = tempDistance;
                            } else {
                                while (!fromQueue.isEmpty()) fromQueue.dequeue();
                                while (!toQueue.isEmpty()) toQueue.dequeue();
                            }
                        }
                        if (DistTo[v] + 1 <= DistTo[i]) {
                            DistTo[i] = DistTo[v] + 1;
                            edgeTo[i] = v;
                            id[i] = id[v];
                        }
                    }
                }
            }
            if (!toQueue.isEmpty() && DistTo[toQueue.peek()] <= nodeDistance) {
                // if the nodes in toQueue and fromQueue are equal in all the above conditions, just take one
                v = toQueue.dequeue();
                for (int i : digraphDFCopy.adj(v)) {
                    if (!marked[i]) {
                        marked[i] = true;
                        toQueue.enqueue(i);
                        DistTo[i] = DistTo[v] + 1;
                        edgeTo[i] = v;
                        id[i] = id[v];
                    } else if (checkEdgeTo(i, v)) {
                        // you found an ancestor
                        tempDistance = DistTo[i] + DistTo[v] + 1;
                        if (tempDistance < currentDistance) {
                            ancestor = i;
                            currentDistance = tempDistance;
                            minDistance = DistTo[i] + DistTo[v] + 1;
                        } else {
                            while (!fromQueue.isEmpty()) fromQueue.dequeue();
                            while (!toQueue.isEmpty()) toQueue.dequeue();
                        }
                    } else if (id[i] == from) {
                        tempDistance = DistTo[i] + DistTo[v] + 1;
                        // System.out.printf("Might be in a cycle for %d, %d\n",from, to);
                        if (tempDistance <= currentDistance) {
                            // System.out.printf("inside id[i]==from for %d, %d\n", from, to);
                            ancestor = i;
                            minDistance = tempDistance;
                            currentDistance = tempDistance;
                        } else {
                            while (!fromQueue.isEmpty()) fromQueue.dequeue();
                            while (!toQueue.isEmpty()) toQueue.dequeue();
                        }
                    }
                    if (DistTo[v] + 1 <= DistTo[i]) {
                        DistTo[i] = DistTo[v] + 1;
                        edgeTo[i] = v;
                        id[i] = id[v];
                    }
                }
            }
            if (!fromQueue.isEmpty() && DistTo[fromQueue.peek()] <= nodeDistance) {
                v = fromQueue.dequeue();
                for (int i : digraphDFCopy.adj(v)) {
                    if (!marked[i]) {
                        marked[i] = true;
                        fromQueue.enqueue(i);
                        DistTo[i] = DistTo[v] + 1;
                        edgeTo[i] = v;
                        id[i] = id[v];

                    } else if (checkEdgeTo(i, v)) {
                        // you found an ancestor - when there is a cycle the real distance is
                        tempDistance = DistTo[i] + DistTo[v] + 1;
                        if (tempDistance < currentDistance) {
                            ancestor = i;
                            currentDistance = tempDistance;
                            minDistance = tempDistance;
                        } else {
                            while (!fromQueue.isEmpty()) fromQueue.dequeue();
                            while (!toQueue.isEmpty()) toQueue.dequeue();
                        }
                    } else if (id[i] == to) {
                        tempDistance = DistTo[i] + DistTo[v] + 1;
                        // System.out.printf("Might be in a cycle for %d, %d\n",from, to);
                        if (tempDistance <= currentDistance) {
                            // System.out.printf("inside id[i]==to for %d %d\n", from, to);
                            ancestor = i;
                            minDistance = tempDistance;
                            currentDistance = tempDistance;
                        } else {
                            while (!fromQueue.isEmpty()) fromQueue.dequeue();
                            while (!toQueue.isEmpty()) toQueue.dequeue();
                        }
                    }
                    if (DistTo[v] + 1 <= DistTo[i]) {
                        DistTo[i] = DistTo[v] + 1;
                        edgeTo[i] = v;
                        id[i] = id[v];
                    }
                }
            }
            nodeDistance++;
        }
        if (currentDistance == INFINITY) {
            minDistance = -1;
            ancestor = -1;
        }
    }

    public static void main(String[] args) {

        System.out.printf("****************************************Testing digraph3 \n");
        Digraph digraph = new Digraph(new In("digraph3.txt"));
        SAP sap = new SAP(digraph);
        int minDist = sap.length(13, 14);
        if (minDist != 1) System.out.printf("Test 1 - (13, 14) expecting 1, getting: %d\n", minDist);
        else System.out.printf("Test 1 passed.\n");
        System.out.printf("Expected ancestor: 14. Actual ancestor: %d\n", sap.ancestor(13, 14));
        System.out.printf("Test 2 - (14, 13) expecting 1, getting: %d\n", sap.length(14, 13));
        System.out.printf("Expected ancestor: 14. Actual ancestor: %d\n", sap.ancestor(14, 13));
        System.out.printf("Test 3 - (13, 0) expecting 2, getting: %d\n", sap.length(13, 0));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(13, 0));
        System.out.printf("Test 4 - (0, 13) expecting 2, getting: %d\n", sap.length(0, 13));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(0, 13));
        System.out.printf("Test 5 - (14, 0) expecting 1, getting: %d\n", sap.length(14, 0));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(14, 0));
        System.out.printf("Test 6 - (0, 14) expecting 1, getting: %d\n", sap.length(0, 14));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(0, 14));
        System.out.printf("Test 7 - (13, 11) expecting 3, getting: %d\n", sap.length(13, 11));
        System.out.printf("Expected ancestor: 11. Actual ancestor: %d\n", sap.ancestor(13, 11));
        System.out.printf("Test 8 - (11, 13) expecting 3, getting: %d\n", sap.length(11, 13));
        System.out.printf("Expected ancestor: 11. Actual ancestor: %d\n", sap.ancestor(11, 13));
        System.out.printf("Test 9 - (14, 11) expecting 2, getting: %d\n", sap.length(14, 11));
        System.out.printf("Expected ancestor: 11. Actual ancestor: %d\n", sap.ancestor(14, 11));
        System.out.printf("Test10 - (11, 14) expecting 2, getting: %d\n", sap.length(11, 14));
        System.out.printf("Expected ancestor: 11. Actual ancestor: %d\n", sap.ancestor(11, 14));
        System.out.printf("Test11 - (14, 12) expecting 3, getting: %d\n", sap.length(14, 12));
        System.out.printf("Expected ancestor: 12. Actual ancestor: %d\n", sap.ancestor(14, 12));
        System.out.printf("Test12 - (12, 14) expecting 3, getting: %d\n", sap.length(12, 14));
        System.out.printf("Expected ancestor: 12. Actual ancestor: %d\n", sap.ancestor(12, 14));
        System.out.printf("Test13 - (14, 10) expecting 3, getting: %d\n", sap.length(14, 10));
        System.out.printf("Expected ancestor: 11. Actual ancestor: %d\n", sap.ancestor(14, 10));
        System.out.printf("Test14 - (10, 14) expecting 3, getting: %d\n", sap.length(10, 14));
        System.out.printf("Expected ancestor: 11. Actual ancestor: %d\n", sap.ancestor(10, 14));
        System.out.printf("Test15 - (14, 9) expecting 4, getting: %d\n", sap.length(14, 9));
        System.out.printf("Expected ancestor: 11. Actual ancestor: %d\n", sap.ancestor(14, 9));
        System.out.printf("Test16 - (9, 14) expecting 4, getting: %d\n", sap.length(9, 14));
        System.out.printf("Expected ancestor: 11. Actual ancestor: %d\n", sap.ancestor(9, 14));
        System.out.printf("Test17 - (13, 8) expecting 5, getting: %d\n", sap.length(13, 8));
        System.out.printf("Expected ancestor: 8. Actual ancestor: %d\n", sap.ancestor(13, 8));
        System.out.printf("Test18 - (8, 13) expecting 5, getting: %d\n", sap.length(8, 13));
        System.out.printf("Expected ancestor: 8. Actual ancestor: %d\n", sap.ancestor(8, 13));
        System.out.printf("Test19 - (14, 8) expecting 4, getting: %d\n", sap.length(14, 8));
        System.out.printf("Expected ancestor: 8. Actual ancestor: %d\n", sap.ancestor(14, 8));
        System.out.printf("Test20 - (8, 14) expecting 4, getting: %d\n", sap.length(8, 14));
        System.out.printf("Expected ancestor: 8. Actual ancestor: %d\n", sap.ancestor(8, 14));
        System.out.printf("Test21 - (7, 13) expecting 6, getting: %d\n", sap.length(7, 13));
        System.out.printf("Expected ancestor: 8. Actual ancestor: %d\n", sap.ancestor(7, 13));
        System.out.printf("Test22 - (13, 7) expecting 6, getting: %d\n", sap.length(13, 7));
        System.out.printf("Expected ancestor: 8. Actual ancestor: %d\n", sap.ancestor(13, 7));
        System.out.printf("Test23 - (1, 2) expecting 1, getting: %d\n", sap.length(1, 2));
        System.out.printf("Expected ancestor: 2. Actual ancestor: %d\n", sap.ancestor(1, 2));
        System.out.printf("Test24 - (1, 13) expecting -1, getting: %d\n", sap.length(1, 13));
        System.out.printf("Expected ancestor: -1. Actual ancestor: %d\n", sap.ancestor(1, 13));
        System.out.printf("Test25 - (9, 13) expecting 5, getting: %d\n", sap.length(9, 13));
        System.out.printf("Expected ancestor: 11. Actual ancestor: %d\n", sap.ancestor(9, 13));
        System.out.printf("Test26 - (13, 9) expecting 5, getting: %d\n", sap.length(13, 9));
        System.out.printf("Expected ancestor: 11. Actual ancestor: %d\n", sap.ancestor(13, 9));
        System.out.printf("Test27 - (8, 14) expecting 4, getting: %d\n", sap.length(8, 14));
        System.out.printf("Expected ancestor: 11. Actual ancestor: %d\n", sap.ancestor(8, 14));
        System.out.printf("Test28 - (14, 8) expecting 4, getting: %d\n", sap.length(14, 8));
        System.out.printf("Expected ancestor: 11. Actual ancestor: %d\n", sap.ancestor(14, 8));

        System.out.printf("****************************************Testing digraph1 \n");
        digraph = new Digraph(new In("digraph1.txt"));
        sap = new SAP(digraph);
        System.out.printf("Test 1 - (0, 2) expecting 1, getting: %d\n", sap.length(0, 2));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(0, 2));
        System.out.printf("Test 2 - (2, 0) expecting 1, getting: %d\n", sap.length(2, 0));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(2, 0));
        System.out.printf("Test 3 - (0, 1) expecting 1, getting: %d\n", sap.length(0, 1));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(0, 1));
        System.out.printf("Test 4 - (1, 0) expecting 1, getting: %d\n", sap.length(1, 0));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(1, 0));
        System.out.printf("Test 5 - (1, 2) expecting 2, getting: %d\n", sap.length(1, 2));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(1, 2));
        System.out.printf("Test 6 - (2, 1) expecting 2, getting: %d\n", sap.length(2, 1));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(2, 1));
        System.out.printf("Test 7 - (4, 0) expecting 2, getting: %d\n", sap.length(4, 0));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(4, 0));
        System.out.printf("Test 8 - (0, 4) expecting 2, getting: %d\n", sap.length(0, 4));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(0, 4));
        System.out.printf("Test 9 - (4, 2) expecting 3, getting: %d\n", sap.length(4, 2));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(4, 2));
        System.out.printf("Test 10 - (2, 4) expecting 3, getting: %d\n", sap.length(2, 4));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(2, 4));
        System.out.printf("Test 11 - (3, 5) expecting 2, getting: %d\n", sap.length(3, 5));
        System.out.printf("Expected ancestor: 1. Actual ancestor: %d\n", sap.ancestor(3, 5));
        System.out.printf("Test 12 - (5, 3) expecting 2, getting: %d\n", sap.length(5, 3));
        System.out.printf("Expected ancestor: 1. Actual ancestor: %d\n", sap.ancestor(5, 3));
        System.out.printf("Test 13 - (7, 11) expecting 5, getting: %d\n", sap.length(7, 11));
        System.out.printf("Expected ancestor: 1. Actual ancestor: %d\n", sap.ancestor(7, 11));
        System.out.printf("Test 14 - (11, 7) expecting 5, getting: %d\n", sap.length(11, 7));
        System.out.printf("Expected ancestor: 1. Actual ancestor: %d\n", sap.ancestor(11, 7));
        System.out.printf("Test 15 - (12, 4) expecting 4, getting: %d\n", sap.length(12, 4));
        System.out.printf("Expected ancestor: 1. Actual ancestor: %d\n", sap.ancestor(12, 4));
        System.out.printf("Test 16 - (12, 4) expecting 4, getting: %d\n", sap.length(12, 4));
        System.out.printf("Expected ancestor: 1. Actual ancestor: %d\n", sap.ancestor(12, 4));
        System.out.printf("Test 17 - (9, 1) expecting 2, getting: %d\n", sap.length(9, 1));
        System.out.printf("Expected ancestor: 1. Actual ancestor: %d\n", sap.ancestor(9, 1));
        System.out.printf("Test 18 - (1, 9) expecting 2, getting: %d\n", sap.length(1, 9));
        System.out.printf("Expected ancestor: 1. Actual ancestor: %d\n", sap.ancestor(1, 9));
        System.out.printf("Test 19 - (12, 0) expecting 4, getting: %d\n", sap.length(12, 0));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(12, 0));
        System.out.printf("Test 20 - (0, 12) expecting 4, getting: %d\n", sap.length(0, 12));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(0, 12));
        System.out.printf("****************************************Testing digraph2 \n");
        digraph = new Digraph(new In("digraph2.txt"));
        sap = new SAP(digraph);
        System.out.printf("Test 1 - (1, 0) expecting 1, getting: %d\n", sap.length(1, 0));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(1, 0));
        System.out.printf("Test 2 - (0, 1) expecting 1, getting: %d\n", sap.length(0, 1));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(0, 1));
        System.out.printf("Test 3 - (5, 0) expecting 1, getting: %d\n", sap.length(5, 0));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(5, 0));
        System.out.printf("Test 4 - (0, 5) expecting 1, getting: %d\n", sap.length(0, 5));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(0, 5));
        System.out.printf("Test 5 - (5, 1) expecting 2, getting: %d\n", sap.length(5, 1));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(5, 1));
        System.out.printf("Test 6 - (1, 5) expecting 2, getting: %d\n", sap.length(1, 5));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(1, 5));
        System.out.printf("Test 7 - (4, 0) expecting 2, getting: %d\n", sap.length(4, 0));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(4, 0));
        System.out.printf("Test 8 - (0, 4) expecting 2, getting: %d\n", sap.length(0, 4));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(0, 4));
        System.out.printf("Test 9 - (4, 1) expecting 3, getting: %d\n", sap.length(4, 1));
        System.out.printf("Expected ancestor: 4. Actual ancestor: %d\n", sap.ancestor(4, 1));
        System.out.printf("Test 10 - (2, 0) expecting 4, getting: %d\n", sap.length(2, 0));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(2, 0));
        System.out.printf("Test 11 - (0, 2) expecting 4, getting: %d\n", sap.length(0, 2));
        System.out.printf("Expected ancestor: 0. Actual ancestor: %d\n", sap.ancestor(0, 2));
    }
}