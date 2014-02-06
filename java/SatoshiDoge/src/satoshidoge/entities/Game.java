package satoshidoge.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import satoshidoge.walletutil.DogeCoindAccess;
import satoshidoge.walletutil.WalletFactory;

/**
 *
 * @author Devin
 */
public class Game {
    private int gameName;
    private String address;
    private BigDecimal minBet;
    private BigDecimal maxBet;
    private int id;
    private BigDecimal multiplier;
    
    /**
     * @param name
     * @param winPercent 
     */
    public Game(int name, BigDecimal multiplier){
        this.gameName = name;
        this.multiplier = multiplier;
    }
    public Game(String address, BigDecimal minBet,
            BigDecimal maxBet, int gameName,
            BigDecimal multiplier, int id){
        this.address = address;
        this.minBet = minBet;
        this.maxBet = maxBet;
        this.gameName = gameName;
        this.multiplier = multiplier;
        this.id = id;
    }

    /**
     * @return the gameName
     */
    public int getGameName() {
        return gameName;
    }

    /**
     * @param gameName the gameName to set
     */
    public void setGameName(int gameName) {
        this.gameName = gameName;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the minBet
     */
    public BigDecimal getMinBet() {
        return minBet;
    }

    /**
     * @param minBet the minBet to set
     */
    public void setMinBet(BigDecimal minBet) {
        this.minBet = minBet;
    }

    /**
     * @return the maxBet
     */
    public BigDecimal getMaxBet() {
        return maxBet;
    }

    /**
     * @param maxBet the maxBet to set
     */
    public void setMaxBet(BigDecimal maxBet) {
        this.maxBet = maxBet;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the multiplier
     */
    public BigDecimal getMultiplier() {
        return multiplier;
    }

    /**
     * @param multiplier the multiplier to set
     */
    public void setMultiplier(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }
}
