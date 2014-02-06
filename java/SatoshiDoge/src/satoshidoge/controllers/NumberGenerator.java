package satoshidoge.controllers;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.joda.time.DateTime;
import org.joda.time.Days;
/**
 *
 * @author Devin
 */
public class NumberGenerator{
    private static String secretKey = getValidKey();
    private static Calendar currentSecretDate;
    private static Date dayZero = setDayZero();
    private static final String SECRET_KEYS_LOCATION = "/home/ubuntu/secretkeys.hash";
    //December 26, 2013: First Secret Key Generated
    private static Date setDayZero() {
        Calendar dayZeroCal = Calendar.getInstance();
        dayZeroCal.set(Calendar.YEAR, 2013);
        dayZeroCal.set(Calendar.MONTH, 11);
        dayZeroCal.set(Calendar.DAY_OF_MONTH, 26);
	//System.out.println(dayZeroCal.getTime().toString());
        return dayZeroCal.getTime();
    }

    public NumberGenerator(){}
    
    private static String getValidKey() {
        if (secretKey == null){
            return getSecretKeyForToday();
        }
        Calendar today = Calendar.getInstance();
        boolean sameDay = currentSecretDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                currentSecretDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
        if (sameDay){
            return secretKey;
        } else {
            return getSecretKeyForToday();
        }
    }
    
    public static int rollTXID(String txid) {
        int lucky = 65536;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(getValidKey().getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(txid.getBytes());
            ByteBuffer wrapped = ByteBuffer.wrap(rawHmac);
            //System.out.println(bytesToHex(rawHmac));
            lucky = (wrapped.getInt() & 0xffff0000) >>> 16;
        } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            Logger.getLogger(NumberGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lucky;
    }
    public static String bytesToHex(byte[] bytes) {
        String form = "";
        for (int i = 0; i < bytes.length; i++){
            String str = Integer.toHexString(((int)bytes[i]) & 0xff);
            if (str.length() == 1)
            {
                str = "0" + str;
            }

            form = form + str;
        }
        return form;
    }
    private static String getSecretKeyForToday(){
        BufferedReader br = null;
	if (dayZero == null) {
		dayZero = setDayZero();
	}
        try{
            Calendar cal = Calendar.getInstance();
            Date today = cal.getTime();
            //System.out.println("DayZero: " + dayZero.toString());
            //System.out.println("Today: " + today.toString());
            int days = Days.daysBetween(new DateTime(dayZero), new DateTime(today)).getDays();
            //System.out.println("Days since dayZero: " + days);
            br = new BufferedReader(new FileReader(SECRET_KEYS_LOCATION));
            for (int i = 0; i < days; i++){
                br.readLine();
            }
            secretKey = br.readLine();
	    currentSecretDate = cal;
        } catch(IOException ex){
            Logger.getLogger(SatoshiGame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (br != null){
                    br.close();
                }
            } catch (IOException ex){
                Logger.getLogger(SatoshiGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //System.out.println("Secret Key: " + secretKey);
        return secretKey;
    }
}
