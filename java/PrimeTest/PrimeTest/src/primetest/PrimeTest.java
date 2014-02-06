/* Devin Wiley
 * dxw112230@utdallas.edu
 * A program that will get an integer from the user
 * and return the largest factor of the number. If the
 * integer is prime the output will be 1.
 */
package primetest;
import java.util.Scanner;
import java.lang.Math;

public class PrimeTest {
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Enter a positive integer," + 
                "a negative integer to end the program: ");
        int userInput = (int) keyboard.nextDouble();
        while(userInput >= 1){
            int greatestFactor = isPrime(userInput);
            if(greatestFactor == 1){
                System.out.println("The integer entered is prime.");
            } else {
                System.out.println("The Greatest Factor of " +
                        userInput + " is " + greatestFactor + ".");
            }
            System.out.print("\nEnter a new positive integer to continue," +
                    " or a value less than 1 to end the program: ");
            userInput = (int) keyboard.nextDouble();
        }
    }
    public static int isPrime(int iTest){
        int iRoot = (int) Math.sqrt(iTest);
        int i = 1;
        int greatestFactor = 1;        
        
        while(i <= iRoot){
            if((iTest % i) == 0 && i != 1){
                int factor = (iTest / i);
                if(factor >= greatestFactor){
                    greatestFactor = factor;
                }
            }
            i++;
        }
        return greatestFactor;
    }
}
