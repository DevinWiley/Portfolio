/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fibonnacci;

/**
 *
 * @author Devin
 */
public class Fibonnacci {

    /**
     * @param args the command line arguments
     */
    final static double ROOT5 = Math.sqrt(5);
    public static void main(String[] args) {
        long fib1, fibFunction;
        fib1 = f(10);
        System.out.println("Recursive function output: " + fib1);
        fibFunction = F(10);
        System.out.println("Non-recursive function output: " + fibFunction);
        boolean isEqual = (int)fib1 == (int)fibFunction;
        System.out.println("Recursive = Non-recursive? " + isEqual);
    }
    
    //Recursively computes the nth fibonacci number
    public static long f(int n){
        if (n > 1){
            return f(n -1) + f(n - 2);
        } else if (n == 1){
            return 1;
        } else {
            return 0;
        }
    }
    
    //Non-recursively computes the nth fibonacci number
    public static long F(int n){
        double fib1 = (1 / ROOT5) * Math.pow(((1 + ROOT5) / 2), n);
        double fib2 = (1 / ROOT5) * Math.pow(((1 - ROOT5) / 2), n);
        double fib = fib1 - fib2;
        return (long)fib;
    }
}
