package satoshidoge;

import java.util.concurrent.Semaphore;
import satoshidoge.controllers.MoneyProcessor;
import satoshidoge.controllers.SatoshiGame;
import satoshidoge.controllers.Startup;
import satoshidoge.controllers.TransactionListener;

/**
 *
 * @author Devin
 */
public class SatoshiDoge {
    public static Semaphore tx_incoming = new Semaphore(0);
    public static Semaphore tx_resolved = new Semaphore(0);
    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        new Startup();
        
        TransactionListener listen = new TransactionListener();
        Thread incomingTransactions = new Thread(listen);
        incomingTransactions.start();
        
        MoneyProcessor money = new MoneyProcessor();
        Thread moneyProcessor = new Thread(money);
        moneyProcessor.start();
        
        SatoshiGame game = new SatoshiGame();
        Thread gameThread = new Thread(game);
        gameThread.start();
    }
}
