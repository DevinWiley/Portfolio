package satoshidoge.entities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luke
 */
public class DAO {
    protected Connection con;
    
    DAO() {
        Connection con = null;
        String url = "jdbc:mysql://localhost:3306/sdoge";
        String user = "root";
        String password = "sUc&hd!0g3w)0w";
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.con = con;
    }

    /**
     * @return the connection
     */
    public Connection getConnection() {
        return con;
    } 
}
