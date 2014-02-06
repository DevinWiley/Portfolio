package Project2;

import java.util.concurrent.Semaphore;
/**
 *
 * @author Devin
 */
public class Agent implements Runnable{
    private int agentNumber;
    
    public Agent(int number){
        this.agentNumber = number;
    }
    @Override
    public void run() {
        System.out.println("Agent " + agentNumber + " created.");
        try{
            //Access serviced to see if all customers have been served
            Project2.servicedMutex.acquire();
            while (++Project2.serviced <= Project2.CUSTOMERS){
                Project2.servicedMutex.release();
                agentTasks();
                Project2.servicedMutex.acquire();
            }
            //All customers serviced, release serviced so others can access
            Project2.servicedMutex.release();
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }
    private void agentTasks() throws InterruptedException{
        //Agent waits until customer is in agentLine
        Project2.custInAgentLine.acquire();
        //Agent accesses the queue of customers
        Project2.agentQueueMutex.acquire();
        int custAssigned = Project2.agentLineQueue.peek().assignedNumber;
        int custNum = Project2.agentLineQueue.peek().number;
        Project2.agentLineQueue.poll().assignAgent(agentNumber);
        Project2.agentQueueMutex.release();
        //Signal that person has left agentLine, allow announcer to call next
        Project2.agentLine.release();
        
        //Agent announces serving customer
        announceServing(custNum);
        //Customer's turn to announce agent is servicing them
        Project2.customerWait[custAssigned].release();
        
        //Agent's turn to ask customer to take eye exam
        Project2.agentWait[agentNumber].acquire();
        issueExam(custNum);
        //Customer's turn to take the eye exam
        Project2.customerWait[custAssigned].release();
        
        //Agent's turn to issue the license
        Project2.agentWait[agentNumber].acquire();
        issueLicense(custNum);
        Project2.customerWait[custAssigned].release();
        Project2.agentWait[agentNumber].acquire();
    }
    private void announceServing(int custNum){
        System.out.println("Agent " + agentNumber + " serving Customer "
                + custNum);
    }
    private void issueExam(int custNum){
        System.out.println("Agent " + agentNumber + " asks Customer " + custNum
                + " to take a photo and eye exam");
    }
    private void issueLicense(int custNum){
        System.out.println("Agent " + agentNumber + " gives license to "
                + "customer " + custNum);
    }
}
