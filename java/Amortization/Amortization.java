//Devin Wiley
//dxw112230@zmail.com
//A program that will ask the user for values concerning a loan
//and display a loan amortization schedule. The program will iterate
//as long as the user allows it, allowing new values each iteration.

import java.util.Scanner;
import java.lang.Math;

public class Amortization{
	public static void main(String[] args){
		Scanner keyboard = new Scanner(System.in);
		
		System.out.println("To run the program, Enter any character to continue");
		String input = keyboard.nextLine();
		
		boolean nextRun = (input.length() == 0) ? false : true;
		while(nextRun){
			double sLoanValue = getSLoanValue(keyboard);
			double cLoanValue = sLoanValue;
			double annualPercentage = getAnnualPercentage(keyboard);
			int numYears = getNumYears(keyboard);
			int numPeriods = numYears * 12;
			double monthlyPayment = monthlyPayment(sLoanValue, annualPercentage, numYears);
			
			System.out.printf("%-10s %-10s %-10s %-10s\n", "Payment", "Principal", "Interest", "Balance");
			int currentPeriod = 1;
			double principal, interest;
			for(int i = 0; i < numPeriods; i++){
				interest = getInterest(cLoanValue, annualPercentage);
				principal = monthlyPayment - interest;
				cLoanValue -= principal;
				System.out.printf("%-10d $%-9.2f $%-9.2f $%-9.2f\n", currentPeriod, principal, interest, Math.abs(cLoanValue));
				currentPeriod++;
			}
			keyboard.nextLine();
			System.out.println("\nTo run again enter any character, or press enter to end the program.");
			input = keyboard.nextLine();
			nextRun = (input.length() == 0) ? false : true;
		}
	}
	
	//Takes remaining balance and determines interest due during that month
	public static double getInterest(double cLoanValue, double annualPercentage){
		double interest = cLoanValue * ( annualPercentage / 12);
		return interest;
	}
	
	//Calculates the monthly payments usings the loan value, annual percentage rate, and the total years
	public static double monthlyPayment(double sLoanValue, double annualPercentage, int numYears){
		double periodInterest = annualPercentage / 12;
		double numInterestPeriods = numYears * 12;
		double monthlyPay = (sLoanValue * periodInterest) / ( 1 - Math.pow(( 1 + periodInterest),-(numInterestPeriods)));
		return monthlyPay;
	}
	
	//Gets the value of the loan from the user.
	public static double getSLoanValue(Scanner keyboard){
		System.out.print("What is the amount of the loan(i.e. 100000)? ");
		double sLoanValue = keyboard.nextDouble();
		return sLoanValue;
	}
	
	//Gets the annual percentage rate from the user
	public static double getAnnualPercentage(Scanner keyboard){
		System.out.print("What is the Annual Percentage Rate, APR, on the loan (i.e. 5 for 5%)? ");
		double annualPercentage = keyboard.nextDouble();
		annualPercentage = annualPercentage / 100;
		return annualPercentage;
	}
	
	//Gets the length of the loan from the user in years
	public static int getNumYears(Scanner keyboard){
		System.out.print("What is the length of the loan in years(i.e. 30)? ");
		int numYears = keyboard.nextInt();
		return numYears;
	}
}