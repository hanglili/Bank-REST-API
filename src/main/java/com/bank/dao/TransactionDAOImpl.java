package com.bank.dao;

import com.bank.exceptions.InsufficientBalanceException;
import com.bank.exceptions.NoSuchAccountException;
import com.bank.exceptions.NoSuchTransactionException;
import com.bank.exceptions.TransactionOnSameAccountException;
import com.bank.model.Account;
import com.bank.model.Transaction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionDAOImpl implements TransactionDAO {

    private static TransactionDAOImpl instance;
    private ConcurrentHashMap<Long, Transaction> transactionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, List<Transaction>> accountTransactionMap = new ConcurrentHashMap<>();
    private AccountDAOImpl accountService = AccountDAOImpl.getInstance();

    private TransactionDAOImpl(){}

    public static TransactionDAOImpl getInstance(){
        if(instance == null){
            synchronized (TransactionDAOImpl.class) {
                if(instance == null) {
                    instance = new TransactionDAOImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public Transaction get(long transactionId) throws NoSuchTransactionException {
        if (transactionMap.containsKey(transactionId)) {
            return transactionMap.get(transactionId);
        } else {
            throw new NoSuchTransactionException(transactionId);
        }
    }

    @Override
    public List<Transaction> getAllTransactionsForAccount(long accountId)
            throws NoSuchAccountException {
        accountService.getAccount(accountId);
        return accountTransactionMap.getOrDefault(accountId, new ArrayList<>());
    }

    @Override
    public Transaction processTransaction(Transaction transaction)
            throws NoSuchAccountException, InsufficientBalanceException, TransactionOnSameAccountException {
        Account senderAccount =  accountService.getAccount(transaction.getSourceAccountId());
        Account recipientAccount = accountService.getAccount(transaction.getDestinationAccountId());

        if (transaction.getSourceAccountId() == transaction.getDestinationAccountId()) {
            throw new TransactionOnSameAccountException(transaction.getSourceAccountId());
        }

        if (senderAccount == null) {
            throw new NoSuchAccountException(transaction.getSourceAccountId());
        }

        if (recipientAccount == null) {
            throw new NoSuchAccountException(transaction.getDestinationAccountId());
        }

        if (senderAccount.getBalance() < transaction.getAmount()) {
            throw new InsufficientBalanceException(senderAccount.getId());
        }

        Object innerLock, outerLock;
        if (senderAccount.getId() > recipientAccount.getId()){
            innerLock = senderAccount.getLock();
            outerLock = recipientAccount.getLock();
        } else{
            innerLock = recipientAccount.getLock();
            outerLock = senderAccount.getLock();
        }

        /*
         * Locks are sorted according to account's id's, so that these accounts
         * are always locked and unlocked in the same order to avoid deadlock.
         */
        synchronized (outerLock){
            synchronized (innerLock){
                accountService.withdraw(transaction.getSourceAccountId(), transaction.getAmount());
                try {
                    accountService.deposit(transaction.getDestinationAccountId(), transaction.getAmount());
                } catch (Exception e){
                    accountService.deposit(transaction.getSourceAccountId(),transaction.getAmount());
                    throw e;
                }
            }
        }

        transactionMap.put(transaction.getId(), transaction);

        List<Transaction> senderTransactions = accountTransactionMap.getOrDefault(transaction.getSourceAccountId(),
                new ArrayList<>());
        senderTransactions.add(transaction);
        accountTransactionMap.put(transaction.getSourceAccountId(), senderTransactions);

        List<Transaction> recipientTransactions = accountTransactionMap.getOrDefault(transaction.getDestinationAccountId(),
                new ArrayList<>());
        recipientTransactions.add(transaction);
        accountTransactionMap.put(transaction.getDestinationAccountId(), recipientTransactions);

        return transaction;
    }
}
