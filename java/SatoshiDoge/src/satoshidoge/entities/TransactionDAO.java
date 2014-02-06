package satoshidoge.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Luke
 */
public class TransactionDAO extends DAO{
    
    public TransactionDAO() {
        super();
    }
    
    public boolean add(Transaction transaction) {
        boolean success = true;
        
        try {
            String query = "INSERT INTO transaction(`txid`,`amount`,`game`"
                    + ",`sendAddress`,`time`) VALUES ('"
                    + transaction.getTxid() + "','"
                    + transaction.getAmount() + "','"
                    + transaction.getGame() + "','"
                    + "" + "','"                    //TODO: add send address
                    + transaction.getTime() + "');";
            
            Statement statement = con.createStatement();
            statement.executeUpdate(query);
            //PreparedStatement statement = con.prepareStatement(query);
            //ResultSet rs = statement.executeQuery();
            con.close();
        } catch (Exception ex) {
           System.err.println("Caught Exception in add Transaction: " + ex.getMessage());
           success = false;
        }
        return success;
    }
    
    public Transaction retrieve(Long id) {
        Transaction transaction = new Transaction();
        
        try {
            String query = "SELECT * FROM transaction WHERE id=" + id + ";";
            ResultSet result = con.createStatement().executeQuery(query);
            if(result.next()) {
                transaction.setTxid(result.getString("txid"));
                transaction.setAmount(result.getBigDecimal("amount"));
                transaction.setGame(result.getInt("game"));
                //TODO: set send address
                //transaction.setSendAddress(result.getDate("sendAddress"));
                transaction.setTime(result.getInt("time"));
                con.close();
            }
        } catch (Exception ex) {
            System.err.println("Caught Exception: " + ex.getMessage());
        }
        return transaction;
    }
    
    public boolean update(Transaction transaction) {
        boolean success = true;
        
        try {
            String query = "UPDATE transaction (`txid`,`amount`,`game`"
                    + ",`sendAddress`,`time`) VALUES ('"
                    + transaction.getTxid() + "','"
                    + transaction.getAmount() + "','"
                    + transaction.getGame() + "','"
                    + "" + "','"                    //TODO: add send address
                    + transaction.getTime() + "');";
            Statement statement = con.createStatement();
            statement.executeUpdate(query);
            //PreparedStatement statement = con.prepareStatement(query);
            //ResultSet rs = statement.executeQuery();
            con.close();            
        } catch (Exception ex) {
            System.err.println("Caught Exception: " + ex.getMessage());
            success = false;
        }
        return success;
    }
    
    public boolean delete(Long id) {
        boolean success = true;
        
        try {
            String query = "DELETE FROM transaction WHERE txid=" + id +";";  
            Statement statement = con.createStatement();
            statement.executeUpdate(query);
            //PreparedStatement statement = con.prepareStatement(query);
            //ResultSet rs = statement.executeQuery();
            con.close();                   
        } catch (Exception ex) {
            System.err.println("Caught Exception: " + ex.getMessage());
            success = false;
        }
        return success;
    }

}
