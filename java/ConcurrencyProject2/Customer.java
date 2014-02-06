package Project2;

import java.util.concurrent.Semaphore;

/**
 *
 * @author Devin
 */
public class Customer implements Runnable{
    public int number;
    public int assignedNumber;
    public Semaphore assigned = new Semaphore(0, true);
    private int agentNumber;
    
    public Customer (int number){
        this.number = number;
    }
    @Override
    public void run() {
        try {
            System.out.println("Customer " + number + " created");
            
            //Customer gets in line for the information desk, line size < 10
            Project2.infoDeskLine.acquire();
            System.out.println("Customer " + number + " enters the DMV");
            //Customer adds himself to the info desk queue
            Project2.infoQueueMutex.acquire();
            Project2.infoDeskQueue.add(this);
            Project2.infoQueueMutex.release();
            //Signal the info desk line has customer in it
            Project2.custInInfoLine.release();
            //Customer waits to be assigned a number from the info desk.
            assigned.acquire();
            //Customer leaves line allowing next customer to enter the line
            Project2.infoDeskLine.release();
            
            //Customer waits until number is called
            Project2.customerWait[assignedNumber].acquire();
            //Customer is in the agentLine, and added to the queue
            Project2.agentQueueMutex.acquire();
            Project2.agentLineQueue.add(this);
            Project2.agentQueueMutex.release();
            Project2.custInAgentLine.release();
            
            //Customer's turn to announce agent servicing customer
            Project2.customerWait[assignedNumber].acquire();
            announceAgent();
            //Agent's turn to issue eye exam
            Project2.agentWait[agentNumber].release();
            
            //Customer's turn to take the eye exam
            Project2.customerWait[assignedNumber].acquire();
            takeExam();
            //Agent's turn to issue license
            Project2.agentWait[agentNumber].release();
            
            //Customer's turn to get license
            Project2.customerWait[assignedNumber].acquire();
            getLicense();
            //Signal Agent that customer has left
            Project2.agentWait[agentNumber].release();
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }
    private void announceAgent(){
        System.out.println("Customer " + number + " being served by agent "
                    + agentNumber);
    }
    private void takeExam(){
        System.out.println("Customer " + number + " completes photo and "
                + "eye exam for Agent " + agentNumber);
    }
    private void getLicense(){
        System.out.println("Customer " + number + " gets license and departs");
    }
    public void assignedNumber(int assignedNumber){
        this.assignedNumber = assignedNumber;
    }
    public void assignAgent(int agentNumber){
        this.agentNumber = agentNumber;
    }
}
