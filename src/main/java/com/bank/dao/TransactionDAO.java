package com.bank.dao;

import com.bank.exceptions.InsufficientBalanceException;
import com.bank.exceptions.NoSuchAccountException;
import com.bank.exceptions.NoSuchTransactionException;
import com.bank.exceptions.TransactionOnSameAccountException;
import com.bank.model.Transaction;
import java.util.List;

public interface TransactionDAO {

    Transaction get(long id) throws NoSuchTransactionException;

    long getTransactionId();

    Transaction processTransaction(Transaction transaction)
            throws NoSuchAccountException, InsufficientBalanceException, TransactionOnSameAccountException;

    List<Transaction> getAllTransactionsForAccount(long accountId) throws NoSuchAccountException;

}