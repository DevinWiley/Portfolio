package satoshidoge.walletutil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import satoshidoge.entities.Transaction;
import satoshidoge.exceptions.TransactionNotFound;

/**
 *
 * @author Devin
 */
public interface DogeCoindAccess {
    public BigDecimal getBalanceByName(String name);
    public ArrayList<String> getAddressesByAccountName(String name);
    public ArrayList<Transaction> getTransactionsByAccountName(String name, Integer head);
    public JSONObject getRawTransaction(String txid);
    public String getRawTransactionHex(String txid);
    public String getReturnAddressByTXID(String txid);
    public String sendDogeToAddress(String fromAccount, String toAddress, BigDecimal amount);
    public ArrayList<Object> getDepositsByTXID(String txid);
    public JSONArray getTransactionsSinceBlock(String lastBlockProcessed);
    public String getNextBlockHash(String lastBlockProcessed);
    public long getBlockTime(String blockHash);
    public void setTXfee(BigDecimal amount);
    public JSONObject getUnspentByTXID(String txid) throws TransactionNotFound, JSONException;
    public JSONArray getConfirmedInputs(BigDecimal amount) throws JSONException;
    public JSONArray getConfirmedInputsIgnoring(String txid, BigDecimal amount) throws JSONException;
    public JSONArray getInputsIncluding(String txid, BigDecimal amount) throws TransactionNotFound, JSONException;
    public JSONArray listUnspent(int min, int max) throws JSONException;
    public JSONArray listUnspent(int min) throws JSONException;
    public String createRawTransaction(JSONArray txids, BigDecimal amount, String toAddress, String returnAddress) throws JSONException;
    public JSONObject signRawTransaction(String hex) throws JSONException;
    public String sendRawTransaction(String hex);
    public String sendRawTransaction(JSONObject transaction) throws JSONException;
    public String deployTransaction(BigDecimal amount, String toAddress, String returnAddress) throws JSONException;
    public String deployTransaction(String txid, BigDecimal amount, String toAddress, String returnAddress) throws JSONException, TransactionNotFound;
}
