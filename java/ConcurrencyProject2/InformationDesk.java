package Project2;

import java.util.concurrent.Semaphore;
/**
 *
 * @author Devin
 */
public class InformationDesk implements Runnable{
    private static int currentAssignment = 0;
    
    //Constructors
    public InformationDesk() {}
    
    private static int assignNumber(){
        int temp = currentAssignment;
        currentAssignment++;
        return temp;
    }
    @Override
    public void run() {
        System.out.println("Information Desk Created.");
        try {
            Project2.assignedMutex.acquire();
            while (++Project2.assigned <= Project2.CUSTOMERS){
                Project2.assignedMutex.release();
                infoDeskTasks();
                Project2.assignedMutex.acquire();
            }
            Project2.assignedMutex.release();
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }
    private void infoDeskTasks() throws InterruptedException{
        //InfoDesk waits until a customer is in the line
        Project2.custInInfoLine.acquire();
        //InfoDesk accesses the queue of customers to the info desk
        Project2.infoQueueMutex.acquire();
        Project2.infoDeskQueue.peek().assignedNumber = assignNumber();
        int custnr = Project2.infoDeskQueue.peek().number;
        System.out.println("Customer " + custnr + " gets number " 
                + (currentAssignment - 1) + ", waits to be called");
        Project2.infoDeskQueue.poll().assigned.release();
        Project2.infoQueueMutex.release();
        //Announcer is signaled that a number has been assigned.
        Announcer.announce.release();
    }
    
}
