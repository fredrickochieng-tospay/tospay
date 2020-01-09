package net.tospay.auth.ui.account;

import net.tospay.auth.interfaces.AccountType;

public interface OnAccountItemClickListener {

    void onTopupClick(AccountType accountType);

    void onAccountSelectedListener(AccountType accountType);

    void onVerifyClick(AccountType accountType);

}
