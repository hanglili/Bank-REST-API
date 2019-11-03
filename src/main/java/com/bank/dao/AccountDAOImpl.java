package com.bank.dao;

import com.bank.exceptions.DuplicateAccountIdException;
import com.bank.exceptions.InsufficientBalanceException;
import com.bank.exceptions.NoSuchAccountException;
import com.bank.model.Account;
import java.util.concurrent.ConcurrentHashMap;

public class AccountDAOImpl implements AccountDAO {

    private static AccountDAOImpl instance;
    private ConcurrentHashMap<Long, Account> accountMap = new ConcurrentHashMap<>();
    private static final Object newAccountLock = new Object();

    private AccountDAOImpl(){}

    public static AccountDAOImpl getInstance(){
        if (instance == null){
            synchronized (AccountDAOImpl.class) {
                if(instance == null) {
                    instance = new AccountDAOImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public Account addAccount(long id, String name, double initialBalance) throws DuplicateAccountIdException {
        if (accountMap.containsKey(id)){
            throw new DuplicateAccountIdException(id);
        } else {
            synchronized (newAccountLock) {
                if (accountMap.containsKey(id)){
                    throw new DuplicateAccountIdException(id);
                } else{
                    Account account = new Account(id, name, initialBalance);
                    accountMap.put(account.getId(), account);
                    return account;
                }
            }
        }
    }

    @Override
    public Account getAccount(long accountId) throws NoSuchAccountException {
        if (accountMap.containsKey(accountId)) {
            return accountMap.get(accountId);
        } else {
            throw new NoSuchAccountException(accountId);
        }
    }

    @Override
    public void withdraw(long accountId, double amount) throws NoSuchAccountException, InsufficientBalanceException {
        if (!accountMap.containsKey(accountId)) {
            throw new NoSuchAccountException(accountId);
        } else if(amount > accountMap.get(accountId).getBalance()) {
            throw new InsufficientBalanceException(accountId);
        } else{
            if (amount > accountMap.get(accountId).getBalance()) {
                throw new InsufficientBalanceException(accountId);
            } else{
                Account account = accountMap.get(accountId);
                account.setBalance(account.getBalance() - amount);
                accountMap.put(accountId,account);
            }
        }
    }

    @Override
    public void deposit(long accountId, double amount) throws NoSuchAccountException {
        if (!accountMap.containsKey(accountId)) {
            throw new NoSuchAccountException(accountId);
        } else {
            Account account = accountMap.get(accountId);
            account.setBalance(account.getBalance() + amount);
            accountMap.put(accountId,account);
        }
    }
}
