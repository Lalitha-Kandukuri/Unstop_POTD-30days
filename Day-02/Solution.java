import java.util.*;

public class Solution {

    static int[][] tree;
    static int[] lazy;
    static int[] arr;

    static void build(int node, int l, int r) {
        if (l == r) {
            tree[node][arr[l]] = 1;
            return;
        }

        int mid = (l + r) / 2;
        build(node * 2, l, mid);
        build(node * 2 + 1, mid + 1, r);

        for (int i = 0; i < 8; i++) {
            tree[node][i] = tree[node * 2][i] + tree[node * 2 + 1][i];
        }
    }

    static void apply(int node, int mask) {
        if (mask == 0) return;

        int[] temp = new int[8];
        for (int i = 0; i < 8; i++) {
            temp[i ^ mask] = tree[node][i];
        }
        tree[node] = temp;
        lazy[node] ^= mask;
    }

    static void push(int node) {
        if (lazy[node] != 0) {
            apply(node * 2, lazy[node]);
            apply(node * 2 + 1, lazy[node]);
            lazy[node] = 0;
        }
    }

    static void update(int node, int l, int r, int ql, int qr, int mask) {
        if (ql <= l && r <= qr) {
            apply(node, mask);
            return;
        }

        push(node);

        int mid = (l + r) / 2;

        if (ql <= mid)
            update(node * 2, l, mid, ql, qr, mask);
        if (qr > mid)
            update(node * 2 + 1, mid + 1, r, ql, qr, mask);

        for (int i = 0; i < 8; i++) {
            tree[node][i] = tree[node * 2][i] + tree[node * 2 + 1][i];
        }
    }

    static int[] query(int node, int l, int r, int ql, int qr) {
        if (ql <= l && r <= qr)
            return tree[node];

        push(node);

        int mid = (l + r) / 2;

        if (qr <= mid)
            return query(node * 2, l, mid, ql, qr);

        if (ql > mid)
            return query(node * 2 + 1, mid + 1, r, ql, qr);

        int[] left = query(node * 2, l, mid, ql, qr);
        int[] right = query(node * 2 + 1, mid + 1, r, ql, qr);

        int[] res = new int[8];
        for (int i = 0; i < 8; i++) {
            res[i] = left[i] + right[i];
        }

        return res;
    }

    public static List<Integer> process_events(int n, int q, int[] receptors, List<List<Integer>> events) {

        arr = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            arr[i] = receptors[i - 1];
        }

        tree = new int[4 * n + 5][8];
        lazy = new int[4 * n + 5];

        build(1, 1, n);

        List<Integer> ans = new ArrayList<>();

        for (List<Integer> event : events) {

            if (event.size() == 4) {
                int L = event.get(1);
                int R = event.get(2);
                int M = event.get(3);

                update(1, 1, n, L, R, M);
            } else {
                int L = event.get(1);
                int R = event.get(2);

                int[] cnt = query(1, 1, n, L, R);

                int best = 0;
                for (int i = 0; i < 8; i++) {
                    best = Math.max(best, cnt[i]);
                }

                ans.add(best);
            }
        }

        return ans;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();
        int q = sc.nextInt();

        int[] receptors = new int[n];
        for (int i = 0; i < n; i++) {
            receptors[i] = sc.nextInt();
        }

        List<List<Integer>> events = new ArrayList<>();

        for (int i = 0; i < q; i++) {

            int type = sc.nextInt();

            if (type == 1) {
                int L = sc.nextInt();
                int R = sc.nextInt();
                int M = sc.nextInt();
                events.add(Arrays.asList(type, L, R, M));
            } else {
                int L = sc.nextInt();
                int R = sc.nextInt();
                events.add(Arrays.asList(type, L, R));
            }
        }

        List<Integer> results = process_events(n, q, receptors, events);

        for (int x : results) {
            System.out.println(x);
        }

        sc.close();
    }
}

/*
Operation || Complexity
Build Segment Tree	O(N)
Range XOR Update	O(8 log N) = O(log N)
Range Query	O(8 log N) = O(log N)

Since the frequency array size is fixed at 8 (values 0 to 7), operations on it take constant time.

Therefore, for Q operations:

Total Time Complexity: O(N + Q log N)
*/