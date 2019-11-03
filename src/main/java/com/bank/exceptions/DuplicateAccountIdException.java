package com.bank.exceptions;

import static com.bank.utils.Constants.ACCOUNT_ID_ALREADY_EXISTS;

public class DuplicateAccountIdException extends Exception {
    public DuplicateAccountIdException(long accountId){
        super(ACCOUNT_ID_ALREADY_EXISTS + accountId);
    }
}
