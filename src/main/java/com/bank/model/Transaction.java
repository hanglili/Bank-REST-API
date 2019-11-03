package com.bank.model;

import io.dropwizard.jackson.JsonSnakeCase;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

@JsonSnakeCase
public class Transaction {

    private long id;

    @NotEmpty
    private long sourceAccountId;

    @NotEmpty
    private long destinationAccountId;

    @NotNull
    @Min(1)
    private double amount;

    public Transaction() {
        // Jackson constructor
    }

    public Transaction(long id, long sourceAccountId, long destinationAccountId, double amount) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public long getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(long destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setAmount(String amount) {
        try {
            this.amount = Integer.parseInt(amount);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("amount must be an integer value");
        }
    }

}
