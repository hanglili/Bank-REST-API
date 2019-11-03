package com.bank.exceptions;

import static com.bank.utils.Constants.INSUFFICIENT_BALANCE;

public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(long accountId){
        super(INSUFFICIENT_BALANCE + accountId);
    }
}
