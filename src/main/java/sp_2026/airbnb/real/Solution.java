package sp_2026.airbnb.real;

// Let’s say I am an Airbnb Host to rent out one property. In the next 30 days (1..30), I receive multiple booking requests. Each request is represented as an interval [checkIn, checkOut), and daily price is the same. Write a program to choose requests that maximize the revenue.


// sample input

// (16, 21), (1, 3), (10, 17), (10, 11), (16, 17), (8, 11), (23, 26), (2, 5), (25, 30)

// output:

// (2, 5), (8, 11), (16, 21), (25, 30)

// explanation:

// Maximum revenue: 16
// [16, 21), days : 5
// [8, 11), days : 3
// [25, 30), days : 5
// [2, 5), days : 3

// input: (2, 5), (1, 2), (1, 4)
// output: (1, 2), (2, 5)

// input: (5, 30), (1, 2), (2, 4), (1, 10)
// output: (1, 2), (2, 4), (5, 30)

// (1, 2), (1, 30), (2, 4), (5, 500)


import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.net.InetAddress;
import java.util.regex.*;

public class Solution {

    static int addNumbers(int a, int b) {
      	return a+b; 
    }

    public static void main(String[] args) {
        
        
        // (16, 21), (1, 3), (10, 17), (10, 11), (16, 17), (8, 11), (23, 26), (2, 5), (25, 30)
        
        // (1, 2), (1, 30), (2, 4), (5, 500)
        
        int day = 500;
        List<List<Integer>> intervals = new ArrayList<>();
        intervals.add(Arrays.asList(1, 2));
        intervals.add(Arrays.asList(1, 30));
        intervals.add(Arrays.asList(2, 4));
        intervals.add(Arrays.asList(5, 500));
        // intervals.add(Arrays.asList(16, 17));
        // intervals.add(Arrays.asList(8, 11));
        // intervals.add(Arrays.asList(23, 26));
        // intervals.add(Arrays.asList(2, 5));
        // intervals.add(Arrays.asList(25, 30));
        
        System.out.println(maxProfit(day, intervals));
    }
    
    
    public static List<List<Integer>> maxProfit(int n,List<List<Integer>> intervals){
        
        List<List<Integer>> res = new ArrayList<>();
        if (n < 1 || intervals == null || intervals.size() == 0){
            return res;
        }        
        
        intervals.sort((a,b) -> a.get(1) - b.get(1));
        int[] profit = new int[n+1];
        int[] select = new int[n+1];
        select[0] = -1;
        int index = 0;
        
        for (int i=1;i<=n;i++){
            profit[i] = profit[i-1];
            select[i] = select[i-1];
            while (index < intervals.size() && i >= intervals.get(index).get(1) -1 ){
                int begin = intervals.get(index).get(0);
                int end = intervals.get(index).get(1);
                
                if (end - begin + profit[begin-1] > profit[i]){
                    profit[i] = end - begin + profit[begin-1];
                    select[i] = index;
                }
                index++;
            }
        }
        
        
        // System.out.println(Arrays.toString(profit));
        // System.out.println(Arrays.toString(select));
        
        // int max = 0;
        // int maxIndex = 0;
        // for (int i=1;i<=n;i++){
        //     if (profit[i] > max){
        //         max = profit[i];
        //         maxIndex = i;
        //     }
        // }
        
        index = n;
        while(select[index] >= 0){
            res.add(intervals.get(select[index]));
            index = intervals.get(select[index]).get(0)-1;
        }
        
        res.sort((a,b) -> a.get(0) - b.get(0));
        
        return res;
    }
}
