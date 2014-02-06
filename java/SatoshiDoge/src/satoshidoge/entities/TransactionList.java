/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package satoshidoge.entities;

import java.util.LinkedList;
import satoshidoge.exceptions.InvalidListOperation;

/**
 *
 * @author Devin
 */
public class TransactionList {
    LinkedList<Transaction> transactionsByTXID;
    private final String txid;
    private int length;
    
    public TransactionList(String txid) {
        this.transactionsByTXID = new LinkedList<>();
        this.txid = txid;
        length = 0;
    }
    public void add(Transaction transaction) throws InvalidListOperation{
        if (!transaction.getTxid().equals(txid)){
            throw new InvalidListOperation("Cannot add " + transaction.getTxid()
            + " when list is defined for " + txid);
        } else {
            transactionsByTXID.add(transaction);
            length++;
        }
    }
    public int length(){
	//System.out.println("Transaction Length(): " + length);
        return this.length;
    }
    public Transaction get(int i){
        return transactionsByTXID.get(i);
    }
}
