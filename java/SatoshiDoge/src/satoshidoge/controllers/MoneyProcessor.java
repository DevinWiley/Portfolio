package satoshidoge.controllers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import satoshidoge.SatoshiDoge;
import satoshidoge.entities.DAO;
import satoshidoge.entities.GameResult;
import satoshidoge.exceptions.TransactionNotFound;
import satoshidoge.walletutil.DogeCoindAccess;
import satoshidoge.walletutil.WalletFactory;

/**
 *
 * @author Devin
 */
public final class MoneyProcessor implements Runnable {
    private Connection connection;
    private final DogeCoindAccess dogecoind = new WalletFactory().getDogeCoind();
    private static final String fromAddress = "DHWnkqxkcdRzq9EWQ4LVzk9AyVxPFZQzjn";
    private static final BigDecimal TX_FEE = BigDecimal.valueOf(0.001);
    
    public MoneyProcessor(){
        connect();
        dogecoind.setTXfee(TX_FEE);
    }
    
    public void connect(){
        Connection con = null;
        String url = "jdbc:mysql://localhost:3306/sdoge";
        String user = "root";
        String password = "sUc&hd!0g3w)0w";
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        connection = con;
    }
    
    @Override
    public void run() {
        while (true){
            try {
                SatoshiDoge.tx_resolved.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(MoneyProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
            String query = null;
            try {
                query = "SELECT * FROM tx_resolved LIMIT 0, 1";
                ResultSet result = connection.createStatement().executeQuery(query);
                //Get first element in the table
                if (result.next()){
                    //Get the txid for that row
                    String tempTXID = result.getString("txid");
                    //Determine how many rows share the same txid
                    query = "SELECT COUNT(*) FROM tx_resolved where txid='"
                        + tempTXID + "';";
                    ResultSet count = connection.createStatement().executeQuery(query);
                    int rows = 0;
                    //Copy the number of rows currently in db
                    if (count.next()){
                        rows = count.getInt(1);
                    }
                    //Get the number transactions that share a same TXID
                    int recipients = result.getInt("sameTXID");
                    //Test if all rows for a transaction are in tx_resolved
                    if (rows == recipients){
                        //Get all entries from database
                        query = "SELECT * FROM tx_resolved WHERE txid='"
                                + tempTXID + "';";
                        ResultSet transactions = connection.createStatement().executeQuery(query);
                        ArrayList<GameResult> list = new ArrayList<>();
                        while (transactions.next()){
                            GameResult temp = new GameResult(
                                    transactions.getString("txid"),
                                    transactions.getInt("result"),
                                    transactions.getInt("game"),
                                    transactions.getBigDecimal("amount"),
                                    transactions.getBigDecimal("profit"),
                                    transactions.getBigDecimal("gains"),
                                    transactions.getString("returnAddress"),
                                    transactions.getInt("lucky"),
                                    transactions.getLong("time")
                            );
                            list.add(temp);
                        }
                        String txid_out = transferDoge(list);
                        for (int i = 0; i < list.size(); i++){
                        String update = "INSERT INTO tx_processed (txid, out_txid, result,"
                                + "game, amount, profit, gains, returnAddress, lucky, time) VALUE ("
                                + "'" + list.get(i).getTxid() + "', "
                                + "'" + txid_out + "', "
                                + "'" + list.get(i).getResult() + "', "
                                + "'" + list.get(i).getGameName() + "', "
                                + "'" + list.get(i).getAmount() + "', "
                                + "'" + list.get(i).getProfit() + "', "
                                + "'" + list.get(i).getGains() + "', "
                                + "'" + list.get(i).getReturnAddress() + "', "
                                + "'" + list.get(i).getLucky() + "', "
                                + "'" + list.get(i).getTime() + "' "
                                + ")";
                        connection.createStatement().executeUpdate(update);
                        //System.out.println("Added to tx_processed");
                        }
                        //System.out.println("Deleted top from tx_resolved");
                        connection.createStatement().executeUpdate("DELETE FROM tx_resolved WHERE txid='"
                                + list.get(0).getTxid() + "';");
                    }
                }
            } catch (SQLException ex){
                Logger lgr = Logger.getLogger(SatoshiGame.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }
    
    public String transferDoge(ArrayList<GameResult> games){
        String txid_out;
        BigDecimal total = new BigDecimal(0d);
        for (int i = 0; i < games.size(); i ++){
            total = total.add(games.get(i).getProfit());
        }
        try {
            //System.out.println(games.get(0).getTxid());
            txid_out = dogecoind.deployTransaction(games.get(0).getTxid(), total, games.get(0).getReturnAddress(), fromAddress);
            //System.out.println(txid_out);
        } catch (TransactionNotFound | JSONException ex){
            Logger.getLogger(MoneyProcessor.class.getName()).log(Level.SEVERE, null, ex);
            txid_out = "Error";
        }
        return txid_out;
    }
}
