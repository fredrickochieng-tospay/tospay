package net.tospay.auth.interfaces;

public interface AccountType {

    int WALLET = 101;
    int MOBILE = 102;
    int BANK = 103;
    int CARD = 104;
    int TITLE = 105;

    int getType();

}
