package satoshidoge.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import satoshidoge.SatoshiDoge;
import satoshidoge.entities.DAO;

/**
 *
 * @author Devin
 */
public final class Startup {
    private Connection connection;
    public Startup(){
        connect();
        handlePreviousTransactions();
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

    private void handlePreviousTransactions() {
        try {
            String query = "SELECT * FROM tx_resolved";
            ResultSet result = connection.createStatement().executeQuery(query);
            while (result.next()){
                SatoshiDoge.tx_resolved.release();
            }
            query = "SELECT * FROM tx_incoming";
            result = connection.createStatement().executeQuery(query);
            while (result.next()){
                SatoshiDoge.tx_incoming.release();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Startup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}