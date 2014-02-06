package satoshidoge.controllers;

import java.math.BigDecimal;
import java.util.HashMap;
import satoshidoge.entities.Game;
import satoshidoge.entities.GameDAO;

/**
 *
 * @author Devin
 */
public class GameVerify {
    private static HashMap<String, Game> validGames = new HashMap<>();
    
    public GameVerify(){
        if (validGames.isEmpty()){
            validGames = new GameDAO().retrieveGames();
        }
    }
    
    /**
     * Finds the integer representation for the gameName for the
     * given address
     * @param address
     * @return integer representation of the gameName
     */
    public int findGameName(String address){
        int gameName = 0;
        if (validGames.containsKey(address)){
            gameName = validGames.get(address).getGameName();
        }
        return gameName;
    }
    public boolean validGameAddress(String address){
        boolean valid = false;
        if (validGames.containsKey(address)){
            valid = true;
            //System.out.println("ValidAddress");
        }
        return valid;
    }
    public boolean validBet(String address, BigDecimal betAmount){
        boolean valid = false;
            if (validGames.containsKey(address)){
            Game tempGame = validGames.get(address);
            if (tempGame.getAddress().equals(address)){
                if (tempGame.getMaxBet().compareTo(betAmount) >= 0
                        && tempGame.getMinBet().compareTo(betAmount) <= 0){
                    //System.out.println("ValidBet");
                    valid = true;
                }
            }
        }
        return valid;
    }
    public boolean verifyTransaction(String address, BigDecimal betAmount){
        boolean valid = false;
        if (validBet(address, betAmount) && validGameAddress(address)){
            valid = true;
        }
        return valid;
    }
    public BigDecimal getMultiplier(String address){
        return validGames.get(address).getMultiplier();
    }
}
