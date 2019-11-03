package com.bank.exceptions;

import static com.bank.utils.Constants.NO_SUCH_ACCOUNT;

public class NoSuchAccountException extends Exception {
    public NoSuchAccountException(long accountId){
        super(NO_SUCH_ACCOUNT + accountId);
    }
}
