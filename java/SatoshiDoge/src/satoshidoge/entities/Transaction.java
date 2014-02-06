package satoshidoge.entities;

import java.math.BigDecimal;

/**
 *
 * @author Devin
 */
public class Transaction {
    private String toAccount;
    private String address;
    private String category;
    private BigDecimal amount;
    private int confirmations;
    private String blockhash;
    private int blockindex;
    private String txid;
    private int time;
    private int game;
    
    public Transaction (
            String toAccount, String address, String category, 
            BigDecimal amount, int confirmations, String blockhash,
            int blockindex, String txid, int time){
        this.toAccount = toAccount;
        this.address = address;
        this.category = category;
        this.amount = amount;
        this.confirmations = confirmations;
        this.blockhash = blockhash;
        this.blockindex = blockindex;
        this.txid = txid;
        this.time = time;
    }
    
    //Alternate constructor for TransactionListener
    public Transaction (
            String toAccount, String address,
            String txid, BigDecimal amount, int time){
        this.toAccount = toAccount;
        this.address = address;
        this.amount = amount;
        this.txid = txid;
        this.time = time;
    }

    Transaction() {
    }
    /**
     * Returns a string representation of the Transaction Object
     * @return 
     */
    @Override
    public String toString(){
        return "toAccount: " + this.getToAccount() + "\n"
                + "address: " + this.getAddress() + "\n"
                + "category: " + this.getCategory() + "\n"
                + "amount: " + this.getAmount() + "\n"
                + "confirmations: " + this.getConfirmations() + "\n"
                + "blockhash: " + this.getBlockhash() + "\n"
                + "blockindex: " + this.getBlockindex() + "\n"
                + "txid: " + this.getTxid() + "\n"
                + "time: " + this.getTime() + "\n";
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
     * @return the toAccount
     */
    public String getToAccount() {
        return toAccount;
    }

    /**
     * @param toAccount the toAccount to set
     */
    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
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
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
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
     * @return the confirmations
     */
    public int getConfirmations() {
        return confirmations;
    }

    /**
     * @param confirmations the confirmations to set
     */
    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    /**
     * @return the blockhash
     */
    public String getBlockhash() {
        return blockhash;
    }

    /**
     * @param blockhash the blockhash to set
     */
    public void setBlockhash(String blockhash) {
        this.blockhash = blockhash;
    }

    /**
     * @return the blockindex
     */
    public int getBlockindex() {
        return blockindex;
    }

    /**
     * @param blockindex the blockindex to set
     */
    public void setBlockindex(int blockindex) {
        this.blockindex = blockindex;
    }

    /**
     * @return the time
     */
    public int getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * @return the game
     */
    public int getGame() {
        return game;
    }

    /**
     * @param game the game to set
     */
    public void setGame(int game) {
        this.game = game;
    }
}
