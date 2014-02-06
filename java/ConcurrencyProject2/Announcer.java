/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Project2;

import java.util.concurrent.Semaphore;
/**
 *
 * @author Devin
 */
public class Announcer implements Runnable{
    public static Semaphore announce = new Semaphore(0, true);
    private static int currentNumber = 0;
    
    public Announcer() {}
    @Override
    public void run() {
        System.out.println("Announcer created.");
        while (currentNumber < Project2.CUSTOMERS){
            try {
                //Announcer waits until numbers have been assigned
                announce.acquire();
                //Announcer waits for line to not be full
                Project2.agentLine.acquire();
                System.out.println("Announcer calls number " + currentNumber);
                //Agent signals that the assigned number has been called
                Project2.customerWait[currentNumber].release();
                currentNumber++;
            } catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }
}
