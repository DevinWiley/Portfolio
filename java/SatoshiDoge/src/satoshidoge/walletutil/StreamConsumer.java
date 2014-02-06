package satoshidoge.walletutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Devin
 */
public class StreamConsumer extends Thread {
    private InputStream inputStream;
    StringBuilder stringBuilder = new StringBuilder();
    private final Semaphore finished = new Semaphore(0);
    
    /**
     *
     * @param inputStream
     */
    public StreamConsumer(InputStream inputStream){
        this.inputStream = inputStream;
    }
    
    /**
     * Gets the result from the process output. 
     * Uses a java.util.concurrent.Semaphore to ensure that
     * String returned is accurate data.
     * @return
     */
    public String getResult(){
        try {
            finished.acquire();
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }
        return stringBuilder.toString();
    }
    
    @Override
    public void run(){
        try {
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader bufferReader = new BufferedReader(isr);
            String line;
            while ((line = bufferReader.readLine()) != null){
                stringBuilder.append(line);
                //System.out.print(line);
                //stringBuilder.append("\n");
            }
            finished.release();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
