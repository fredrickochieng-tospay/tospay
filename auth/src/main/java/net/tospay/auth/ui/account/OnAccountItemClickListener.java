package net.tospay.auth.ui.account;

import android.view.View;

import net.tospay.auth.interfaces.AccountType;

public interface OnAccountItemClickListener {

    default void onAccountType(AccountType accountType) {

    }

    void onVerifyClick(View view, AccountType accountType);

    void onAddAccount(int accountType);
}
