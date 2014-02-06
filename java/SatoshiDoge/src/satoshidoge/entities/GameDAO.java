package satoshidoge.entities;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Devin
 */
public class GameDAO extends DAO {
    public GameDAO(){
        super();
    }
    
    /**
     * Retrieves the addresses for every game in the Database.
     * @return Array List containing all the addresses for all of our games.
     */
    public ArrayList<String> retrieveAddresses(){
        ArrayList<String> addresses = new ArrayList<>();
        try{
            String query = "SELECT address FROM game";
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()){
                addresses.add(result.getString("address"));
            }
        } catch (Exception ex){
            System.err.println("Caught Exception: " + ex.getMessage());
        }
        addresses.trimToSize();
        return addresses;
    }
    
    public HashMap<String, Game> retrieveGames(){
        HashMap<String, Game> games = new HashMap<>();
        try {
            String query = "SELECT * FROM game";
            ResultSet result = con.createStatement().executeQuery(query);
            while (result.next()){
                String address = result.getString("address");
                BigDecimal minBet = result.getBigDecimal("minBet");
                BigDecimal maxBet = result.getBigDecimal("maxBet");
                int gameName = result.getInt("gameName");
                BigDecimal multiplier = result.getBigDecimal("multiplier");
                int id = result.getInt("id");
                Game game = new Game(address, minBet, maxBet, gameName, multiplier, id);
                games.put(address, game);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GameDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return games;
    }
}
