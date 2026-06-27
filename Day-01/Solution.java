import java.util.*;

public class Solution {

    static int n;
    static ArrayList<Integer>[] tree;

    static int[] parent, depth, heavy, head, pos, size;
    static int curPos;

    static SegmentTree seg;

    // DFS to compute subtree sizes and heavy child
    static int dfs(int v, int p) {
        parent[v] = p;
        size[v] = 1;
        int maxSize = 0;

        for (int u : tree[v]) {
            if (u == p) continue;
            depth[u] = depth[v] + 1;
            dfs(u, v);
            size[v] += size[u];
            if (size[u] > maxSize) {
                maxSize = size[u];
                heavy[v] = u;
            }
        }
        return size[v];
    }

    // Decompose tree
    static void decompose(int v, int h) {
        head[v] = h;
        pos[v] = curPos++;

        if (heavy[v] != -1)
            decompose(heavy[v], h);

        for (int u : tree[v]) {
            if (u != parent[v] && u != heavy[v])
                decompose(u, u);
        }
    }

    static class SegmentTree {
        int n;
        int[] tree;
        boolean[] lazy;

        SegmentTree(int n) {
            this.n = n;
            tree = new int[4 * n];
            lazy = new boolean[4 * n];
        }

        void push(int node, int l, int r) {
            if (!lazy[node]) return;

            tree[node] = (r - l + 1) - tree[node];

            if (l != r) {
                lazy[node * 2] ^= true;
                lazy[node * 2 + 1] ^= true;
            }

            lazy[node] = false;
        }

        void update(int node, int l, int r, int ql, int qr) {
            push(node, l, r);

            if (ql > r || qr < l) return;

            if (ql <= l && r <= qr) {
                lazy[node] ^= true;
                push(node, l, r);
                return;
            }

            int mid = (l + r) / 2;
            update(node * 2, l, mid, ql, qr);
            update(node * 2 + 1, mid + 1, r, ql, qr);

            tree[node] = tree[node * 2] + tree[node * 2 + 1];
        }

        int query(int node, int l, int r, int ql, int qr) {
            push(node, l, r);

            if (ql > r || qr < l) return 0;

            if (ql <= l && r <= qr)
                return tree[node];

            int mid = (l + r) / 2;

            return query(node * 2, l, mid, ql, qr)
                    + query(node * 2 + 1, mid + 1, r, ql, qr);
        }

        void update(int l, int r) {
            if (l > r) return;
            update(1, 0, n - 1, l, r);
        }

        int query(int l, int r) {
            if (l > r) return 0;
            return query(1, 0, n - 1, l, r);
        }
    }

    // Toggle edges on path
    static void updatePath(int a, int b) {
        while (head[a] != head[b]) {
            if (depth[head[a]] < depth[head[b]]) {
                int t = a;
                a = b;
                b = t;
            }

            seg.update(pos[head[a]], pos[a]);
            a = parent[head[a]];
        }

        if (depth[a] > depth[b]) {
            int t = a;
            a = b;
            b = t;
        }

        // exclude LCA node because values represent edges
        if (pos[a] + 1 <= pos[b])
            seg.update(pos[a] + 1, pos[b]);
    }

    // Count active edges on path
    static int queryPath(int a, int b) {
        int ans = 0;

        while (head[a] != head[b]) {
            if (depth[head[a]] < depth[head[b]]) {
                int t = a;
                a = b;
                b = t;
            }

            ans += seg.query(pos[head[a]], pos[a]);
            a = parent[head[a]];
        }

        if (depth[a] > depth[b]) {
            int t = a;
            a = b;
            b = t;
        }

        if (pos[a] + 1 <= pos[b])
            ans += seg.query(pos[a] + 1, pos[b]);

        return ans;
    }

    public static void processQueries(int n, int[][] edges, int[][] queries) {

        Solution.n = n;

        tree = new ArrayList[n + 1];
        for (int i = 1; i <= n; i++)
            tree[i] = new ArrayList<>();

        for (int[] e : edges) {
            tree[e[0]].add(e[1]);
            tree[e[1]].add(e[0]);
        }

        parent = new int[n + 1];
        depth = new int[n + 1];
        heavy = new int[n + 1];
        head = new int[n + 1];
        pos = new int[n + 1];
        size = new int[n + 1];

        Arrays.fill(heavy, -1);

        dfs(1, 0);

        curPos = 0;
        decompose(1, 1);

        seg = new SegmentTree(n);

        StringBuilder out = new StringBuilder();

        for (int[] q : queries) {
            if (q[0] == 1) {
                updatePath(q[1], q[2]);
            } else {
                out.append(queryPath(q[1], q[2])).append('\n');
            }
        }

        System.out.print(out);
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();

        int[][] edges = new int[n - 1][2];

        for (int i = 0; i < n - 1; i++) {
            edges[i][0] = sc.nextInt();
            edges[i][1] = sc.nextInt();
        }

        int q = sc.nextInt();

        int[][] queries = new int[q][3];

        for (int i = 0; i < q; i++) {
            queries[i][0] = sc.nextInt();
            queries[i][1] = sc.nextInt();
            queries[i][2] = sc.nextInt();
        }

        processQueries(n, edges, queries);
    }
}

/*
Complexity
Build: O(N)
Each Toggle: O(log²N)
Each Query: O(log²N)
Memory: O(N)
*/