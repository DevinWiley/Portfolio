/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package satoshidoge.entities;

import java.math.BigDecimal;

/**
 *
 * @author Devin
 */
public class GameResult {
    private String txid;
    private int result;
    private int gameName;
    private BigDecimal amount;
    private BigDecimal profit;
    private BigDecimal gains;
    private String returnAddress;
    private int lucky;
    private long time;
    
    public GameResult(String txid, int result, int gameName,
            BigDecimal amount, BigDecimal profit, BigDecimal gains,
            String returnAddress, int lucky, long time){
        this.txid = txid;
        this.result = result;
        this.gameName = gameName;
        this.amount = amount;
        this.profit = profit;
        this.gains = gains;
        this.returnAddress = returnAddress;
        this.lucky = lucky;
        this.time = time;
    }

    /**
     * @return the txid
     */
    public String getTxid() {
        return txid;
    }

    /**
     * @param txid the txid to set
     */
    public void setTxid(String txid) {
        this.txid = txid;
    }

    /**
     * @return the result
     */
    public int getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(int result) {
        this.result = result;
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
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return the profit
     */
    public BigDecimal getProfit() {
        return profit;
    }

    /**
     * @param profit the profit to set
     */
    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    /**
     * @return the gains
     */
    public BigDecimal getGains() {
        return gains;
    }

    /**
     * @param gains the gains to set
     */
    public void setGains(BigDecimal gains) {
        this.gains = gains;
    }

    /**
     * @return the returnAddress
     */
    public String getReturnAddress() {
        return returnAddress;
    }

    /**
     * @param returnAddress the returnAddress to set
     */
    public void setReturnAddress(String returnAddress) {
        this.returnAddress = returnAddress;
    }

    /**
     * @return the lucky
     */
    public int getLucky() {
        return lucky;
    }

    /**
     * @param lucky the lucky to set
     */
    public void setLucky(int lucky) {
        this.lucky = lucky;
    }

    /**
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(long time) {
        this.time = time;
    }
}
