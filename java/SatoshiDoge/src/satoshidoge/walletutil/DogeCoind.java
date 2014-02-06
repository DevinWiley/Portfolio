package satoshidoge.walletutil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import satoshidoge.controllers.GameVerify;
import satoshidoge.entities.Transaction;
import satoshidoge.exceptions.TransactionNotFound;


/**
 *
 * @author Devin
 */
public class DogeCoind extends Exec implements DogeCoindAccess {
    private BigDecimal txFee = BigDecimal.valueOf(0.5);
    /**
     * Returns the balance for specified account name.
     * @param name
     * @return
     */
    @Override
    public BigDecimal getBalanceByName(String name) {
        String[] cmd = new String[]{"dogecoind","getbalance", name};
        String output = executeCommand(cmd);
        return BigDecimal.valueOf(Double.parseDouble(output));
    }
    /**
     * Gets Address associated with a given account name.
     * @param name
     * @return ArrayList containing addresses associated with an Account
     */
    @Override
    public ArrayList<String> getAddressesByAccountName(String name) {
        ArrayList<String> list = new ArrayList<>();
        String[] cmd = new String[]{"dogecoind", "getaddressesbyaccount", name};
        String output = executeCommand(cmd);
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
	Matcher matches = pattern.matcher(output);
        while (matches.find()){
            list.add(matches.group(1));
        }
        list.trimToSize();
        return list;
    }
    /**
     * Gets Transactions for a given account name.
     * @param name
     * @param head 
     * @return ArrayList containing to most recent transactions
     */
    @Override
    public ArrayList<Transaction> getTransactionsByAccountName(String name, Integer head) {
        ArrayList<Transaction> list = new ArrayList<>();
        String[] cmd = new String[]{"dogecoind", "listtransactions", name, head.toString()};
        String output = executeCommand(cmd);
        try {
            JSONArray jsonArray = new JSONArray(output);
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.length() >= 9){
                    Transaction transaction = new Transaction(
                            (String)jsonObject.get("account"),
                            (String)jsonObject.get("address"),
                            (String)jsonObject.get("category"),
                            (BigDecimal)jsonObject.get("amount"),
                            (Integer)jsonObject.get("confirmations"),
                            (String)jsonObject.get("blockhash"),
                            (Integer)jsonObject.get("blockindex"), 
                            (String)jsonObject.get("txid"),
                            (Integer)jsonObject.get("time"));
                    list.add(transaction);
                }
            }
        } catch (JSONException ex) {
            Logger.getLogger(DogeCoind.class.getName()).log(Level.SEVERE, null, ex);
        }
        list.trimToSize();
        return list;
    }
    /**
     * Retrieves the transaction information contained in an JSON Object
     * @param txid
     * @return JSONObject containing a Raw Transaction
     */
    @Override
    public JSONObject getRawTransaction(String txid) {
        String[] cmd = new String[]{"dogecoind", "getrawtransaction", txid, "1"};
        String output = executeCommand(cmd);
        JSONObject jsonObject = null;
        try {        
            jsonObject = new JSONObject(output);
        } catch (JSONException ex) {
            Logger.getLogger(DogeCoind.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonObject;
    }
    @Override
    public String getRawTransactionHex(String txid) {
        String[] cmd = new String[]{"dogecoind", "getrawtransaction", txid};
        String output = executeCommand(cmd);
        return output;
    }
    /**
     * Returns the return address for a transaction. Return address can be
     * provided by sending .00054321 Doge to an additional recipient when 
     * playing the game
     * @param txid
     * @return the return address for a transaction
     */
    @Override
    public String getReturnAddressByTXID(String txid) {
	JSONObject firstTransaction = getRawTransaction(txid);
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        String address = null;
        try {
            JSONArray testGivenAddress = firstTransaction.getJSONArray("vout");
            //Test outputs, if output recieved value of .00054321 Doge then that
            //is the assumed return address as per the website rules
            for (int i = 0; i < testGivenAddress.length(); i++){
                JSONObject output = testGivenAddress.getJSONObject(i);
                if (Math.abs(1.2345 - output.getDouble("value")) < .00000001){
                    String addresses = output.getJSONObject("scriptPubKey").getString("addresses");
                    Matcher matches = pattern.matcher(addresses);
                    matches.find();
                    address = matches.group(1);
                    return address;
                }
            }
            JSONArray vin = firstTransaction.getJSONArray("vin");
            int correctVOUT = vin.getJSONObject(0).getInt("vout");
	    String prevTXID = vin.getJSONObject(0).getString("txid");
	    JSONObject prevTransaction = getRawTransaction(prevTXID);
	    JSONArray vout = prevTransaction.getJSONArray("vout");
	    JSONObject jsonAddress = vout.getJSONObject(correctVOUT).getJSONObject("scriptPubKey");
	    String addresses = jsonAddress.getString("addresses");
	    Matcher matches = pattern.matcher(addresses);
	    matches.find();
            address = matches.group(1);
        } catch (JSONException ex) {
            Logger.getLogger(DogeCoind.class.getName()).log(Level.SEVERE, null, ex);
        }
        return address;
    }

    @Override
    public String sendDogeToAddress(String fromAccount, String toAddress, BigDecimal amount) {
        String[] cmd = new String[]{"dogecoind", "sendfrom", fromAccount, toAddress, amount.toString()};
        String output = executeCommand(cmd);
        return output;
    }
    /**
     * Handles multiple recipients per transaction id.
     * @param txid
     * @return Array list with even entries being the address as String, and 
     * address+1 being the amount sent to that address as Double.
     */
    @Override
    public ArrayList<Object> getDepositsByTXID(String txid) {
        ArrayList<Object> deposits = new ArrayList<>();
        JSONObject transaction = getRawTransaction(txid);
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        try {
            JSONArray vout = transaction.getJSONArray("vout");
            for (int i = 0; i < vout.length(); i++){
                JSONObject output = vout.getJSONObject(i);
                String addresses = output.getJSONObject("scriptPubKey").getString("addresses");
                Matcher matches = pattern.matcher(addresses);
                matches.find();
                String address = matches.group(1);
                if (new GameVerify().validGameAddress(address)){
                    deposits.add(address);
                    deposits.add((Double)output.getDouble("value"));
                }
            }
        } catch (JSONException ex) {
            Logger.getLogger(DogeCoind.class.getName()).log(Level.SEVERE, null, ex);
        }
        deposits.trimToSize();
        return deposits;
    }
    /**
     * Retrieves all the transactions since passed block
     * @param blockHash
     * @return JSONArray containing transactions
     */
    @Override
    public JSONArray getTransactionsSinceBlock(String blockHash) {
        String[] cmd = new String[]{"dogecoind","listsinceblock", blockHash};
        JSONObject output;
        JSONArray transactions = null;
        try {
            output = new JSONObject(executeCommand(cmd));
            transactions = output.getJSONArray("transactions");
        } catch (JSONException ex) {
            Logger.getLogger(DogeCoind.class.getName()).log(Level.SEVERE, null, ex);
        }
        return transactions;
    }
    
    /**
     * Parses the current block hash and returns the block hash for the next
     * block on the chain
     * @param lastBlockProcessed
     * @return Block Hash for height lastBlockProcessed + 1
     */
    @Override
    public String getNextBlockHash(String lastBlockProcessed){
        String[] cmd = new String[]{"dogecoind", "getblock", lastBlockProcessed};
        String output = executeCommand(cmd);
        String nextBlockHash;
        try {
            JSONObject jsonObject = new JSONObject(output);
            nextBlockHash = jsonObject.getString("nextblockhash");
        } catch (JSONException ex) {
            nextBlockHash = null;
        }
        return nextBlockHash;
    }
    @Override
    public long getBlockTime(String blockHash){
        String[] cmd = new String[]{"dogecoind", "getblock", blockHash};
        String output = executeCommand(cmd);
        long time;
        try {
            JSONObject jsonObject = new JSONObject(output);
            time = jsonObject.getLong("time");
        } catch (JSONException ex) {
            time = 0;
        }
        return time;
    }
    /**
     * Sets the transaction fee for all transfers using sendAmount
     * @param amount
     */
    @Override
    public void setTXfee(BigDecimal amount) {
        String[] cmd = new String[]{"dogecoind", "settxfee", amount.toString()};
        executeCommand(cmd);
    }

    /**
     * Searches the unspent transactions for the passed txid.
     * @param txid
     * @return JSONObject representation of the unspent transaction
     * @throws TransactionNotFound
     * @throws JSONException
     */
    @Override
    public JSONObject getUnspentByTXID(String txid) throws TransactionNotFound, JSONException{
        JSONArray unspentTransactions = listUnspent(0, 999999);
        JSONObject correctTransaction = null;
        for (int i = 0; i < unspentTransactions.length(); i++){
            JSONObject testObject = unspentTransactions.getJSONObject(i);
            if (testObject.getString("txid").equals(txid)){
                correctTransaction = testObject;
            }
        }
        if (correctTransaction == null){
            throw new TransactionNotFound("TXID: " + txid + " not found in "
                    + "unspent Transactions");
        }
        return correctTransaction;
    }
    
    /**
     * Gathers an array of unspent transactions to be used for input in a 
     * raw transaction. Confirmed inputs have at least 3 confirmations.
     * @param amount
     * @return array of inputs for a raw transaction
     * @throws JSONException
     */
    @Override
    public JSONArray getConfirmedInputs(BigDecimal amount) throws JSONException{
        JSONArray unspentTransaction = listUnspent(3, 999999);
        ArrayList<Object> objectArray = new ArrayList<>();
        JSONObject temp;
        BigDecimal gathered = BigDecimal.valueOf(0d);
        for (int i = 0; i < unspentTransaction.length() && gathered.compareTo(amount) == -1; i++){
            temp = unspentTransaction.getJSONObject(i);
            gathered = gathered.add(BigDecimal.valueOf(temp.getDouble("amount")));
            objectArray.add(temp);
            
        }
        JSONArray jsonArray = new JSONArray(objectArray);
        return jsonArray;
    }
    
    /**
     * Gathers an array of unspent transactions to be used for input in a 
     * raw transaction. Confirmed inputs have at least 3 confirmations.
     * Ignores the txid passed as it's assumed its already included.
     * @param txid
     * @param amount 
     * @return array of inputs for a raw transaction - txid
     * @throws JSONException
     */
    @Override
    public JSONArray getConfirmedInputsIgnoring(String txid, BigDecimal amount) throws JSONException{
        JSONArray unspentTransaction = listUnspent(3, 999999);
        ArrayList<Object> objectArray = new ArrayList<>();
        JSONObject temp;
        BigDecimal gathered = BigDecimal.valueOf(0d);
        for (int i = 0; i < unspentTransaction.length() && gathered.compareTo(amount) == -1; i++){
            temp = unspentTransaction.getJSONObject(i);
            if (!temp.getString("txid").equals(txid)){
                gathered = gathered.add(BigDecimal.valueOf(temp.getDouble("amount")));
                objectArray.add(temp);
            }
        }
        JSONArray jsonArray = new JSONArray(objectArray);
        return jsonArray;
    }

    /**
     * Creates a JSONArray for a raw transaction including the txid passed.
     * @param txid
     * @param amount
     * @return array of inputs including txid.
     * @throws TransactionNotFound
     * @throws JSONException
     */
    @Override
    public JSONArray getInputsIncluding(String txid, BigDecimal amount) throws TransactionNotFound, JSONException {
        JSONObject targetTransaction = getUnspentByTXID(txid);
        JSONArray unspent = new JSONArray();
        BigDecimal total = BigDecimal.valueOf(0d);
        total = total.add(BigDecimal.valueOf(targetTransaction.getDouble("amount")));
        if (total.compareTo(amount) < 0){
            BigDecimal remaining = amount.subtract(total);
            unspent = getConfirmedInputsIgnoring(txid, remaining);
        }
        unspent.put(targetTransaction);
        return unspent;
    }
    
    /**
     * Lists unspent transactions with specified min/max confirmations.
     * @param min
     * @param max
     * @return
     * @throws JSONException
     */
    @Override
    public JSONArray listUnspent(int min, int max) throws JSONException{
        String[] cmd = new String[]{"dogecoind", "listunspent", "" + min, "" + max};
        JSONArray unspentTransactions = new JSONArray(executeCommand(cmd));
        return unspentTransactions;
    }

    /**
     * List unspent transactions with specified min/999999 confirmations
     * @param min
     * @return
     * @throws JSONException
     */
    @Override
    public JSONArray listUnspent(int min) throws JSONException{
        return listUnspent(min, 999999);
    }

    /**
     * Creates raw transaction sending amount specified to toAddress
     * using supplied inputs.
     * Excess funds from inputs will be returned to returnAddress
     * @param txids
     * @param amount
     * @param toAddress
     * @param returnAddress
     * @return Hex representing raw transaction
     * @throws JSONException
     */
    @Override
    public String createRawTransaction(JSONArray txids, BigDecimal amount, String toAddress, String returnAddress) throws JSONException{
        StringBuilder builder = new StringBuilder();
        BigDecimal total = BigDecimal.valueOf(0d);
        builder.append("[");
        JSONObject currentTransaction = null;
        for (int i = 0; i < txids.length(); i++){
            currentTransaction = txids.getJSONObject(i);
            String currentTXID = currentTransaction.getString("txid");
            total = total.add(BigDecimal.valueOf(currentTransaction.getDouble("amount")));
            int currentVOUT = currentTransaction.getInt("vout");
            if (i > 0){ 
                builder.append(",");
            }
            builder.append("{\"txid\":\"")
                    .append(currentTXID)
                    .append("\"," + "\"vout\":")
                    .append(currentVOUT)
                    .append("}");
        }
        builder.append("]");
        BigDecimal weKeep = total.subtract(amount);
        weKeep = weKeep.subtract(txFee);
	StringBuilder builder2 = new StringBuilder();
        builder2.append("{")
                .append("\"").append(toAddress).append("\":")
                .append(amount).append(",")
                .append("\"").append(returnAddress).append("\":")
                .append(weKeep).append("}");
	String object1 = builder.toString();
	String object2 = builder2.toString();
	//System.out.println("object1: " + object1 + "\n\nObject2: " + object2);
        String[] cmd = new String[]{"dogecoind", "createrawtransaction", object1, object2};
	//System.out.println(cmd[0] + cmd[1] + cmd[2] + cmd[3]);
        String testJSON = executeCommand(cmd);
        //System.out.println(testJSON + "\n\n");
        return testJSON;
    }

    /**
     * Signs a raw transaction, preparing it to be sent
     * @param hex
     * @return JSONObject with signed raw transaction hex.
     * @throws JSONException
     */
    @Override
    public JSONObject signRawTransaction(String hex) throws JSONException{
        String[] cmd = new String[]{"dogecoind", "signrawtransaction", hex};
        String jsonTest = executeCommand(cmd);
        JSONObject signedTransaction = new JSONObject(jsonTest);
        return signedTransaction;
    }
    
    /**
     * Sends signed raw transaction to the network.
     * @param hex
     * @return txid for sent transaction
     */
    @Override
    public String sendRawTransaction(String hex) {
        String[] cmd = new String[]{"dogecoind", "sendrawtransaction", hex};
        String txid = executeCommand(cmd);
        return txid;
    }

    /**
     * Sends signed raw transaction to the network.
     * @param transaction
     * @return txid for sent transaction
     * @throws JSONException
     */
    @Override
    public String sendRawTransaction(JSONObject transaction) throws JSONException {
        String hex = transaction.getString("hex");
        String txid = sendRawTransaction(hex);
        return txid;
    }

    /**
     * Creates, signs, and sends a raw transaction to specified toAddress, sending
     * amount.
     * Excess funds are returned to returnAddress.
     * @param amount
     * @param toAddress
     * @param returnAddress
     * @return txid for sent transaction
     * @throws JSONException
     */
    @Override
    public String deployTransaction(BigDecimal amount, String toAddress, String returnAddress) throws JSONException {
        amount = amount.add(txFee);
        JSONArray unspents = getConfirmedInputs(amount);
        amount = amount.subtract(txFee);
        String rawtransactionHex = createRawTransaction(unspents, amount, toAddress, returnAddress);
        JSONObject signedTransaction = signRawTransaction(rawtransactionHex);
        String out_txid = sendRawTransaction(signedTransaction.getString("hex"));
        return out_txid;
    }

    /**
     * Creates, signs, and sends a raw transaction to specified toAddress, sending
     * amount. Inputs will include transaction passed.
     * Excess funds are returned to returnAddress
     * @param txid
     * @param amount
     * @param toAddress
     * @param returnAddress
     * @return txid for sent transaction
     * @throws JSONException
     * @throws TransactionNotFound
     */
    @Override
    public String deployTransaction(String txid, BigDecimal amount, String toAddress, String returnAddress) throws JSONException, TransactionNotFound {
        amount = amount.add(txFee);
        JSONArray unspents = getInputsIncluding(txid, amount);
        amount = amount.subtract(txFee);
        String rawtransactionHex = createRawTransaction(unspents, amount, toAddress, returnAddress);
	//System.out.println("rawtransactionHex: " + rawtransactionHex);
        JSONObject signedTransaction = signRawTransaction(rawtransactionHex);
        String out_txid = sendRawTransaction(signedTransaction.getString("hex"));
        return out_txid;
    }
}
