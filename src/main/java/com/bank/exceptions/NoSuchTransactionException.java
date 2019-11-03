package com.bank.exceptions;

import static com.bank.utils.Constants.NO_SUCH_TRANSACTION;

public class NoSuchTransactionException extends Exception {
    public NoSuchTransactionException(long transactionId){
        super(NO_SUCH_TRANSACTION + transactionId);
    }
}
