package net.tospay.auth.model;

import net.tospay.auth.interfaces.AccountType;

public class AccountTitle implements AccountType {

    private String name;
    private int accountType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    @Override
    public int getType() {
        return AccountType.TITLE;
    }

    @Override
    public String toString() {
        return "AccountTitle{" +
                "name='" + name + '\'' +
                ", accountType=" + accountType +
                '}';
    }
}
