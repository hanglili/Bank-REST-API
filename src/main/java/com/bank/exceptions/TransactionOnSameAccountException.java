package com.bank.exceptions;

public class TransactionOnSameAccountException extends Exception {
    public TransactionOnSameAccountException(long accountId){
        super("Transaction between two accounts that are the same: " + accountId);
    }
}
