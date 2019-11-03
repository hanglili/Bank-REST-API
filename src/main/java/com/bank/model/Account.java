package com.bank.model;

public class Account {

    private long id;
    private String name;
    private double balance;
    private final Object lock = new Object();

    public Account() {
        // Jackson constructor
    }

    public Account(long id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Object getLock() {
        return lock;
    }
}
