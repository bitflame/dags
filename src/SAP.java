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
    Queue<Integer> fromQueue;
    Queue<Integer> toQueue;
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
            if (this.hasCycle()) return;
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
        for (; y != from && y != to && edgeTo[y] != y; y = edgeTo[y]) {
            hops++;
        }
        return ((x == from && y == to) || (x == to && y == from));
    }

    /* todo write another lockstep, and check to make sure all the nodes with distance 1 from x are used, before using
        nodes with distance 2 and on and on. Use a distance counter, and a conditional that checks to see if the next node
         is further before it moves on. And use the reversePost to give priority to the vertex that is "smaller" in
        topological order. I can only assume that it means it is closer to the sink, but will have to validate this. Also
        use a method like find() to traverse edgeTo and connected to check the ids. Find out why reversePost for digraph3
         is missing some nodes and see what can be done about digraphs with two cycles
         how should I deal with graphs with multiple circuits
         check the reverse order of other graphs and make sure all the nodes are there
         in postorder children return first
         if the ids are the same or find can traverse to both ends from the current node, that node is the ancestor. I have to
         update the id[] in bfs, since dfs() only updates !marked, and this might apply to edgeTo also. I may have to do it again
         in bfs() or just do it in bfs(). Given that I am using the data in bfs, it might be better to do it twice, so I can use
         the original as a cache and if only run bfs, if the ids do not match or something. I think the answer is I have
         to update in a way that I get the result I want. That means id has to be updated in bfs() and I have to review the rest.
         I should try to add the cycle detection to the lockstep-bfs also
         */
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
                            // DistTo[i] = Math.min(DistTo[i], DistTo[v] + 1);
                            // DistTo[i] = DistTo[v] + 1;
                            DistTo[i] = Math.min(DistTo[i], DistTo[v] + 1);
                            edgeTo[i] = v;
                            id[i] = id[v];
                        } else if (id[i] == to) {
                            tempDistance = DistTo[i] + DistTo[v] + 1;
                            if (tempDistance <= currentDistance) {
                                ancestor = i;
                                minDistance = tempDistance;
                                currentDistance = tempDistance;
                            } else {
                                while (!fromQueue.isEmpty()) fromQueue.dequeue();
                                while (!toQueue.isEmpty()) toQueue.dequeue();
                            }
                            DistTo[i] = Math.min(DistTo[i], DistTo[v] + 1);
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
                            // DistTo[i] = DistTo[v] + 1;
                            DistTo[i] = Math.min(DistTo[i], DistTo[v] + 1);
                            edgeTo[i] = v;
                            id[i] = id[v];
                        } else if (id[i] == from) {
                            tempDistance = DistTo[i] + DistTo[v] + 1;
                            if (tempDistance <= currentDistance) {
                                ancestor = i;
                                minDistance = tempDistance;
                                currentDistance = tempDistance;
                            } else {
                                while (!fromQueue.isEmpty()) fromQueue.dequeue();
                                while (!toQueue.isEmpty()) toQueue.dequeue();
                            }
                            DistTo[i] = Math.min(DistTo[i], DistTo[v] + 1);
                            edgeTo[i] = v;
                            id[i] = id[v];
                        }

                    }
                } else if (!hasCycle()) {
                    if (st.get(fromQueue.peek()) < st.get(toQueue.peek())) {
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
                                // DistTo[i] = DistTo[v] + 1;
                                DistTo[i] = Math.min(DistTo[i], DistTo[v] + 1);
                                edgeTo[i] = v;
                                id[i] = id[v];
                            } else if (id[i] == to) {
                                tempDistance = DistTo[i] + DistTo[v] + 1;
                                if (tempDistance <= currentDistance) {
                                    ancestor = i;
                                    minDistance = tempDistance;
                                    currentDistance = tempDistance;
                                } else {
                                    while (!fromQueue.isEmpty()) fromQueue.dequeue();
                                    while (!toQueue.isEmpty()) toQueue.dequeue();
                                }
                                DistTo[i] = Math.min(DistTo[i], DistTo[v] + 1);
                                edgeTo[i] = v;
                                id[i] = id[v];
                            }
                        }
                        nodeDistance = v;
                    } else if (st.get(toQueue.peek()) < st.get(fromQueue.peek())) {
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
                                // DistTo[i] = DistTo[v] + 1;
                                DistTo[i] = Math.min(DistTo[i], DistTo[v] + 1);
                                edgeTo[i] = v;
                                id[i] = id[v];
                            } else if (id[i] == from) {
                                tempDistance = DistTo[i] + DistTo[v] + 1;
                                if (tempDistance <= currentDistance) {
                                    ancestor = i;
                                    minDistance = tempDistance;
                                    currentDistance = tempDistance;
                                } else {
                                    while (!fromQueue.isEmpty()) fromQueue.dequeue();
                                    while (!toQueue.isEmpty()) toQueue.dequeue();
                                }
                                DistTo[i] = Math.min(DistTo[i], DistTo[v] + 1);
                                edgeTo[i] = v;
                                id[i] = id[v];
                            }
                        }
                        nodeDistance = v;
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
                        // DistTo[i] = DistTo[v] + 1;
                        DistTo[i] = Math.min(DistTo[i], DistTo[v] + 1);
                        edgeTo[i] = v;
                        id[i] = id[v];
                    } else if (id[i] == from) {
                        tempDistance = DistTo[i] + DistTo[v] + 1;
                        if (tempDistance <= currentDistance) {
                            ancestor = i;
                            minDistance = tempDistance;
                            currentDistance = tempDistance;
                        } else {
                            while (!fromQueue.isEmpty()) fromQueue.dequeue();
                            while (!toQueue.isEmpty()) toQueue.dequeue();
                        }
                        DistTo[i] = Math.min(DistTo[i], DistTo[v] + 1);
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
                        // DistTo[i] = DistTo[v] + 1;
                        DistTo[i] = Math.min(DistTo[i], DistTo[v] + 1);
                        edgeTo[i] = v;
                        id[i] = id[v];
                    } else if (id[i] == to) {
                        tempDistance = DistTo[i] + DistTo[v] + 1;
                        if (tempDistance <= currentDistance) {
                            ancestor = i;
                            minDistance = tempDistance;
                            currentDistance = tempDistance;
                        } else {
                            while (!fromQueue.isEmpty()) fromQueue.dequeue();
                            while (!toQueue.isEmpty()) toQueue.dequeue();
                        }
                        DistTo[i] = Math.min(DistTo[v] + 1, DistTo[i]);
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
