package net.tospay.auth.interfaces;

public interface AccountType {

    int WALLET = 1;
    int MOBILE = 2;
    int BANK = 3;
    int CARD = 4;

    int getType();

}
