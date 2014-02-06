package Project2;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Devin
 */
public class Project2{
    public static final int MAX_IN_LINE = 10;
    public static final int CUSTOMERS = 100;
    public static final int AGENTS = 4;
    public static final int INFORMATION_DESKS = 1;
    public static final int ANNOUNCERS = 1;
    public static int serviced = 0;
    public static int assigned = 0;
    
    public static Semaphore infoDeskLine = new Semaphore(MAX_IN_LINE);
    public static Semaphore agentLine = new Semaphore(MAX_IN_LINE, true);
    public static Semaphore custInAgentLine = new Semaphore(0, true);
    public static Semaphore custInInfoLine = new Semaphore(0, true);
    public static Semaphore[] customerWait = new Semaphore[CUSTOMERS];
    public static Semaphore[] agentWait = new Semaphore[AGENTS];
    public static Semaphore agentQueueMutex = new Semaphore(1, true);
    public static Semaphore infoQueueMutex = new Semaphore(1, true);
    public static Semaphore servicedMutex = new Semaphore(1, true);
    public static Semaphore assignedMutex = new Semaphore(1, true);
    
    public static Thread[] customers = new Thread[CUSTOMERS];
    public static Thread[] agents = new Thread[AGENTS];
    public static Thread announcers;
    public static Thread informationDesk;
    
    public static Queue<Customer> agentLineQueue = new LinkedList<Customer>();
    public static Queue<Customer> infoDeskQueue = new LinkedList<Customer>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Create all the threads
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        initAgents();
        initInfoDesk();
        initAnnouncers();        
        initCustomers();
        
        //join all the threads to main and end simulation
        joinCustomers();
        joinInfoDesk();
        joinAnnouncers();
        joinAgents();
        
        System.out.println("\nSimulation Complete");
    }
    
    //Initiation of the threads
    private static void initAgents(){
        for (int i = 0; i < AGENTS; i++){
            agentWait[i] = new Semaphore(0, true);
            agents[i] = new Thread(new Agent(i));
            agents[i].start();
            agents[i].setPriority(Thread.MAX_PRIORITY);
        }
    }
    private static void initAnnouncers(){
        for (int i = 0; i < ANNOUNCERS; i++){
            announcers = new Thread(new Announcer());
            announcers.start();
            announcers.setPriority(Thread.MAX_PRIORITY);
        }
    }
    private static void initInfoDesk(){
        for (int i = 0; i < INFORMATION_DESKS; i++){
            informationDesk = new Thread(new InformationDesk());
            informationDesk.start();
            informationDesk.setPriority(Thread.MAX_PRIORITY);
        }
    }
    private static void initCustomers(){
        for (int i = 0; i < CUSTOMERS; i++){
            //initiates the customerWait Semaphore to wait for announcer to call
            customerWait[i] = new Semaphore(0, true);
            customers[i] = new Thread(new Customer(i));
            customers[i].start();
        }
    }
    
    //Joining of the threads
    private static void joinAgents(){
        for (int i = 0; i < AGENTS; i++){
            try {
                agents[i].join();
                System.out.println("Agent " + i + " was joined");
            } catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }
    private static void joinCustomers(){
        for (int i = 0; i < CUSTOMERS; i++){
            try {
                customers[i].join();
                System.out.println("Customer " + i + " was joined");
            } catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }
    private static void joinAnnouncers(){
        for (int i = 0; i < ANNOUNCERS; i++){
            try {
                announcers.join();
                System.out.println("Announcer was Joined");
                //System.out.println("Announcer was joined");
            } catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }
    private static void joinInfoDesk(){
        for (int i = 0; i < INFORMATION_DESKS; i++){
            try{
                informationDesk.join();
                System.out.println("Information Desk was Joined.");
                //System.out.println("Information Desk was joined");
            } catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }
}
