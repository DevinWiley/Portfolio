/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package satoshidoge.controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import satoshidoge.SatoshiDoge;
import satoshidoge.walletutil.DogeCoindAccess;
import satoshidoge.walletutil.WalletFactory;
/**
 *
 * @author Alex Henkemeier
 */
public class SatoshiGame implements Runnable{
    private Connection connection;
    private PreparedStatement ps;
    private static final BigDecimal LOSS_PERCENT = BigDecimal.valueOf(0.005);
    DogeCoindAccess dogeAddr = new WalletFactory().getDogeCoind();

    public int roll(String txid){
        int roll = NumberGenerator.rollTXID(txid);
        return roll;
    }

    
    public SatoshiGame(){
            String url = "jdbc:mysql://localhost:3306/sdoge";
            String user = "root";
            String password = "sUc&hd!0g3w)0w";
            
            try{
                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException ex){
                Logger lgr = Logger.getLogger(SatoshiGame.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
    }
    
    @Override
    public void run(){ 
        while(true){
            try {
                SatoshiDoge.tx_incoming.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(SatoshiGame.class.getName()).log(Level.SEVERE, null, ex);
            }
            ResultSet result;
            try{
                result = connection.createStatement().executeQuery("SELECT * FROM tx_incoming LIMIT 0, 1");
                if(result.next()){
                        int gameName = result.getInt("game");                        
                        BigDecimal betAmount = result.getBigDecimal("amount");
                        BigDecimal multiplier = result.getBigDecimal("multiplier");
                        BigDecimal gains = BigDecimal.valueOf(0), profit;
                        String txid = result.getString("txid");
                        int sameTXID = result.getInt("sameTXID");
                        int lucky;
                        if((lucky = roll(txid)) >= gameName){
                            profit = betAmount.multiply(LOSS_PERCENT);
                            profit = profit.setScale(8, RoundingMode.FLOOR);
                            if (profit.compareTo(new BigDecimal(1)) <= 0){
                                profit = new BigDecimal(1);
                            }
                        } else {
                            profit = betAmount.multiply(multiplier);
                            gains = profit.subtract(betAmount);
                        }
                        String update = "INSERT INTO tx_resolved (txid, result, "
                                + "game, amount, profit, "
                                + "gains, returnAddress, lucky, time, sameTXID) Value ("
                                + "'" + txid + "', "
                                + "'" + ((lucky >= gameName) ? 0 : 1) + "', "
                                + "'" + gameName + "', "
                                + "'" + betAmount + "', "
                                + "'" + profit + "', "
                                + "'" + gains + "', "
                                + "'" + result.getString("returnAddress") + "', "
                                + "'" + lucky + "', "
                                + "'" + result.getLong("time") + "', "
                                + "'" + sameTXID + "' "
                                + ")";
                        connection.createStatement().executeUpdate(update);
                        SatoshiDoge.tx_resolved.release();
                        //System.out.println("Added entry to tx_resolved");
                        connection.createStatement().executeUpdate("DELETE FROM tx_incoming WHERE txid='"
                                + txid + "' AND game='" + gameName + "';");
                        //System.out.println("Deleted top entry from tx_incoming");
                }
            } catch(SQLException ex){
                Logger lgr = Logger.getLogger(SatoshiGame.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }
    
    
}