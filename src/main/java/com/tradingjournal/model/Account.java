package com.tradingjournal.model;

import java.util.Objects;

/**
 * Represents a trading account with a starting balance
 */
public class Account implements Comparable<Account> {
    private String name;
    private double startingBalance;
    private String description;

    public Account() {
    }

    public Account(String name, double startingBalance) {
        this.name = name;
        this.startingBalance = startingBalance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getStartingBalance() {
        return startingBalance;
    }

    public void setStartingBalance(double startingBalance) {
        this.startingBalance = startingBalance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Account account = (Account) o;
        return Objects.equals(name, account.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Account o) {
        return this.name.compareToIgnoreCase(o.name);
    }
}
