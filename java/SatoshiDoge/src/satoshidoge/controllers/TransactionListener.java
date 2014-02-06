package satoshidoge.controllers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import satoshidoge.SatoshiDoge;
import satoshidoge.entities.DAO;
import satoshidoge.entities.Transaction;
import satoshidoge.entities.TransactionList;
import satoshidoge.exceptions.InvalidListOperation;
import satoshidoge.walletutil.DogeCoindAccess;
import satoshidoge.walletutil.WalletFactory;

/**
 *
 * @author Luke
 */
public final class TransactionListener implements Runnable {
    private Connection connection;
    private String lastBlockProcessed;      //hash of last block
    private long lastBlockTime;
    private String currentBlockProcessing;  //hash of current block
    private String mostRecentBlockProcessed;
    private long currentBlockTime;            //time of current transaction
    private long mostRecentBlockTime;
    private long lastTransactionTime;
    private final GameVerify gameVerify = new GameVerify();
    private final DogeCoindAccess dogecoind = new WalletFactory().getDogeCoind();

    public TransactionListener() {
        connect();
    }
    
    public void connect(){
        String url = "jdbc:mysql://localhost:3306/sdoge";
        String user = "root";
        String password = "sUc&hd!0g3w)0w";
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            String query = "SELECT * FROM util;";
            ResultSet result = connection.createStatement().executeQuery(query);
            if(result.next()) {
                lastBlockProcessed = result.getString("lastBlockProcessed");
                lastTransactionTime = result.getLong("lastTimeProcessed");
                currentBlockProcessing = lastBlockProcessed;
                lastBlockTime = dogecoind.getBlockTime(lastBlockProcessed);
                mostRecentBlockTime = lastTransactionTime;
                currentBlockTime = lastBlockTime;
                //System.out.println("LastBlockProcessed: " + lastBlockProcessed);
            }
        } catch (SQLException ex) {
            System.err.println("Caught Exception: " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        while(true) {
            //System.out.println("Begin TransactionListener Loop");
            //Fill txList with transactions from this block
            //Returns null if there has been no transactions since last block
            HashMap<String, TransactionList> txList = populateList();
            Transaction transaction;
            if (txList != null){
                for(int i = 0; i < txList.size(); i++){
                    TransactionList currentList = getTransactionList(txList, i);
                    for (int j = 0; j < currentList.length(); j++){
                        transaction = currentList.get(j);
                        String query=null;
                        //Bet amount is valid for a valid game
                        if (gameVerify.verifyTransaction(transaction.getAddress(), transaction.getAmount())
					&& gameVerify.findGameName(transaction.getAddress()) > 0
					&& gameVerify.findGameName(transaction.getAddress()) < 65536){
                            try {
                                query = "INSERT INTO tx_incoming (txid, amount, multiplier, game,"
                                        + "returnAddress, time, sameTXID) VALUES ("
                                        + "'" + transaction.getTxid() + "', "
                                        + "'" + transaction.getAmount() + "', "
                                        + "'" + gameVerify.getMultiplier(transaction.getAddress()) + "', "
                                        + "'" + gameVerify.findGameName(transaction.getAddress()) + "', "
                                        + "'" + dogecoind.getReturnAddressByTXID(transaction.getTxid()) + "', "
                                        + "'" + transaction.getTime() + "', "
                                        + "'" + currentList.length() + "' "
                                        + ");";
                                Statement statement = connection.createStatement();
                                statement.executeUpdate(query);
                                SatoshiDoge.tx_incoming.release();
                                //System.out.println("Added " + transaction.getTxid() + " to tx_incoming");
                                //System.out.println("Read transaction " + transaction.getTxid());
                                String update = "DELETE FROM tx_unconfirmed WHERE txid='" 
                                        + transaction.getTxid() + "';";
                                connection.createStatement().executeUpdate(update);
                                //System.out.println("Deleted from tx_unconfirmed");
                             } catch (SQLException ex) {
                                 System.err.println("Caught Exception in add Transaction: " + ex.getMessage());
                                 //System.out.println(query);
                             }
                        } else {
                            //Transaction isn't correct for some reason
                            try {
                                String update;
                                //If it was for a valid address then we send it back
                                if (gameVerify.findGameName(transaction.getAddress()) > 0 &&
                                        gameVerify.findGameName(transaction.getAddress()) < 65536){
                                    update = "INSERT INTO tx_resolved (txid, result, "
                                            + "game, amount, profit, "
                                            + "gains, returnAddress, lucky, time, sameTXID) Value ("
                                            + "'" + transaction.getTxid() + "', "
                                            + "'" + 2 + "', "
                                            + "'" + gameVerify.findGameName(transaction.getAddress()) + "', "
                                            + "'" + transaction.getAmount() + "', "
                                            + "'" + transaction.getAmount() + "', "
                                            + "'" + 0 + "', "
                                            + "'" + dogecoind.getReturnAddressByTXID(transaction.getTxid()) + "', "
                                            + "'" + 32768 + "', "
                                            + "'" + transaction.getTime() + "', "
                                            + "'" + currentList.length() + "' "
                                            + ")";
                                    //If it's a deposit or invalid address, we keep it
                                } else if (gameVerify.findGameName(transaction.getAddress()) <= 0) {
                                    update = "INSERT INTO tx_processed (txid, out_txid, result, "
                                            + "game, amount, profit, "
                                            + "gains, returnAddress, lucky, time) Value ("
                                            + "'" + transaction.getTxid() + "', "
                                            + "'Deposit',"
                                            + "'" + 2 + "', "
                                            + "'" + gameVerify.findGameName(transaction.getAddress()) + "', "
                                            + "'" + transaction.getAmount() + "', "
                                            + "'" + transaction.getAmount() + "', "
                                            + "'" + 0 + "', "
                                            + "'" + dogecoind.getReturnAddressByTXID(transaction.getTxid()) + "', "
                                            + "'" + 65536 + "', "
                                            + "'" + transaction.getTime() + "' "
                                            + ")";
                                    //Default: We send it back. Unreachable
                                } else {
                                    update = "INSERT INTO tx_resolved (txid, result, "
                                            + "game, amount, profit, "
                                            + "gains, returnAddress, lucky, time, sameTXID) Value ("
                                            + "'" + transaction.getTxid() + "', "
                                            + "'" + 2 + "', "
                                            + "'" + gameVerify.findGameName(transaction.getAddress()) + "', "
                                            + "'" + transaction.getAmount() + "', "
                                            + "'" + transaction.getAmount() + "', "
                                            + "'" + 0 + "', "
                                            + "'" + dogecoind.getReturnAddressByTXID(transaction.getTxid()) + "', "
                                            + "'" + 32768 + "', "
                                            + "'" + transaction.getTime() + "', "
                                            + "'" + currentList.length() + "' "
                                            + ")";
                                }
                                connection.createStatement().executeUpdate(update);
                                SatoshiDoge.tx_resolved.release();
                                //System.out.println("Added to tx_resolved");
                                String update2 = "DELETE FROM tx_unconfirmed WHERE txid='" 
                                        + transaction.getTxid() + "';";
                                connection.createStatement().executeUpdate(update2);
                            } catch (SQLException ex) {
                                Logger.getLogger(TransactionListener.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
                updateBlock();
                updateTime();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(TransactionListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public HashMap<String, TransactionList> populateList() {
        //System.out.println("Inside populateList");
        JSONArray txObject = dogecoind.getTransactionsSinceBlock(lastBlockProcessed);
        long maxTransactionTime = 0;
        HashMap<String, TransactionList> txList = new HashMap<>();
        if (txObject.length() == 0) {
            //System.out.println("Transactions: UP-TO-DATE");
            return null;
        }
        //System.out.println("transaction objects: " + txObject.length());
        for (int i = 0; i < txObject.length(); i++){
            Object jsonObject = null;
            try {
                jsonObject = txObject.getJSONObject(i);
                JSONObject jsonTxObject = (JSONObject)jsonObject;
                //If transaction time > lastTransaction (it's newer) then we process it
                if (jsonTxObject.getLong("time") >= lastTransactionTime){
                    //System.out.println("More recent than last transactions");
                    //We keep track of the most recent transaction in the current Set
                    //We will set this to mostRecentTransaction Later
                    if (jsonTxObject.getLong("time") > maxTransactionTime){
                        maxTransactionTime = jsonTxObject.getLong("time");
			//System.out.println("maxTransactionTime: " + maxTransactionTime);
                    }
                    //We check to see if the transaction was either a receive or sending
                    if (jsonTxObject.getString("category").equals("receive")){
                        //System.out.println("Transaction was received");
                        //We Check tx_processing if it has been already added
                        //Deprecated but doesn't hurt to keep
                        int tempGame = gameVerify.findGameName(jsonTxObject.getString("address"));
                        String addr = jsonTxObject.getString("txid");
                        String query = "SELECT txid, game FROM tx_processed WHERE txid='"
                                + addr + "' AND game='"
                                + tempGame
                                + "' UNION "
                                + "SELECT txid, game FROM tx_resolved WHERE txid='"
                                + addr + "' AND game='"
                                + tempGame
                                + "' UNION "
                                + "SELECT txid, game FROM tx_incoming WHERE txid='"
                                + addr + "' AND game='"
                                + tempGame
                                + "';";
                        ResultSet result = null;
                        try {
                            result = connection.createStatement().executeQuery(query);
                            //If it isn't in tx_processed, tx_resolved, or tx_incoming
                            if (!result.next()){
                                //System.out.println("Transaction not in tx_processed");
                                //Check to see if linked list is inside HashMap
                                Transaction transaction = new Transaction(
                                        jsonTxObject.getString("account"),
                                        jsonTxObject.getString("address"),
                                        jsonTxObject.getString("txid"),
                                        BigDecimal.valueOf(jsonTxObject.getDouble("amount")),
                                        jsonTxObject.getInt("time"));
                                String tempKey;
                                if (txList.containsKey(tempKey = jsonTxObject.getString("txid"))){
                                    try {
                                        //System.out.println("Added to TransactionList: " + tempKey);
                                        txList.get(tempKey).add(transaction);
                                    } catch (InvalidListOperation ex) {
                                        //System.out.println("InvalidListOperation caught: " + tempKey + " used.");
                                    }
                                } else {
                                    //System.out.println("Created new TransactionList: " + tempKey);
                                    txList.put(tempKey, new TransactionList(tempKey));
                                    try {
                                        txList.get(tempKey).add(transaction);
                                    } catch (InvalidListOperation ex) {
                                        //System.out.println("InvalidListOperation caught: " + tempKey + " used.");
                                    }
                                }
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(TransactionListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                //We check if the the transaction is more recent than the last blockhash
                //If it is we test it for a blockhash
		//throws JSONException if it doesn't have a blockhash
		if (jsonTxObject.getLong("time") > dogecoind.getBlockTime(lastBlockProcessed)){
                    String tempBlockHash = jsonTxObject.getString("blockhash");
                    //Make sure not to set null to any private variables.
                    if (tempBlockHash != null){
                        long tempTime;
                        //if the currentBlockTime is greater than previous currentBlockTimes
                        //Then it must be most recent block so it is saved.
                        if ((tempTime = dogecoind.getBlockTime(tempBlockHash)) > currentBlockTime){
                            currentBlockProcessing = tempBlockHash;
                            currentBlockTime = tempTime;
                        }
                    }
                }
            } catch (JSONException ex){
                //Logger.getLogger(TransactionListener.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    /*  We fall here when ther isn't a blockhash for the transaction
                        So we test whether or not we sent or received the transactions
                        If we sent the transaction, then we Rebroadcast the transaction
                    */
                    if (jsonObject instanceof JSONObject){
                        JSONObject tempObject = (JSONObject)jsonObject;
                        if (tempObject.getString("category").equals("send")){
                            String transactionHex = dogecoind.getRawTransactionHex(tempObject.getString("txid"));
                            dogecoind.sendRawTransaction(transactionHex);
                        }
                    }
                } catch (JSONException ex2){
                    
                }
            }
        }
	if (maxTransactionTime >= mostRecentBlockTime){
		mostRecentBlockTime = maxTransactionTime;
	}
        //Most recent block is saved to lastBlockProcessed and will be used for next populateList
        if (currentBlockTime > lastBlockTime){
            lastBlockProcessed = currentBlockProcessing;
            //lastBlockTime = currentBlockTime;
        }
        return txList;
    }
    
    public TransactionList getTransactionList(Map map, int i){
        Iterator it = map.entrySet().iterator();
        TransactionList list = null;
        int j = 0;
        while (it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            if (j == i){
                list = (TransactionList)entry.getValue();
                break;
            }
        }
        return list;
    }
    
    public void updateBlock() {
        //Update database
        try {
            String query = "UPDATE util SET lastBlockProcessed="
                    + "\""+ lastBlockProcessed +"\";";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            //System.out.println("Last Block Processed: " + lastBlockProcessed);
        } catch (SQLException ex) {
           System.err.println("Caught Exception in add Transaction: " + ex.getMessage());
        }
    }
    public void updateTime(){
        try {
            String query = "UPDATE util SET lastTimeProcessed="
                    + "\""+ mostRecentBlockTime +"\";";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
	    lastBlockTime = mostRecentBlockTime;
            //System.out.println("Last Block Transaction: " + lastBlockTime);
        } catch (SQLException ex) {
           System.err.println("Caught Exception in add Transaction: " + ex.getMessage());
        }
    }
}
