package net.tospay.auth.model;

import net.tospay.auth.interfaces.AccountType;

public class AccountTitle implements AccountType {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getType() {
        return AccountType.TITLE;
    }
}
