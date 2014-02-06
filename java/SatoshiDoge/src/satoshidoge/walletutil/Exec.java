package satoshidoge.walletutil;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Devin
 */
public abstract class Exec {
    /**
     * Executes a the passed command and returns the
     * output for the given command as a String.
     * @param cmd
     * @return 
     */
    protected String executeCommand(String[] cmd){
        String output = null;
        try {
            ProcessBuilder procBuilder = new ProcessBuilder(cmd);
            procBuilder.redirectOutput();
            Process proc = procBuilder.start();
            InputStream stdout = proc.getInputStream();
            StreamConsumer stdoutConsume = new StreamConsumer(stdout);
            stdoutConsume.start();
            output = stdoutConsume.getResult();
            proc.destroy();
        } catch (IOException ex) {
            Logger.getLogger(DogeCoind.class.getName()).log(Level.SEVERE, "Complete Failure", ex);
        }
        return output;
    }
}
