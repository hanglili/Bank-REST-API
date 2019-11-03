package com.bank.dao;

import com.bank.exceptions.DuplicateAccountIdException;
import com.bank.exceptions.InsufficientBalanceException;
import com.bank.exceptions.NoSuchAccountException;
import com.bank.model.Account;

public interface AccountDAO {

    Account getAccount(long id) throws NoSuchAccountException;

    Account addAccount(long id, String name, double initialBalance) throws DuplicateAccountIdException;

    void withdraw(long accountId, double amount) throws NoSuchAccountException, InsufficientBalanceException;

    void deposit(long accountId, double amount) throws NoSuchAccountException;
}
