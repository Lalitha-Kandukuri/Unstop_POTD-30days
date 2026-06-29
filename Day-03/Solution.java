import java.util.*;

public class Solution{

    static long min_operations(int n, int[] E, int[] T) {
        long ans = 0;

        long prevPos = 0;
        long prevNeg = 0;

        for (int i = 0; i < n; i++) {
            long diff = (long) T[i] - E[i];

            long pos = Math.max(diff, 0);
            long neg = Math.max(-diff, 0);

            if (pos > prevPos)
                ans += (pos - prevPos);

            if (neg > prevNeg)
                ans += (neg - prevNeg);

            prevPos = pos;
            prevNeg = neg;
        }

        return ans;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();

        int[] E = new int[n];
        int[] T = new int[n];

        for (int i = 0; i < n; i++)
            E[i] = sc.nextInt();

        for (int i = 0; i < n; i++)
            T[i] = sc.nextInt();

        System.out.println(min_operations(n, E, T));

        sc.close();
    }
}

/*
Time Complexity
The array is traversed only once.
Each iteration performs a constant number of operations (Math.max, subtraction, comparison).

Time Complexity: O(N)
*/